package org.ezhitkevich.authorization_service.service.files;

import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.model.FileMetadata;
import org.ezhitkevich.authorization_service.model.MinioFile;

import java.io.IOException;
import java.util.List;

public interface FilesService {

    List<FileMetadata> getAllFilesLimit(String username, Integer limit);

    MinioFile getFile(String bucketName, String filename) throws IOException;

    void uploadFile(String bucketName, String filename, MinioFile minioFile);

    void deleteFile(String bucketName, String filename);

    void renameFile(String bucketName, String oldFilename, String newFilename);
}
