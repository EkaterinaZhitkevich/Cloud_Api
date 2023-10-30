package org.ezhitkevich.cloud_api.service.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.exception.NoFilesFoundException;
import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.repository.FileMetadataRepository;
import org.ezhitkevich.cloud_api.service.files.FilesService;
import org.ezhitkevich.cloud_api.service.files.MinioService;
import org.ezhitkevich.cloud_api.service.security.UserService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesServiceImpl implements FilesService {

    private final FileMetadataRepository fileRepository;

    private final MinioService minioService;

    private final UserService userService;

    private static final String PRE_SIGNED_URL = "http:/localhost:8080/cloud/file/";

    @Override
    @Transactional
    public List<FileMetadata> getAllFilesLimit(String username, Integer limit) {
        log.info("Method getAllFilesLimit in class {} started", getClass().getSimpleName());

        List<FileMetadata> files = fileRepository.findAllFilesByUsername(username);
        if (files.isEmpty()) {
            throw new NoFilesFoundException(username);
        }
        List<FileMetadata> fileMetadataList = files.stream()
                .limit(limit).toList();

        log.info("Method getAllFilesLimit in class {} finished", getClass().getSimpleName());
        return fileMetadataList;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public MinioFile getFile(String username, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(username, filename)) {
            throw new NoFilesFoundException(username);
        }
        MinioFile file = minioService.getFile(username, filename);

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return file;
    }

    @Override
    @Transactional(rollbackFor = IOException.class)
    public void uploadFile(String username, String filename, MinioFile minioFile) throws IOException{
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        if (!minioService.isBucketExist(username)) {
            minioService.createBucket(username);
        }

        FileMetadata fileMetadata = FileMetadata.builder()
                .filename(getFilenameFromFullFileName(filename))
                .extension(getExtensionFromFullFilename(filename))
                .url(getPreSignedUrl(filename))
                .size(minioFile.getResource().contentLength())
                .hash(minioFile.getHash())
                .build();
        User user = userService.findUserByLogin(username);
        fileMetadata.setUser(user);
        fileRepository.save(fileMetadata);
        minioService.uploadFile(username, filename, minioFile);

        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    @Transactional
    public void deleteFile(String username, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(username, filename)) {
            throw new NoFilesFoundException(username);
        }

        fileRepository.deleteByFilenameAndExtension(getFilenameFromFullFileName(filename),
                getExtensionFromFullFilename(filename));
        minioService.deleteFile(username, filename);

        log.info("Method delete file in class {} finished", getClass().getSimpleName());
    }

    @Override
    @Transactional
    public void renameFile(String username, String oldFilename, String newFilename) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        String shortOldFileName = getFilenameFromFullFileName(oldFilename);
        String shortNewFilename = getExtensionFromFullFilename(newFilename);
        FileMetadata fileMetadata = fileRepository.findByUsernameAndFilename(username, shortOldFileName)
                .orElseThrow(() -> new NoFilesFoundException(username));
        fileRepository.updateByFilename(shortNewFilename, fileMetadata.getId());
        minioService.renameFile(username, oldFilename, newFilename);

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }

    private String getFilenameFromFullFileName(String fullFilename) {
        return fullFilename.substring(0, fullFilename.lastIndexOf('.'));
    }

    private String getExtensionFromFullFilename(String fullFilename) {
        return fullFilename.substring(fullFilename.lastIndexOf('.'));
    }

    private final String getPreSignedUrl(String filename) {
        return PRE_SIGNED_URL.concat(filename);
    }
}
