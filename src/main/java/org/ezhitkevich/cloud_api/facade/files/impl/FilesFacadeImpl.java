package org.ezhitkevich.cloud_api.facade.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.dto.FileDto;
import org.ezhitkevich.cloud_api.dto.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.RequestRenameFileDto;
import org.ezhitkevich.cloud_api.facade.files.FilesFacade;
import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;
import org.ezhitkevich.cloud_api.service.files.FilesService;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesFacadeImpl implements FilesFacade {

    private final FilesService filesService;

    @Override
    public List<ListFileResponseDto> getAllFilesLimit(String username, Integer limit) {
        log.info("Method get all files limit in class {} started", getClass().getSimpleName());


        List<FileMetadata> filesLimit = filesService.getAllFilesLimit(username, limit);
        List<ListFileResponseDto> fileResponseDtos = filesLimit.stream().map(fileMetadata -> ListFileResponseDto.builder()
                .filename(fileMetadata.getFilename().concat(fileMetadata.getExtension()))
                .size(fileMetadata.getSize())
                .build()).toList();

        log.info("Method get all files limit in class {} finished", getClass().getSimpleName());
        return fileResponseDtos;
    }

    @Override
    public FileDto getFile(String username, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        MinioFile file = filesService.getFile(username, filename);
        FileDto fileDto = FileDto.builder()
                .file(file.getFile())
                .hash(file.getHash())
                .build();

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return fileDto;
    }

    @Override
    public void uploadFile(String username, String filename, FileDto fileDto) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        MinioFile file = MinioFile.builder()
                .file(fileDto.getFile())
                .hash(fileDto.getHash())
                .build();
        filesService.uploadFile(username, filename, file);

        log.info("Method upload file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void deleteFile(String username, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        filesService.deleteFile(username, filename);

        log.info("Method delete file in class {} finished", getClass().getSimpleName());
    }

    @Override
    public void renameFile(String username, String oldFilename, RequestRenameFileDto renameFileDto) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        filesService.renameFile(username, oldFilename, renameFileDto.getNewFileName());

        log.info("Method rename file in class {} finished", getClass().getSimpleName());
    }
}
