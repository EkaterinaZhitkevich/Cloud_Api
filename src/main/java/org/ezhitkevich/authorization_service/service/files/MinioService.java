package org.ezhitkevich.authorization_service.service.files;

import org.ezhitkevich.authorization_service.dto.FileDto;

import java.io.InputStream;
import java.util.List;

public interface MinioService {

    List<FileDto> getAllFiles();

    InputStream getFile(String filename);

    FileDto uploadFile(FileDto fileDto);

   void deleteFile(String filename);

   void renameFile(String oldFilename, String newFilename);
}
