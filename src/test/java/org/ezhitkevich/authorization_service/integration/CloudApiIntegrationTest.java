package org.ezhitkevich.authorization_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.ezhitkevich.authorization_service.dto.AuthResponseDto;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.ezhitkevich.authorization_service.dto.UserRequestDto;
import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.ezhitkevich.authorization_service.facade.security.AuthorizationFacade;
import org.ezhitkevich.authorization_service.facade.security.RegistrationFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@WithMockUser("user")
public class CloudApiIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilesFacade filesFacade;

    @Autowired
    RegistrationFacade registrationFacade;

    @Autowired
    AuthorizationFacade authorizationFacade;

    @Autowired
    ObjectMapper objectMapper;

    private static final String USERNAME = "user";

    private static final String AUTHORIZATION_HEADER = "Authorization";

    private String authToken;


    @Test
    public void registrationLoginFilesTest() throws Exception{
        registerShouldSuccessfullyReturnOk();
        loginShouldSuccessfullyReturnOkAndAuthToken();
        insertMinioFiles();
        getAllFilesShouldSuccessfullyReturnOkAndListFileResponseDto();
        getFileShouldSuccessfullyReturnOkAndFileDto();
        uploadFileShouldSuccessfullyReturnOk();
        renameFileShouldSuccessfullyReturnOk();
        deleteFileSuccessfullyReturnOk();
        logoutShouldSuccessfullyReturnOk();
    }

    public void registerShouldSuccessfullyReturnOk() throws Exception {
        UserRequestDto userRequestDto = userRequestDto();

        mockMvc.perform(post("/cloud/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isOk());
    }


    public void loginShouldSuccessfullyReturnOkAndAuthToken() throws Exception {
        UserRequestDto userRequestDto = userRequestDto();
        MvcResult mvcResult = mockMvc.perform(post("/cloud/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AuthResponseDto token = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                AuthResponseDto.class);
        authToken = token.getAuthToken();

        assertNotNull(authToken);
    }


    public void getAllFilesShouldSuccessfullyReturnOkAndListFileResponseDto() throws Exception {

        Integer limit = 2;

        MvcResult mvcResult = mockMvc.perform(get("/cloud/list")
                        .queryParam("limit", String.valueOf(limit))
                        .accept(MediaType.APPLICATION_JSON)
                        .header(AUTHORIZATION_HEADER, authToken))
                .andDo(print())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<ListFileResponseDto> fileResponseDtos = objectMapper.readValue(mvcResult.getResponse()
                        .getContentAsString(),
                objectMapper.getTypeFactory().constructType(List.class, ListFileResponseDto.class));

        assertEquals(2, fileResponseDtos.size());
    }


    public void uploadFileShouldSuccessfullyReturnOk() throws Exception {
        String filepath = "src/test/resources/file2.txt";
        String filename = filename(filepath);
        FileDto fileDto = fileDto(filename);

        mockMvc.perform(post("/cloud/file")
                        .param("filename", filename)
                        .header(AUTHORIZATION_HEADER, authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(fileDto)))
                .andExpect(status().isOk());
    }


    public void getFileShouldSuccessfullyReturnOkAndFileDto() throws Exception {

        String filePath = filePaths()[0];
        String filename = filename(filePath);
        FileDto fileDto = fileDto(filePath);

        MvcResult mvcResult = mockMvc.perform(get("/cloud/file")
                        .queryParam("filename", filename)
                        .header(AUTHORIZATION_HEADER, authToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        FileDto actFileDto = objectMapper.readValue(mvcResult.getResponse().getContentAsString(),
                objectMapper.getTypeFactory().constructType(FileDto.class));

        assertEquals(fileDto, actFileDto);
    }


    public void renameFileShouldSuccessfullyReturnOk() throws Exception {
        String filePath = filePaths()[0];
        String oldFileName = filename(filePath);
        RequestRenameFileDto renameFileDto = new RequestRenameFileDto("new_file.txt");

        mockMvc.perform(put("/cloud/file")
                        .queryParam("filename", oldFileName)
                        .header(AUTHORIZATION_HEADER, authToken)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(renameFileDto)))
                .andExpect(status().isOk());
    }


    public void deleteFileSuccessfullyReturnOk() throws Exception {
        String filePath = filePaths()[1];
        String filename = filename(filePath);

        mockMvc.perform(delete("/cloud/file")
                        .queryParam("filename", filename)
                        .header(AUTHORIZATION_HEADER, authToken)
                        .with(csrf()))
                .andExpect(status().isOk());
    }


    public void logoutShouldSuccessfullyReturnOk() throws Exception{
     mockMvc.perform(post("/cloud/logout")
             .header(AUTHORIZATION_HEADER, authToken))
             .andExpect(status().isOk());
    }


    private UserRequestDto userRequestDto() {
        return new UserRequestDto("user", "password");
    }


    private void insertMinioFiles() {
        List<FileDto> fileDtos = Arrays.stream(filePaths())
                .map(this::fileDto).toList();
        filesFacade.uploadFile(USERNAME, filename(filePaths()[0]), fileDtos.get(0));
        filesFacade.uploadFile(USERNAME, filename(filePaths()[1]), fileDtos.get(1));
    }

    private FileDto fileDto(String filePath) {
        File file = new File(filePath);
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
            fail();
        }
        return FileDto.builder()
                .file(binaryFile.toString())
                .hash(Arrays.toString(digest))
                .build();
    }

    private String filename(String filePath) {
        return filePath.substring(filePath.lastIndexOf('/'));
    }

    private String[] filePaths() {
        return new String[]{"src/test/resources/file.txt", "src/test/resources/img.jpg"};
    }

}
