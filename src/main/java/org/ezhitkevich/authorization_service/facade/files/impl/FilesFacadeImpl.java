package org.ezhitkevich.authorization_service.facade.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.ezhitkevich.authorization_service.exception.NoFilesFoundException;
import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesFacadeImpl implements FilesFacade {

    private final MinioService minioService;

    @Override
    public List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit) {
        log.info("Method get all files limit in class {} started", getClass().getSimpleName());

        if (!minioService.isBucketExist(userLogin)){
            throw new NoFilesFoundException(userLogin);
        }
        List<ListFileResponseDto> filesLimit = minioService.getAllFilesLimit(userLogin, limit);

        log.info("Method get all files limit in class {} finished", getClass().getSimpleName());
        return filesLimit;
    }

    @Override
    public FileDto getFile(String userLogin, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        if (!minioService.isBucketExist(userLogin)){
            throw new NoFilesFoundException(userLogin);
        }
        FileDto file = minioService.getFile(userLogin, filename);

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return file;
    }

    @Override
    public void uploadFile(String userLogin, String filename, FileDto fileDto) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        if (!minioService.isBucketExist(userLogin)){
             minioService.createBucket(userLogin);
        }
        minioService.uploadFile(userLogin,filename,fileDto);
        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void deleteFile(String userLogin, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        minioService.deleteFile(userLogin, filename);

        log.info("Method delete file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void renameFile(String userLogin, String oldFilename, RequestRenameFileDto renameFileDto) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        minioService.renameFile(userLogin, oldFilename, renameFileDto.getNewFileName());

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }
}
