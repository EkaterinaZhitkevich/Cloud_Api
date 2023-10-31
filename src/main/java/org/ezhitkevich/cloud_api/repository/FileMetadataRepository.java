package org.ezhitkevich.cloud_api.repository;

import org.ezhitkevich.cloud_api.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FileMetadataRepository extends JpaRepository<FileMetadata, Long> {

    void deleteByFilenameAndExtension(String filename, String extension);

    @Query("from FileMetadata fm join fm.user u where u.login =:username")
    List<FileMetadata> findAllFilesByUsername(String username);

    @Query("from FileMetadata fm join fm.user u where  u.login = :username and fm.filename = :filename and fm.extension = :extension")
    Optional<FileMetadata> findByUsernameAndFilenameAndExtension(String username, String filename, String extension);
}
