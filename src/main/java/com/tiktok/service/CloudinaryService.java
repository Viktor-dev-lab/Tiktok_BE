package com.tiktok.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudinaryService {

  private final Cloudinary cloudinary;

  public Map<String, Object> uploadVideo(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty");
    }

    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("video/")) {
      throw new IllegalArgumentException("File must be a video");
    }

    String publicId = "videos/" + UUID.randomUUID();

    Map<String, Object> uploadParams = ObjectUtils.asMap(
        "resource_type", "video",
        "public_id", publicId,
        "folder", "tiktok_videos",
        "overwrite", true,
        "eager", java.util.Arrays.asList(
            new Transformation()
                .width(300)
                .height(300)
                .crop("pad")
                .audioCodec("none")),
        "eager_async", true,
        "chunk_size", 6_000_000);

    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

    log.info("Video uploaded: {}", uploadResult.get("secure_url"));

    return uploadResult;
  }

  public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
    if (file.isEmpty()) {
      throw new IllegalArgumentException("File is empty");
    }

    String publicId = "images/" + UUID.randomUUID();

    Map<String, Object> uploadParams = ObjectUtils.asMap(
        "resource_type", "image",
        "public_id", publicId,
        "folder", "tiktok_images",
        "overwrite", true,
        "transformation", new Transformation()
            .width(500)
            .height(500)
            .crop("limit"));

    Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);

    log.info("Image uploaded: {}", uploadResult.get("secure_url"));

    return uploadResult;
  }

  public Map<String, Object> deleteFile(String publicId, String resourceType) throws IOException {
    Map<String, Object> params = ObjectUtils.asMap(
        "resource_type", resourceType,
        "invalidate", true);

    Map<String, Object> result = cloudinary.uploader().destroy(publicId, params);

    log.info("File deleted: {}", publicId);

    return result;
  }

  public Map<String, Object> getFileInfo(String publicId, String resourceType) throws Exception {
    return cloudinary.api().resource(
        publicId,
        ObjectUtils.asMap("resource_type", resourceType));
  }
}
