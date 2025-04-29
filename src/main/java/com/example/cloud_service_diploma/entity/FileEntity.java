package com.example.cloud_service_diploma.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Arrays;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    UserEntity userId;

    @Column(name = "file_name", nullable = false)
    String fileName;

    @Column(name = "hash", nullable = false)
    String hash;

    @Column(name = "file")
    @Lob
    private byte[] file;

    @Column(name = "size", nullable = false)
    private Long size;

    @CreationTimestamp
    @Column(name = "file_upload_date", nullable = true)
    LocalDateTime fileUploadDate;

    public void setId(Long id) {
        this.id = id;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public FileEntity(UserEntity userId, String fileName, String hash, byte[] file, Long size, LocalDateTime fileUploadDate) {
        this.userId = userId;
        this.fileName = fileName;
        this.hash = hash;
        this.file = file;
        this.size = size;
        this.fileUploadDate = fileUploadDate;
    }

    public String getHash() {
        return hash;
    }

    public String getFileName() {
        return fileName;
    }

    public byte[] getFile() {
        return file;
    }

    public Long getId() {
        return id;
    }

    public Long getSize() {
        return size;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "FileEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", fileName='" + fileName + '\'' +
                ", hash='" + hash + '\'' +
                ", file=" + Arrays.toString(file) +
                ", size=" + size +
                ", fileUploadDate=" + fileUploadDate +
                '}';
    }

    public static FileEntity build(UserEntity userId, String fileName, String hash, byte[] file, Long size, LocalDateTime fileUploadDate) {
        return new FileEntity(userId, fileName, hash, file, size, fileUploadDate);
    }
}