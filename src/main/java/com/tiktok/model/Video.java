package com.tiktok.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "videos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Video {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "user_id", insertable = false, updatable = false)
    @JsonProperty("user_id")
    private Long userId;
    
    private String type;
    
    @Column(columnDefinition = "TEXT")
    private String thumbUrl;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String fileUrl;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String music;
    
    @Column(nullable = false)
    private Boolean isLiked = false;
    
    @Column(nullable = false)
    private Integer likesCount = 0;
    
    @Column(nullable = false)
    private Integer commentsCount = 0;
    
    @Column(nullable = false)
    private Integer sharesCount = 0;
    
    @Column(nullable = false)
    private Integer viewsCount = 0;
    
    @Column(nullable = false)
    private String viewable = "public";
    
    @ElementCollection
    @CollectionTable(name = "video_allows", joinColumns = @JoinColumn(name = "video_id"))
    @Column(name = "allow_type")
    private List<String> allows = new ArrayList<>();
    
    @Embedded
    private VideoMeta meta;
    
    private LocalDateTime publishedAt;
    
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        if (publishedAt == null) {
            publishedAt = LocalDateTime.now();
        }
        if (allows == null || allows.isEmpty()) {
            allows = List.of("comment", "duet", "stitch");
        }
    }
}
