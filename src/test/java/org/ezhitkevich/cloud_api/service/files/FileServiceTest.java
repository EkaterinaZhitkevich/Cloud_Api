package org.ezhitkevich.cloud_api.service.files;

import org.ezhitkevich.cloud_api.exception.NoFilesFoundException;
import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.ezhitkevich.cloud_api.model.MinioFile;
import org.ezhitkevich.cloud_api.model.Role;
import org.ezhitkevich.cloud_api.model.User;
import org.ezhitkevich.cloud_api.repository.FileMetadataRepository;
import org.ezhitkevich.cloud_api.service.files.impl.FilesServiceImpl;
import org.ezhitkevich.cloud_api.service.files.impl.MinioServiceImpl;
import org.ezhitkevich.cloud_api.service.security.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FileServiceTest {

    @InjectMocks
    FilesServiceImpl filesService;

    @Mock
    MinioServiceImpl minioService;

    @Mock
    FileMetadataRepository fileMetadataRepository;

    @Mock
    UserServiceImpl userService;

    @Captor
    ArgumentCaptor<MinioFile> fileArgumentCaptor;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Test
    public void getAllFilesLimitShouldReturnListFileMetadata() {
        User user = user();
        FileMetadata file = FileMetadata.builder()
                .fileUuid(UUID.randomUUID())
                .filename("file")
                .extension(".txt")
                .url("http://localhost:8080/cloud/file/file.txt")
                .size(52L)
                .user(user).build();
        FileMetadata img = FileMetadata.builder()
                .fileUuid(UUID.randomUUID())
                .filename("img")
                .extension(".png")
                .url("http://localhost:8080/cloud/file/img.png")
                .size(256L)
                .user(user).build();
        List<FileMetadata> files = List.of(file, img);

        when(fileMetadataRepository.findAllFilesByUsername(user.getLogin())).thenReturn(files);

        List<FileMetadata> actFiles = filesService.getAllFilesLimit(user.getLogin(), 2);

        assertEquals(files, actFiles);
    }

    @Test
    public void getAllFilesShouldThrowNoFilesFoundException() {
        User user = user();
        when(fileMetadataRepository.findAllFilesByUsername(user.getLogin())).thenThrow(NoFilesFoundException.class);

        assertThrows(NoFilesFoundException.class, () -> filesService.getAllFilesLimit(user.getLogin(), 2));
    }

    @Test
    public void getFileShouldReturnMinioFile() throws IOException {
        User user = user();
        String filename = "file.txt";
        MinioFile file = MinioFile.builder()
                .binaryStringFile("1010101001010101")
                .hash("5e162148b9097f0dbff32c34657d653c")
                .build();

        when(minioService.getFile(user.getLogin(), filename)).thenReturn(file);

        MinioFile actFile = filesService.getFile(user().getLogin(), filename);

        assertEquals(file, actFile);
    }

    @Test
    public void getFileShouldThrowNoFilesFoundException() throws IOException {
        User user = user();
        String filename = "file.txt";

        when(minioService.getFile(user.getLogin(), filename)).thenThrow(NoFilesFoundException.class);

        assertThrows(NoFilesFoundException.class, () -> filesService.getFile(user.getLogin(), filename));
    }

    @Test
    public void uploadFileShouldSuccessfullyWork() throws IOException{
        String filename = "file.txt";
        MinioFile minioFile = MinioFile.builder()
                .binaryStringFile("1010101001010101")
                .hash("5e162148b9097f0dbff32c34657d653c")
                .build();

        filesService.uploadFile(user().getLogin(), filename, minioFile);

        verify(minioService).uploadFile(anyString(), anyString(), fileArgumentCaptor.capture());
        verify(userService, times(1)).findUserByLogin(anyString());
        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));

        MinioFile value = fileArgumentCaptor.getValue();

        assertEquals(minioFile, value);
    }

    @Test
    public void deleteFileShouldSuccessfullyWork() {
        User user = user();
        String filename = "file.txt";
        String shortFilename = "file";
        String fileExtension = ".txt";
        FileMetadata fileMetadata = fileMetadata();

        when(fileMetadataRepository.findByUsernameAndFilenameAndExtension(user.getLogin(), shortFilename, fileExtension))
                .thenReturn(Optional.of(fileMetadata));

        filesService.deleteFile(user.getLogin(), filename);

        verify(minioService, times(1)).deleteFile(user.getLogin(), filename);
        verify(fileMetadataRepository).deleteByFilenameAndExtension(stringArgumentCaptor.capture(), anyString());
        String value = stringArgumentCaptor.getValue();

        assertEquals(shortFilename, value);
    }

    @Test
    public void deleteFileShouldThrowNoFilesFoundException() {
        User user = user();
        String filename = "file.txt";
        String shortFilename = "file";
        String fileExtension = ".txt";

        when(fileMetadataRepository.findByUsernameAndFilenameAndExtension(user.getLogin(), shortFilename, fileExtension))
                .thenReturn(Optional.empty());

        assertThrows(NoFilesFoundException.class, () -> filesService.deleteFile(user.getLogin(), filename));
    }

    @Test
    public void renameFileShouldSuccessfullyWork() {
        User user = user();
        String oldFilename = "file.txt";
        String shortOldFilename = "file";
        String newFileName = "file_2.txt";
        String fileExtension = ".txt";
        FileMetadata fileMetadata = fileMetadata();

        when(fileMetadataRepository.findByUsernameAndFilenameAndExtension(user.getLogin(), shortOldFilename, fileExtension))
                .thenReturn(Optional.of(fileMetadata));

        filesService.renameFile(user.getLogin(), oldFilename, newFileName);

        verify(minioService, times(1)).renameFile(anyString(), anyString(), anyString());
    }

    @Test
    public void renameFileShouldThrowNoFilesFoundException() {
        User user = user();
        String oldFilename = "file.txt";
        String shortOldFilename = "file";
        String newFileName = "file_2.txt";
        String fileExtension = ".txt";

        when(fileMetadataRepository.findByUsernameAndFilenameAndExtension(user.getLogin(), shortOldFilename, fileExtension))
                .thenReturn(Optional.empty());

        assertThrows(NoFilesFoundException.class, () -> filesService.renameFile(user.getLogin(), oldFilename, newFileName));
    }

    private User user() {
        return User.builder()
                .login("user")
                .password("password")
                .userUuid(UUID.randomUUID())
                .roles(Set.of(new Role("USER")))
                .build();
    }

    private FileMetadata fileMetadata() {
        return FileMetadata.builder()
                .fileUuid(UUID.randomUUID())
                .filename("file")
                .extension(".txt")
                .url("http://localhost:8080/cloud/file/file.txt")
                .size(52L)
                .user(user()).build();
    }
}
