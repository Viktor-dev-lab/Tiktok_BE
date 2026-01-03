package com.tiktok.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.persistence.Embedded;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoMeta {
    
    private String fileFormat;
    private String mimeType;
    
    @Embedded
    private VideoResolution video;
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class VideoResolution {
        private Integer resolutionX;
        private Integer resolutionY;
    }
}
