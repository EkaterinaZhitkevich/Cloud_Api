package org.ezhitkevich.cloud_api.facade.files;

import org.ezhitkevich.cloud_api.dto.FileDto;
import org.ezhitkevich.cloud_api.dto.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.RequestRenameFileDto;

import java.io.IOException;
import java.util.List;

public interface FilesFacade {

    List<ListFileResponseDto> getAllFilesLimit(String userLogin, Integer limit);

    FileDto getFile(String userLogin, String filename) throws IOException;

    void uploadFile(String userLogin, String filename, FileDto fileDto);

    void deleteFile(String userLogin, String filename);

    void renameFile(String userLogin, String oldFilename, RequestRenameFileDto renameFileDto);

}
