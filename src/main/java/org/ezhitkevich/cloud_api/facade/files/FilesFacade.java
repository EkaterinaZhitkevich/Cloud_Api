package org.ezhitkevich.cloud_api.facade.files;

import org.ezhitkevich.cloud_api.dto.response.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.request.RequestRenameFileDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FilesFacade {

    List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit);

    Resource getFile(String userLogin, String filename) throws IOException;

    void uploadFile(String userLogin, String filename, MultipartFile file);

    void deleteFile(String userLogin, String filename);

    void renameFile(String userLogin, String oldFilename, RequestRenameFileDto renameFileDto);

}
