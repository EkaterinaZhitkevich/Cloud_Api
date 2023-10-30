package org.ezhitkevich.cloud_api.repository;

import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    void deleteByFilenameAndExtension(String filename, String extension);

    @Modifying
    @Query("update FileMetadata fm set fm.filename = :newFilename where fm.id = :id")
    void updateByFilename(String newFilename, Long id);

    @Query("from FileMetadata fm join fm.user u where u.login =:username")
    List<FileMetadata> findAllFilesByUsername(String username);

    @Query("from FileMetadata fm join fm.user u where u.login = :username and fm.filename = :filename")
    boolean findByUserAndFilename(String username, String filename);


    @Query("from FileMetadata fm join fm.user u where u.login = :username and fm.filename = :filename")
    Optional<FileMetadata> findByUsernameAndFilename(String username, String filename);
}
