package org.ezhitkevich.authorization_service.service.files.impl;

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
import org.ezhitkevich.authorization_service.exception.InvalidFileInputDataException;
import org.ezhitkevich.authorization_service.model.FileMetadata;
import org.ezhitkevich.authorization_service.model.MinioFile;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:minio/minio.properties")
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
    public FileMetadata uploadFile(String bucketName, String filename, MinioFile minioFile) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        byte[] fileBytes = minioFile.getFile().getBytes();
        FileMetadata fileMetadata = null;

        try (InputStream stream = new ByteArrayInputStream(fileBytes)) {

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .stream(stream, stream.available(), -1)
                    .build());

            fileMetadata = FileMetadata.builder()
                    .filename(getFilenameFromFullFileName(filename))
                    .extension(getExtensionFromFullFilename(filename))
                    .url(getPreSignedUrl(filename))
                    .hash(minioFile.getHash())
                    .build();


            log.info("Method upload file in class {} finished", getClass().getSimpleName());
            return fileMetadata;

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

    public void createBucket(String bucketName) {
        try {
            minioClient.makeBucket(MakeBucketArgs.builder()
                    .bucket(bucketName)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isBucketExist(String bucketName) {
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

    private final String getPreSignedUrl(String filename) {
        return "http:\\localhost:8080\\cloud\\file".concat(filename);
    }

    private String getFilenameFromFullFileName(String fullFilename) {
        return fullFilename.substring(0, fullFilename.lastIndexOf('.'));
    }

    private String getExtensionFromFullFilename(String fullFilename) {
        return fullFilename.substring(fullFilename.lastIndexOf('.'));
    }

    private String getBinaryStringFromInputStream(InputStream stream) throws IOException {
        StringBuilder binaryFile = new StringBuilder();

        byte[] bytes = stream.readAllBytes();
        for (byte b : bytes) {
            binaryFile.append(Integer.toBinaryString(stream.read()));
        }

        return binaryFile.toString();
    }

}
