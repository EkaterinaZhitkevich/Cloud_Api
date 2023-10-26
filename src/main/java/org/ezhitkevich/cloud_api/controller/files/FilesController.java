package org.ezhitkevich.cloud_api.controller.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ezhitkevich.cloud_api.dto.FileDto;
import org.ezhitkevich.cloud_api.dto.ListFileResponseDto;
import org.ezhitkevich.cloud_api.dto.RequestRenameFileDto;
import org.ezhitkevich.cloud_api.facade.files.FilesFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/cloud")
@RequiredArgsConstructor
@Slf4j
public class FilesController{

    private final FilesFacade filesFacade;

    @GetMapping("/list")
    public ResponseEntity<List<ListFileResponseDto>> getAllFilesLimit(@RequestParam("limit") Integer limit){
        log.info("Method get all files in class {} started", getClass().getSimpleName());
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        List<ListFileResponseDto> files = filesFacade.getAllFilesLimit(username, limit);
        log.info("Method get all files in class {} finished", getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.OK).body(files);
    }

    @PostMapping( "/file")
    public ResponseEntity<Void> uploadFile(@RequestParam("filename") String filename, FileDto fileDto) {
        log.info("Method upload file in class {} started", getClass().getSimpleName());
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        filesFacade.uploadFile(username,filename, fileDto);
        log.info("Method upload file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok().build();
    }

    @GetMapping( "/file")
    public ResponseEntity<FileDto> getFile(@RequestParam("filename") String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        FileDto file = filesFacade.getFile(username, filename);
        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok(file);
    }

    @PutMapping("/file")
    public ResponseEntity<Void> renameFile(@RequestParam("filename") String oldFilename,
                                           @RequestBody RequestRenameFileDto renameFileDto){
        log.info("Method rename file in class {} started", getClass().getSimpleName());
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        filesFacade.renameFile(username, oldFilename, renameFileDto);
        log.info("Method rename file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestParam("filename") String filename){
        log.info("Method delete file in class {} started", getClass().getSimpleName());
        UserDetails principal = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = principal.getUsername();
        filesFacade.deleteFile(username, filename);
        log.info("Method delete file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok().build();
    }
}
