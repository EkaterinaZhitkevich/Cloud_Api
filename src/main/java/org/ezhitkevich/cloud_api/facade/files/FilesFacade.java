package org.ezhitkevich.cloud_api.facade.files;

import org.ezhitkevich.cloud_api.dto.FileDto;
import org.ezhitkevich.cloud_api.dto.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.RequestRenameFileDto;

import java.io.IOException;
import java.util.List;

public interface FilesFacade {

    List<ListFileResponseDto> getAllFilesLimit(String username, Integer limit);

    FileDto getFile(String username, String filename) throws IOException;

    void uploadFile(String username, String filename, FileDto fileDto);

    void deleteFile(String username, String filename);

    void renameFile(String username, String oldFilename, RequestRenameFileDto renameFileDto);

}
