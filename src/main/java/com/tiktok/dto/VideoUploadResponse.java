package com.tiktok.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VideoUploadResponse {
    private String fileUrl;
    private String thumbUrl;
    private String publicId;
    private String format;
    private Long duration; // seconds
    private Long bytes;
    private Integer width;
    private Integer height;
    private String resourceType;
}