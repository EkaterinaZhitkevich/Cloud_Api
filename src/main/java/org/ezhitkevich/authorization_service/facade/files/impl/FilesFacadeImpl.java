package org.ezhitkevich.authorization_service.facade.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.ezhitkevich.authorization_service.model.FileMetadata;
import org.ezhitkevich.authorization_service.model.MinioFile;
import org.ezhitkevich.authorization_service.service.files.FilesService;
import org.springframework.stereotype.Component;

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
    public FileDto getFile(String userLogin, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        MinioFile file = filesService.getFile(userLogin, filename);
        FileDto fileDto = FileDto.builder()
                .file(file.getFile())
                .hash(file.getHash())
                .build();

        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return fileDto;
    }

    @Override
    public void uploadFile(String userLogin, String filename, FileDto fileDto) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        MinioFile file = MinioFile.builder()
                .file(fileDto.getFile())
                .hash(fileDto.getHash())
                .build();
        filesService.uploadFile(userLogin, filename, file);

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
