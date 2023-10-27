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

    private static final String URL = "http:\\localhost:8080\\cloud\\file";

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
    public MinioFile getFile(String bucketName, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(bucketName, filename)) {
            throw new NoFilesFoundException(bucketName);
        }
        MinioFile file = minioService.getFile(bucketName, filename);

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return file;
    }

    @Override
    @Transactional
    public void uploadFile(String bucketName, String filename, MinioFile minioFile) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        if (!minioService.isBucketExist(bucketName)) {
            minioService.createBucket(bucketName);
        }

      FileMetadata  fileMetadata = FileMetadata.builder()
                .filename(getFilenameFromFullFileName(filename))
                .extension(getExtensionFromFullFilename(filename))
                .url(getPreSignedUrl(filename))
                .hash(minioFile.getHash())
                .build();

        User user = userService.findUserByLogin(bucketName);
        fileMetadata.setUser(user);
        fileRepository.save(fileMetadata);
        minioService.uploadFile(bucketName, filename, minioFile);

        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    @Transactional
    public void deleteFile(String bucketName, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(bucketName, filename)) {
            throw new NoFilesFoundException(bucketName);
        }
        fileRepository.deleteByFilenameAndExtension(getFilenameFromFullFileName(filename),
                getExtensionFromFullFilename(filename));
        minioService.deleteFile(bucketName, filename);

        log.info("Method delete file in class {} finished", getClass().getSimpleName());
    }

    @Override
    @Transactional
    public void renameFile(String bucketName, String oldFilename, String newFilename) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(bucketName, oldFilename)) {
            throw new NoFilesFoundException(bucketName);
        }

        String shortOldFileName = getFilenameFromFullFileName(oldFilename);
        String shortNewFilename = getExtensionFromFullFilename(newFilename);

        fileRepository.updateByFilename(shortOldFileName, shortNewFilename);
        minioService.renameFile(bucketName, oldFilename, newFilename);

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }

    private String getFilenameFromFullFileName(String fullFilename) {
        return fullFilename.substring(0, fullFilename.lastIndexOf('.'));
    }

    private String getExtensionFromFullFilename(String fullFilename) {
        return fullFilename.substring(fullFilename.lastIndexOf('.'));
    }

    private String getPreSignedUrl(String filename) {
        return URL.concat(filename);
    }
}
