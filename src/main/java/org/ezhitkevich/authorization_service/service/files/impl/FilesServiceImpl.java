package org.ezhitkevich.authorization_service.service.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.exception.NoFilesFoundException;
import org.ezhitkevich.authorization_service.model.FileMetadata;
import org.ezhitkevich.authorization_service.model.MinioFile;
import org.ezhitkevich.authorization_service.model.User;
import org.ezhitkevich.authorization_service.repository.FileMetadataRepository;
import org.ezhitkevich.authorization_service.service.files.FilesService;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.ezhitkevich.authorization_service.service.security.UserService;
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

        FileMetadata fileMetadata = minioService.uploadFile(bucketName, filename, minioFile);
        User user = userService.findUserByLogin(bucketName);
        fileMetadata.setUser(user);
        fileRepository.save(fileMetadata);

        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    @Transactional
    public void deleteFile(String bucketName, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        if (!fileRepository.findByUserAndFilename(bucketName, filename)) {
            throw new NoFilesFoundException(bucketName);
        }

        minioService.deleteFile(bucketName, filename);
        fileRepository.deleteByFilenameAndExtension(getFilenameFromFullFileName(filename),
                getExtensionFromFullFilename(filename));

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

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }

    private String getFilenameFromFullFileName(String fullFilename) {
        return fullFilename.substring(0, fullFilename.lastIndexOf('.'));
    }

    private String getExtensionFromFullFilename(String fullFilename) {
        return fullFilename.substring(fullFilename.lastIndexOf('.'));
    }
}
