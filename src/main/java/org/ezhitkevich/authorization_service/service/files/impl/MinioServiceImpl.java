package org.ezhitkevich.authorization_service.service.files.impl;

import io.minio.BucketExistsArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;
import org.ezhitkevich.authorization_service.exception.InvalidFileInputDataException;
import org.ezhitkevich.authorization_service.exception.NoFilesFoundException;
import org.ezhitkevich.authorization_service.model.MinioFile;
import org.ezhitkevich.authorization_service.model.User;
import org.ezhitkevich.authorization_service.repository.MinioFileRepository;
import org.ezhitkevich.authorization_service.service.files.MinioService;
import org.ezhitkevich.authorization_service.service.security.UserService;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:minio/minio.properties")
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    private final MinioFileRepository minioFileRepository;

    private final UserService userService;

    private static final String ALGORITHM = "SHA-256";

    @Override
    @Transactional
    public List<ListFileResponseDto> getAllFilesLimit(String username, Integer limit) {
        log.info("Method getAllFilesLimit in class {} started", getClass().getSimpleName());

        List<MinioFile> files = minioFileRepository.findAllFilesByUsername(username);
        if (files.isEmpty()){
            throw new NoFilesFoundException(username);
        }
        List<ListFileResponseDto> listFileResponseDtos = files.stream()
                .map(minioFile -> ListFileResponseDto.builder()
                        .filename(minioFile.getFilename().concat(minioFile.getExtension()))
                        .size(minioFile.getSize())
                        .build())
                .limit(limit).toList();

        log.info("Method getAllFilesLimit in class {} finished", getClass().getSimpleName());
        return listFileResponseDtos;

    }

    @Override
    public FileDto getFile(String bucketName, String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());

        if (!minioFileRepository.findByUserAndFilename(bucketName, filename)){
            throw new NoFilesFoundException(bucketName);
        }

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
        return FileDto.builder()
                .file(binaryFileString)
                .hash(fileHash)
                .build();
    }

    @Override
    @Transactional
    public void uploadFile(String bucketName, String filename, FileDto fileDto) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());

        byte[] fileBytes = fileDto.getFile().getBytes();
        File file = new File(getPreSignedUrl(filename));
        file.canWrite();
        file.canRead();

        try (FileOutputStream fileOutputStream = new FileOutputStream(file);
             FileInputStream fileInputStream = new FileInputStream(file)) {

            fileOutputStream.write(fileBytes);

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .stream(fileInputStream, file.length(), -1)
                    .build());

            MinioFile minioFile = MinioFile.builder()
                    .filename(getFilenameFromFullFileName(filename))
                    .extension(getExtensionFromFullFilename(filename))
                    .url(getPreSignedUrl(filename))
                    .hash(fileDto.getHash())
                    .build();

            User user = userService.findUserByLogin(bucketName);
            minioFile.setUser(user);
            minioFileRepository.save(minioFile);

            log.info("Method upload file in class {} finished", getClass().getSimpleName());

        } catch (Exception e) {
            log.error("Happened error with upload file. Cause: {}", e.getMessage());

            throw new InvalidFileInputDataException();
        }
    }

    @Override
    @Transactional
    public void deleteFile(String bucketName, String filename) {
        log.info("Method delete file in class {} started", getClass().getSimpleName());

        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filename).build());

            minioFileRepository.deleteByFilenameAndExtension(getFilenameFromFullFileName(filename),
                    getExtensionFromFullFilename(filename));

            log.info("Method delete file in class {} finished", getClass().getSimpleName());
        } catch (Exception e) {
            log.error("Happened error with delete file with name {}. Cause: {}", filename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

    @Override
    @Transactional
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

            minioFileRepository.updateByFilename(newFilename,oldFilename);

            log.info("Method rename file in class {} finished", getClass().getSimpleName());

        } catch (Exception e) {
            log.error("Happened error with rename file with name {}. Cause: {}", oldFilename, e.getMessage());
            throw new InvalidFileInputDataException();
        }

    }

    public void createBucket(String bucketName){
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
        return "http://localhost:8080/file/".concat(filename);
    }

    private String getFilenameFromFullFileName(String fullFilename){
        return fullFilename.substring(0, fullFilename.lastIndexOf('.'));
    }

    private String getExtensionFromFullFilename(String fullFilename){
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
