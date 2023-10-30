package org.ezhitkevich.cloud_api.service.files;

import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;

import java.io.IOException;

public interface MinioService {

    MinioFile getFile(String bucketName, String filename) throws IOException;

    void uploadFile(String bucketName, String filename, MinioFile minioFile);

   void deleteFile(String bucketName, String filename);

   void renameFile(String bucketName, String oldFilename, String newFilename);

   void createBucket(String username);

   boolean isBucketExist(String bucketName);
}
