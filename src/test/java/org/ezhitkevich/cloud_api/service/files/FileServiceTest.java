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
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    public void getAllFilesShouldThrowNoFilesFoundException(){
        User user = user();
        when(fileMetadataRepository.findAllFilesByUsername(user.getLogin())).thenThrow(NoFilesFoundException.class);

        assertThrows(NoFilesFoundException.class, () -> filesService.getAllFilesLimit(user.getLogin(), 2));
    }

    @Test
    public void getFileShouldReturnMinioFile() throws IOException {
        User user = user();
        String filename = "file.txt";
        MinioFile file = MinioFile.builder()
                .file("1010101001010101")
                .hash("5e162148b9097f0dbff32c34657d653c")
                .build();

        when(minioService.getFile(user.getLogin(), filename)).thenReturn(file);

        MinioFile actFile = filesService.getFile(user().getLogin(), filename);

        assertEquals(file, actFile);
    }

    @Test
    public void getFileShouldThrowNoFilesFoundException() throws IOException{
        User user = user();
        String filename = "file.txt";

        when(minioService.getFile(user.getLogin(), filename)).thenThrow(NoFilesFoundException.class);

        assertThrows(NoFilesFoundException.class, () -> filesService.getFile(user.getLogin(), filename));
    }

    @Test
    public void uploadFileShouldSuccessfullyWork(){
        User user = user();
        String filename = "file.txt";
        MinioFile minioFile = MinioFile.builder()
                .file("1010101001010101")
                .hash("5e162148b9097f0dbff32c34657d653c")
                .build();
        FileMetadata file = FileMetadata.builder()
                .fileUuid(UUID.randomUUID())
                .filename("file")
                .extension(".txt")
                .url("http://localhost:8080/cloud/file/file.txt")
                .size(52L)
                .user(user).build();


        when(minioService.uploadFile(user.getLogin(), filename, minioFile)).thenReturn(file);
        
        filesService.uploadFile(user().getLogin(), filename, minioFile);
        
        verify(minioService).uploadFile(anyString(), anyString(), fileArgumentCaptor.capture());
        verify(userService, times(1)).findUserByLogin(anyString());
        verify(fileMetadataRepository, times(1)).save(any(FileMetadata.class));

        MinioFile value = fileArgumentCaptor.getValue();

        assertEquals(minioFile, value);
    }

    @Test
    public void deleteFileShouldSuccessfullyWork(){
        User user = user();
        String filename = "file.txt";
        String shortFilename = "file";

        when(fileMetadataRepository.findByUserAndFilename(user.getLogin(), filename)).thenReturn(true);

        filesService.deleteFile(user.getLogin(), filename);

        verify(minioService, times(1)).deleteFile(user.getLogin(), filename);
        verify(fileMetadataRepository).deleteByFilenameAndExtension(stringArgumentCaptor.capture(), anyString());
        String value = stringArgumentCaptor.getValue();

        assertEquals(shortFilename, value);
    }

    @Test
    public void deleteFileShouldThrowNoFilesFoundException(){
        User user = user();
        String filename = "file.txt";

        when(fileMetadataRepository.findByUserAndFilename(user.getLogin(), filename))
                .thenReturn(false);

        assertThrows(NoFilesFoundException.class, () -> filesService.deleteFile(user.getLogin(), filename));
    }

    @Test
    public void renameFileShouldSuccessfullyWork(){
        User user = user();
        String oldFilename = "file.txt";
        String newFileName = "file_2.txt";

        when(fileMetadataRepository.findByUserAndFilename(user.getLogin(), oldFilename)).thenReturn(true);

        filesService.renameFile(user.getLogin(), oldFilename, newFileName);

        verify(fileMetadataRepository, times(1)).updateByFilename(anyString(), anyString());
    }

    @Test
    public void renameFileShouldThrowNoFilesFoundException(){
        User user = user();
        String oldFilename = "file.txt";
        String newFileName = "file_2.txt";

        when(fileMetadataRepository.findByUserAndFilename(user.getLogin(), oldFilename))
                .thenThrow(NoFilesFoundException.class);

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
}
