package org.ezhitkevich.authorization_service.repository;

import io.minio.PutObjectArgs;
import org.ezhitkevich.authorization_service.model.MinioFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MinioFileRepository extends JpaRepository<MinioFile, Long> {

    void deleteByFilenameAndExtension(String filename, String extension);

    @Modifying
    @Query("update MinioFile m set  m.filename =:newFilename where m.filename =:oldFilename")
    void updateByFilename(String newFilename, String oldFilename);

    @Query("from MinioFile join User u where u.login = :username")
    List<MinioFile> findAllFilesByUsername(String username);

    @Query("from MinioFile m join User u where u.login =:username and m.filename =:filename")
    boolean findByUserAndFilename(String username, String filename);

}
