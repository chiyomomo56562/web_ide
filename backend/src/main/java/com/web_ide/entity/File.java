package com.web_ide.entity;

import jakarta.persistence.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 64)
    private String name;

    @Column(name = "file_path", length = 255, nullable = false)
    private String filePath;

    @Column(name ="extension",nullable = false, length = 10)
    private String extension;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Builder
    public File(String name, String extension, String filePath) {
        this.name = name;
        this.extension = extension;
        this.filePath = filePath;
    }
}
