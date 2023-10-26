package org.ezhitkevich.cloud_api.repository;

import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    void deleteByFilenameAndExtension(String filename, String extension);

    @Modifying
    @Query("update FileMetadata m set  m.filename =:newFilename where m.filename =:oldFilename")
    void updateByFilename(String newFilename, String oldFilename);

    @Query("from FileMetadata join User u where u.login = :username")
    List<FileMetadata> findAllFilesByUsername(String username);

    @Query("from FileMetadata m join User u where u.login =:username and m.filename =:filename")
    boolean findByUserAndFilename(String username, String filename);

}
