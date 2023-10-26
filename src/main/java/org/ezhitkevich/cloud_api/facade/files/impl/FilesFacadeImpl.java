package org.ezhitkevich.cloud_api.facade.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.dto.response.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.request.RequestRenameFileDto;
import org.ezhitkevich.cloud_api.facade.files.FilesFacade;
import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;
import org.ezhitkevich.cloud_api.service.files.FilesService;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesFacadeImpl implements FilesFacade {

    private final FilesService filesService;

    @Override
    public List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit) {
        log.info("Method get all files limit in class {} started", getClass().getSimpleName());


        List<FileMetadata> filesLimit = filesService.getAllFilesLimit(userLogin, limit);
        List<ListFileResponseDto> fileResponseDtos = filesLimit.stream().map(fileMetadata -> ListFileResponseDto.builder()
                .filename(fileMetadata.getFilename().concat(fileMetadata.getExtension()))
                .size(fileMetadata.getSize())
                .build()).toList();

        log.info("Method get all files limit in class {} finished", getClass().getSimpleName());
        return fileResponseDtos;
    }

    @Override
    public Resource getFile(String userLogin, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        MinioFile file = filesService.getFile(userLogin, filename);

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return file.getResource();
    }

    @Override
    public void uploadFile(String userLogin, String filename, MultipartFile file) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        MinioFile minioFile = MinioFile.builder()
                .resource(file.getResource())
                .build();
        filesService.uploadFile(userLogin, filename, minioFile);

        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void deleteFile(String userLogin, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        filesService.deleteFile(userLogin, filename);

        log.info("Method delete file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void renameFile(String userLogin, String oldFilename, RequestRenameFileDto renameFileDto) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        filesService.renameFile(userLogin, oldFilename, renameFileDto.getNewFileName());

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }
}
