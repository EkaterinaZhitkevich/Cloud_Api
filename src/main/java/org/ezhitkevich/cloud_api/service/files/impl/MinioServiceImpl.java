package org.ezhitkevich.cloud_api.service.files.impl;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.exception.InvalidFileInputDataException;
import org.ezhitkevich.cloud_api.model.MinioFile;
import org.ezhitkevich.cloud_api.service.files.MinioService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    private static final String ALGORITHM = "SHA-256";


    @Override
    public MinioFile getFile(String bucketName, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        InputStream stream = null;
        String binaryFileString = null;
        String fileHash = null;
        try {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());

            binaryFileString = getBinaryStringFromInputStream(stream);

            byte[] digest = MessageDigest.getInstance(ALGORITHM).digest(stream.readAllBytes());
            fileHash = Arrays.toString(digest);

            log.info("Method get file in class {} finished", getClass().getSimpleName());

        } catch (Exception e) {
            log.error("Happened error with get file with name {}. Cause: {}", filename, e.getMessage());
            throw new InvalidFileInputDataException();
        } finally {
            stream.close();
        }
        return MinioFile.builder()
                .hash(fileHash)
                .file(binaryFileString)
                .build();
    }

    @Override
    public void uploadFile(String bucketName, String filename, MinioFile minioFile) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        byte[] fileBytes = minioFile.getFile().getBytes();

        try (InputStream stream = new ByteArrayInputStream(fileBytes)) {

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(stream, stream.available(), -1)
                    .build());

            log.info("Method upload file in class {} finished", getClass().getSimpleName());

        } catch (Exception e) {
            log.error("Happened error with upload file. Cause: {}", e.getMessage());
            throw new InvalidFileInputDataException();
        }
    }

    @Override
    public void deleteFile(String bucketName, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename).build());


            log.info("Method delete file in class {} finished", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Happened error with delete file with name {}. Cause: {}", filename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

    @Override
    public void renameFile(String bucketName, String oldFilename, String newFilename) {
        log.info("Method rename file in class {} started", getClass().getSimpleName());

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFilename)
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(oldFilename)
                            .build())
                    .build());

            log.info("Method rename file in class {} finished", getClass().getSimpleName());

        } catch (Exception e) {
            log.error("Happened error with rename file with name {}. Cause: {}", oldFilename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

     public void createBucket(String username) {
         String bucketName = createValidBucketName(username);
         try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

     public boolean isBucketExist(String username) {
         String bucketName = createValidBucketName(username);
         boolean isBucketExist;
        try {
            isBucketExist = minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return isBucketExist;
    }


    private String getBinaryStringFromInputStream(InputStream stream) throws IOException {
        StringBuilder binaryFile = new StringBuilder();

        byte[] bytes = stream.readAllBytes();
        for (byte b : bytes) {
            binaryFile.append(Integer.toBinaryString(stream.read()));
        }

        return binaryFile.toString();
    }

    private String createValidBucketName(String username){
        int randomBucketNumber = ThreadLocalRandom.current().nextInt(10, 5000);
        return username.toLowerCase().concat("-").concat(String.valueOf(randomBucketNumber));
    }

}
