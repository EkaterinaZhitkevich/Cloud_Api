package org.ezhitkevich.cloud_api.facade.files;

import org.ezhitkevich.cloud_api.dto.response.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.request.RequestRenameFileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesFacade {

    List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit);

    Resource getFile(String username, String filename) throws IOException;

    void uploadFile(String username, String filename, MultipartFile file) throws IOException;

    void deleteFile(String username, String filename);

    void renameFile(String username, String oldFilename, RequestRenameFileDto renameFileDto);

}
