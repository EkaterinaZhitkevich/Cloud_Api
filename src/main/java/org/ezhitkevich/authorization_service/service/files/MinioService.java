package org.ezhitkevich.authorization_service.service.files;

import org.ezhitkevich.authorization_service.model.FileMetadata;
import org.ezhitkevich.authorization_service.model.MinioFile;

import java.io.IOException;

public interface MinioService {

    MinioFile getFile(String bucketName, String filename) throws IOException;

    FileMetadata uploadFile(String bucketName, String filename, MinioFile minioFile);

   void deleteFile(String bucketName, String filename);

   void renameFile(String bucketName, String oldFilename, String newFilename);

   void createBucket(String bucketName);

   boolean isBucketExist(String bucketName);
}
