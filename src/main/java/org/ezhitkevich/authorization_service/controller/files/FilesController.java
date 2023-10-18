package org.ezhitkevich.authorization_service.controller.files;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.ezhitkevich.authorization_service.dto.FileDto;
import org.ezhitkevich.authorization_service.dto.RequestRenameFileDto;
import org.ezhitkevich.authorization_service.facade.files.FilesFacade;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/cloud/file")
@RequiredArgsConstructor
@Slf4j
public class FilesController {

    private final FilesFacade filesFacade;

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFiles() throws Exception{
        log.info("Method get all files in class {} started", getClass().getSimpleName());
        List<FileDto> files = filesFacade.getAllFiles();
        log.info("Method get all files in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok(files);
    }

    @PostMapping
    public ResponseEntity<FileDto> uploadFile(@RequestBody FileDto fileDto){
        log.info("Method upload file in class {} started", getClass().getSimpleName());
        FileDto uploadedFile = filesFacade.uploadFile(fileDto);
        log.info("Method upload file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok(uploadedFile);
    }

    @GetMapping()
    public ResponseEntity<Object> getFile(@RequestParam String filename) throws IOException {
        log.info("Method get file in class {} started", getClass().getSimpleName());
        byte[] byteArray = IOUtils.toByteArray(filesFacade.getFile(filename));
        log.info("Method get file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.ok(byteArray);
    }

    @PutMapping
    public ResponseEntity<Void> renameFile(@RequestParam(name = "filename") String oldFilename,
                                           @RequestBody RequestRenameFileDto renameFileDto){
        log.info("Method rename file in class {} started", getClass().getSimpleName());
        filesFacade.renameFile(oldFilename, renameFileDto);
        log.info("Method rename file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFile(@RequestParam String filename){
        log.info("Method delete file in class {} started", getClass().getSimpleName());
        filesFacade.deleteFile(filename);
        log.info("Method delete file in class {} finished", getClass().getSimpleName());
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
