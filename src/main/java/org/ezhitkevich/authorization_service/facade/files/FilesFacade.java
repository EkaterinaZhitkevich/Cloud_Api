package org.ezhitkevich.authorization_service.facade.files;

import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.ListFileResponseDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface FilesFacade {

    List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit);

    FileDto getFile(String userLogin, String filename) throws IOException;

    void uploadFile(String userLogin, String filename, FileDto fileDto);

    void deleteFile(String userLogin, String filename);

    void renameFile(String userLogin, String oldFilename, RequestRenameFileDto renameFileDto);

}
