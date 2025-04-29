package com.example.cloud_service_diploma.repositories;

import com.example.cloud_service_diploma.entity.FileEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<FileEntity, Long> {

    Optional<FileEntity> findFileByUserEntityIdAndFileName(Long userId, String fileName);

    Optional<FileEntity> findFileByUserEntityId(Long userId);

    //@Query(value = "select * from file_entity f where f.user_id = ?1 order by f.id desc limit ?2", nativeQuery = true)
    List<FileEntity> findFilesByUserIdWithLimit(Long userId, int limit);
}
