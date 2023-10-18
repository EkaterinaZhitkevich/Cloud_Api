package org.ezhitkevich.authorization_service.facade.files;

import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;

import java.io.InputStream;
import java.util.List;

public interface FilesFacade {

    List<FileDto> getAllFiles() throws Exception;

    InputStream getFile(String filename);

    FileDto uploadFile(FileDto fileDto);

    void deleteFile(String filename);

    void renameFile(String oldFilename, RequestRenameFileDto renameFileDto);

}
