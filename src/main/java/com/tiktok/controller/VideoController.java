package com.tiktok.controller;

import com.tiktok.dto.ApiResponse;
import com.tiktok.dto.NotificationDTO;
import com.tiktok.dto.VideoUploadResponse;
import com.tiktok.model.User;
import com.tiktok.model.Video;
import com.tiktok.model.VideoMeta;
import com.tiktok.service.CloudinaryService;
import com.tiktok.service.UserService;
import com.tiktok.service.VideoService;
import com.tiktok.service.WebSocketNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Video", description = "Video management APIs")
@Slf4j
public class VideoController {

    private final VideoService videoService;
    private final CloudinaryService cloudinaryService;
    private final UserService userService;
    private final WebSocketNotificationService webSocketNotificationService;

    @GetMapping
    @Operation(summary = "Get all videos", description = "Retrieve a list of all videos")
    public ResponseEntity<ApiResponse<List<Video>>> getAllVideos() {
        List<Video> videos = videoService.getAllVideos();
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    @GetMapping("/suggest")
    @Operation(summary = "Get suggested videos", description = "Get random suggested videos for feed")
    public ResponseEntity<ApiResponse<List<Video>>> getSuggestVideos(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "5") int size) {
        List<Video> videos = videoService.getSuggestVideos(page, size);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get video by ID", description = "Retrieve a specific video by its ID")
    public ResponseEntity<ApiResponse<Video>> getVideoById(@PathVariable Long id) {
        Video video = videoService.getVideoById(id);
        return ResponseEntity.ok(ApiResponse.success(video));
    }

    @PostMapping
    @Operation(summary = "Create video", description = "Upload a new video")
    public ResponseEntity<ApiResponse<Video>> createVideo(@Valid @RequestBody Video video) {
        Video createdVideo = videoService.createVideo(video);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Video created successfully", createdVideo));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update video", description = "Update an existing video")
    public ResponseEntity<ApiResponse<Video>> updateVideo(
            @PathVariable Long id,
            @Valid @RequestBody Video videoDetails) {
        Video updatedVideo = videoService.updateVideo(id, videoDetails);
        return ResponseEntity.ok(ApiResponse.success("Video updated successfully", updatedVideo));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete video", description = "Delete a video by ID")
    public ResponseEntity<ApiResponse<Void>> deleteVideo(@PathVariable Long id) {
        videoService.deleteVideo(id);
        return ResponseEntity.ok(ApiResponse.success("Video deleted successfully", null));
    }

    @PostMapping("/{id}/like")
    @Operation(summary = "Like/Unlike video", description = "Toggle like status for a video")
    public ResponseEntity<ApiResponse<Video>> likeVideo(
            @PathVariable Long id,
            @RequestParam("liker_id") Long likerId) { 
        
        // Lấy thông tin người tim
        User liker = userService.getUserById(likerId);
        
        // Like/Unlike video
        Video video = videoService.likeVideo(id, likerId);
        
        // Gửi notification real-time qua WebSocket nếu đã like (không phải unlike)
        if (video.getIsLiked()) {
            User videoOwner = video.getUser();
            // Chỉ gửi notification nếu không phải chính chủ video tim video của mình
            if (!videoOwner.getId().equals(likerId)) {
                webSocketNotificationService.sendLikeNotification(
                        videoOwner.getId(),
                        likerId,
                        liker.getFullName(),
                        video.getId(),
                        video.getThumbUrl()
                );
            }
        }
        
        return ResponseEntity.ok(ApiResponse.success(video));
    }

    @PostMapping("/{id}/view")
    @Operation(summary = "Increment view count", description = "Increment the view count for a video")
    public ResponseEntity<ApiResponse<Video>> incrementViewCount(@PathVariable Long id) {
        Video video = videoService.incrementViewCount(id);
        return ResponseEntity.ok(ApiResponse.success(video));
    }

    @GetMapping("/search")
    @Operation(summary = "Search videos", description = "Search videos by description or music")
    public ResponseEntity<ApiResponse<List<Video>>> searchVideos(@RequestParam String query) {
        List<Video> videos = videoService.searchVideos(query);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Video>>> getVideosByUser(@PathVariable Long userId) {
        List<Video> videos = videoService.getVideosByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success(videos));
    }

    /*** Upload video */
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadVideo(
            @RequestParam("file") MultipartFile file,
            @RequestParam("user_id") Long userId,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "music", required = false) String music,
            @RequestParam(value = "viewable", defaultValue = "public") String viewable,
            @RequestParam(value = "type", defaultValue = "video") String type) {
        try {
            // Validate file size (max 100MB)
            if (file.getSize() > 100 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "File size must be less than 100MB"));
            }

            // Lấy user từ DB (user_id)
            User user = userService.getUserById(userId);

            // Upload lên Cloudinary
            Map<String, Object> uploadResult = cloudinaryService.uploadVideo(file);

            // Build meta (file_format, mime_type, resolution_x, resolution_y)
            VideoMeta.VideoResolution resolution = new VideoMeta.VideoResolution(
                    (Integer) uploadResult.get("width"),
                    (Integer) uploadResult.get("height"));

            VideoMeta meta = new VideoMeta();
            meta.setFileFormat((String) uploadResult.get("format"));
            meta.setMimeType(file.getContentType());
            meta.setVideo(resolution);

            // Tạo entity Video để lưu DB
            Video video = new Video();
            video.setUser(user);
            video.setType(type);
            video.setThumbUrl((String) uploadResult.get("url")); // thumb_url
            video.setFileUrl((String) uploadResult.get("secure_url")); // file_url
            video.setDescription(description);
            video.setMusic(music);
            video.setViewable(viewable);
            video.setMeta(meta);
            // is_liked, likes_count, comments_count, shares_count, views_count,
            // published_at,
            // created_at, updated_at sẽ dùng default + @PrePersist/@CreatedDate

            // Lưu vào DB
            Video savedVideo = videoService.createVideo(video);

            // Tạo response upload (giữ nguyên DTO cũ)
            VideoUploadResponse uploadResponse = VideoUploadResponse.builder()
                    .fileUrl((String) uploadResult.get("secure_url"))
                    .thumbUrl((String) uploadResult.get("url"))
                    .publicId((String) uploadResult.get("public_id"))
                    .format((String) uploadResult.get("format"))
                    .duration(((Number) uploadResult.get("duration")).longValue())
                    .bytes(((Number) uploadResult.get("bytes")).longValue())
                    .width((Integer) uploadResult.get("width"))
                    .height((Integer) uploadResult.get("height"))
                    .resourceType((String) uploadResult.get("resource_type"))
                    .build();

            Map<String, Object> data = new HashMap<>();
            data.put("upload", uploadResponse);
            data.put("video", savedVideo); 

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Video uploaded and saved successfully",
                    "data", data));

        } catch (IllegalArgumentException e) {
            log.error("Invalid file: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading video: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to upload video: " + e.getMessage()));
        }
    }

    /*** Upload image/thumbnail */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.getSize() > 10 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Image size must be less than 10MB"));
            }

            Map<String, Object> uploadResult = cloudinaryService.uploadImage(file);

            Map<String, Object> response = new HashMap<>();
            response.put("url", uploadResult.get("secure_url"));
            response.put("publicId", uploadResult.get("public_id"));
            response.put("format", uploadResult.get("format"));
            response.put("width", uploadResult.get("width"));
            response.put("height", uploadResult.get("height"));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Image uploaded successfully",
                    "data", response));

        } catch (Exception e) {
            log.error("Error uploading image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to upload image: " + e.getMessage()));
        }
    }

    /*** Xóa file */
    @DeleteMapping("/{resourceType}/{publicId}")
    public ResponseEntity<?> deleteFile(
            @PathVariable String resourceType,
            @PathVariable String publicId) {
        try {
            Map<String, Object> result = cloudinaryService.deleteFile(publicId, resourceType);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "File deleted successfully",
                    "data", result));

        } catch (Exception e) {
            log.error("Error deleting file: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Failed to delete file: " + e.getMessage()));
        }
    }
}
