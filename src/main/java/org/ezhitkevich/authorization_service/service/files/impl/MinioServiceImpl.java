package org.ezhitkevich.authorization_service.service.files.impl;

import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.exception.InvalidFileInputDataException;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:minio/minio.properties")
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket.name}")
    private String bucketName;

    @Override
    public List<FileDto> getAllFiles() {
        List<FileDto> files = new ArrayList<>();

        try {
            Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder()
                    .bucket(bucketName)
                    .recursive(true)
                    .build());
            for (Result<Item> item : results) {
                files.add(FileDto.builder()
                        .filename(item.get().objectName())
                        .size(item.get().size())
                        .url(getPreSignedUrl(item.get().objectName()))
                        .build());
            }
        } catch (Exception e) {
            log.error("Happened error with get all files. Cause: {}", e.getMessage());
            throw new InvalidFileInputDataException();
        }
        return files;

    }

    @Override
    public InputStream getFile(String filename) {
        InputStream stream = null;

        try {
            stream = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename)
                    .build());
        } catch (Exception e) {
            log.error("Happened error with get file with name {}. Cause: {}", filename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

        return stream;
    }

    @Override
    public FileDto uploadFile(FileDto fileDto) {

        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(fileDto.getFile().getOriginalFilename())
                    .stream(fileDto.getFile().getInputStream(), fileDto.getFile().getSize(), -1)
                    .build());
        } catch (Exception e) {
            log.error("Happened error with upload file. Cause: {}", e.getMessage());
            throw new InvalidFileInputDataException();
        }

        return FileDto.builder()
                .title(fileDto.getTitle())
                .url(fileDto.getUrl())
                .size(fileDto.getSize())
                .filename(fileDto.getFilename())
                .build();
    }

    @Override
    public void deleteFile(String filename) {

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename).build());
        } catch (Exception e) {
            log.error("Happened error with delete file with name {}. Cause: {}", filename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

    @Override
    public void renameFile(String oldFilename, String newFilename) {

        try {
            minioClient.copyObject(CopyObjectArgs.builder()
                    .bucket(bucketName)
                    .object(newFilename)
                    .source(CopySource.builder()
                            .bucket(bucketName)
                            .object(oldFilename)
                            .build())
                    .build());
            deleteFile(oldFilename);

        } catch (Exception e) {
            log.error("Happened error with rename file with name {}. Cause: {}", oldFilename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

    private final String getPreSignedUrl(String filename) {
        return "http://localhost:8080/files/".concat(filename);
    }
}
