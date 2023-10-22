package org.ezhitkevich.authorization_service.service.files;

import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface MinioService {

    List<ListFileResponseDto> getAllFilesLimit(String username, Integer limit);

    FileDto getFile(String bucketName, String filename) throws IOException;

    void uploadFile(String bucketName, String filename, FileDto fileDto);

   void deleteFile(String bucketName, String filename);

   void renameFile(String bucketName, String oldFilename, String newFilename);

   void createBucket(String bucketName);

   boolean isBucketExist(String bucketName);
}
