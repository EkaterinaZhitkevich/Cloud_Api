package org.ezhitkevich.cloud_api.service.files;

import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;

import java.io.IOException;
import java.util.List;

public interface FilesService {

    List<FileMetadata> getAllFilesLimit(String username, Integer limit);

    MinioFile getFile(String username, String filename) throws IOException;

    void uploadFile(String username, String filename, MinioFile minioFile);

    void deleteFile(String username, String filename);

    void renameFile(String username, String oldFilename, String newFilename);
}
