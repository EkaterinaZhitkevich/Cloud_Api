package org.ezhitkevich.cloud_api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ezhitkevich.cloud_api.controller.files.FilesController;
import org.ezhitkevich.cloud_api.dto.FileDto;
import org.ezhitkevich.cloud_api.dto.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.RequestRenameFileDto;
import org.ezhitkevich.cloud_api.exception.InvalidFileInputDataException;
import org.ezhitkevich.cloud_api.exception.NoFilesFoundException;
import org.ezhitkevich.cloud_api.facade.files.FilesFacade;
import org.ezhitkevich.cloud_api.handler.GlobalAppExceptionHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = FilesController.class)
@WithMockUser
public class FilesControllerTest extends AbstractControllerTest {

    MockMvc mockMvc;

    @MockBean
    FilesFacade filesFacade;

    @Autowired
    ObjectMapper objectMapper;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @BeforeEach
    void beforeEach() {
        this.mockMvc = mockMvc();
    }

    @Test
    public void getAllFilesLimitShouldSuccessfullyReturnOkAndListFilesResponseDto() throws Exception {
        Integer limit = 2;
        String userLogin = "user";
        List<ListFileResponseDto> listFileResponseDtos = listFileResponseDtos();

        when(filesFacade.getAllFilesLimit(userLogin, limit)).thenReturn(listFileResponseDtos);

        MvcResult mvcResult = mockMvc.perform(get("/cloud/list")
                        .queryParam("limit", String.valueOf(limit)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<ListFileResponseDto> actListFileResponseDtos =
                objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, ListFileResponseDto.class));

        Assertions.assertEquals(listFileResponseDtos, actListFileResponseDtos);
    }

    @Test
    public void getAllFilesLimitShouldReturnBadRequest() throws Exception {
        Integer limit = 2;
        String userLogin = "user";

        when(filesFacade.getAllFilesLimit(userLogin, limit)).thenThrow(NoFilesFoundException.class);

        mockMvc.perform(get("/cloud/list")
                        .queryParam("limit", String.valueOf(limit)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    public void uploadFileShouldSuccessfullyReturnOk() throws Exception {
        String filename = filename();
        String username = "user";
        FileDto fileDto = fileDto();

        mockMvc.perform(post("/cloud/file")
                        .param("filename", filename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileDto)))
                .andExpect(status().isOk());

        verify(filesFacade, times(1)).uploadFile(anyString(), anyString(), any(FileDto.class));
    }

    @Test
    public void uploadFileShouldThrowInvalidFileInputDataExceptionAndReturnBadRequest() throws Exception {
        String filename = filename();
        FileDto fileDto = fileDto();

        doThrow(InvalidFileInputDataException.class)
                .when(filesFacade).uploadFile(anyString(), anyString(), any());

        mockMvc.perform(post("/cloud/file")
                        .param("filename", filename)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void getFileShouldSuccessfullyReturnOkAndFileDto() throws Exception {
        String filename = filename();
        String userLogin = "user";
        FileDto fileDto = fileDto();

        when(filesFacade.getFile(userLogin, filename)).thenReturn(fileDto);

        MvcResult mvcResult = mockMvc.perform(get("/cloud/file")
                        .queryParam("filename", filename))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        FileDto actFileDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructType(FileDto.class));

        Assertions.assertEquals(fileDto, actFileDto);

    }

    @Test
    public void getFileShouldThrowNoFilesFoundExceptionAndReturnBadRequest() throws Exception {
        String filename = filename();
        String userLogin = "user";

        when(filesFacade.getFile(userLogin, filename)).thenThrow(NoFilesFoundException.class);

        mockMvc.perform(get("/cloud/file")
                        .queryParam("filename", filename))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFileShouldThrowInvalidFileInputDataExceptionAndReturnBadRequest() throws Exception {
        String filename = filename();
        String userLogin = "user";

        when(filesFacade.getFile(userLogin, filename)).thenThrow(InvalidFileInputDataException.class);

        mockMvc.perform(get("/cloud/file")
                        .queryParam("filename", filename))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getFileShouldThrowConnectionExceptionAndReturnInternalServerError() throws Exception {
        String filename = filename();
        String userLogin = "user";

        when(filesFacade.getFile(userLogin, filename)).thenThrow(ConnectException.class);

        mockMvc.perform(get("/cloud/file")
                        .queryParam("filename", filename))
                .andExpect(status().isInternalServerError());
    }


    @Test
    public void renameFileShouldSuccessfullyReturnOk() throws Exception {
        String oldFilename = filename();
        String userLogin = "user";
        RequestRenameFileDto renameFileDto = new RequestRenameFileDto("new file.txt");

        mockMvc.perform(put("/cloud/file")
                        .queryParam("filename", oldFilename)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(renameFileDto)))
                .andExpect(status().isOk());

        verify(filesFacade, times(1)).renameFile(userLogin, oldFilename, renameFileDto);
    }

    @Test
    public void renameFileShouldThrowInvalidFileInputDataExceptionAndReturnBadRequest() throws Exception{
        String oldFilename = filename();
        RequestRenameFileDto renameFileDto = new RequestRenameFileDto("new file.txt");

        doThrow(InvalidFileInputDataException.class)
                .when(filesFacade).renameFile(anyString(), anyString(), any());

        mockMvc.perform(put("/cloud/file")
                        .queryParam("filename", oldFilename)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(renameFileDto)))
                .andExpect(status().isBadRequest());
    }


    @Test
    public void deleteFileShouldSuccessfullyReturnOk() throws Exception {
        String filename = filename();

        mockMvc.perform(delete("/cloud/file")
                        .queryParam("filename", filename)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(filesFacade, times(1)).deleteFile(anyString(), stringArgumentCaptor.capture());
        String actualValue = stringArgumentCaptor.getValue();
        Assertions.assertEquals(filename, actualValue);
    }

    @Test
    public void deleteFilesShouldThrowInvalidFileInputDataExceptionAndReturnBadRequest() throws Exception{
        String filename = filename();

        doThrow(InvalidFileInputDataException.class)
                .when(filesFacade).deleteFile(anyString(), anyString());

        mockMvc.perform(delete("/cloud/file")
                        .queryParam("filename", filename)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }


    private List<ListFileResponseDto> listFileResponseDtos() {
        return List.of(ListFileResponseDto.builder()
                        .filename("text.txt")
                        .size(52L)
                        .build(),
                ListFileResponseDto.builder()
                        .filename("image.png")
                        .size(256L)
                        .build());
    }

    private FileDto fileDto() {
        File file = new File(filePath());
        String algorithm = "SHA-256";
        StringBuilder binaryFile = new StringBuilder();
        byte[] digest = null;

        try (FileInputStream fos = new FileInputStream(file)) {

            byte[] fileBytes = fos.readAllBytes();

            for (byte b : fileBytes) {
                binaryFile.append(Integer.toBinaryString(b));
            }
            digest = MessageDigest.getInstance(algorithm).digest(fileBytes);

        } catch (IOException | NoSuchAlgorithmException e) {
            Assertions.fail();
        }
        return FileDto.builder()
                .file(binaryFile.toString())
                .hash(Arrays.toString(digest))
                .build();
    }

    private String filename() {
        return filePath().substring(filePath().lastIndexOf('/'));
    }

    private String filePath() {
        return "src/test/resources/file.txt";
    }

    private MockMvc mockMvc() {
        return MockMvcBuilders
                .standaloneSetup(new FilesController(filesFacade))
                .setControllerAdvice(new GlobalAppExceptionHandler())
                .build();
    }
}
