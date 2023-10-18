package org.ezhitkevich.authorization_service.facade.files.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FilesFacadeImpl implements FilesFacade {

    private final MinioService minioService;

    @Override
    public List<FileDto> getAllFiles() {
        return minioService.getAllFiles();
    }

    @Override
    public InputStream getFile(String filename) {
        return minioService.getFile(filename);
    }

    @Override
    public FileDto uploadFile(FileDto fileDto) {
        return minioService.uploadFile(fileDto);
    }

    @Override
    public void deleteFile(String filename) {
        minioService.deleteFile(filename);
    }

    @Override
    public void renameFile(String oldFilename, RequestRenameFileDto renameFileDto) {
        minioService.renameFile(oldFilename, renameFileDto.getNewFileName());
    }
}
