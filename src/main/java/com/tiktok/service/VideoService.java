package com.tiktok.service;

import com.tiktok.exception.ResourceNotFoundException;
import com.tiktok.model.Video;
import com.tiktok.model.User;
import com.tiktok.repository.VideoRepository;
import com.tiktok.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VideoService {

    private final VideoRepository videoRepository;
    private final UserRepository userRepository;

    public List<Video> getSuggestVideos(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Video> videoPage = videoRepository.findRandomPublicVideos(pageable);
        return videoPage.getContent();
    }

    public List<Video> getAllVideos() {
        return videoRepository.findAll();
    }

    public Video getVideoById(Long id) {
        return videoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Video not found with id: " + id));
    }

    public List<Video> getVideosByUserId(Long userId) {
        return videoRepository.findPublicVideosByUserId(userId);
    }

    public Video createVideo(Video video) {
        return videoRepository.save(video);
    }

    public Video updateVideo(Long id, Video videoDetails) {
        Video video = getVideoById(id);

        video.setDescription(videoDetails.getDescription());
        video.setMusic(videoDetails.getMusic());
        video.setThumbUrl(videoDetails.getThumbUrl());
        video.setFileUrl(videoDetails.getFileUrl());
        video.setViewable(videoDetails.getViewable());
        video.setAllows(videoDetails.getAllows());

        return videoRepository.save(video);
    }

    public void deleteVideo(Long id) {
        Video video = getVideoById(id);
        videoRepository.delete(video);
    }


    public Video incrementViewCount(Long id) {
        Video video = getVideoById(id);
        video.setViewsCount(video.getViewsCount() + 1);
        return videoRepository.save(video);
    }

    public List<Video> searchVideos(String query) {
        return videoRepository.searchVideos(query);
    }

    public Video likeVideo(Long videoId, Long likerId) {
        Video video = getVideoById(videoId);
        User videoOwner = video.getUser();

        // Toggle like status
        boolean wasLiked = video.getIsLiked();
        video.setIsLiked(!wasLiked);

        // Cập nhật likesCount của video
        if (!wasLiked) {
            // Like video
            video.setLikesCount(video.getLikesCount() + 1);
        } else {
            // Unlike video
            video.setLikesCount(Math.max(0, video.getLikesCount() - 1));
        }

        Video savedVideo = videoRepository.save(video);

        // Tính lại tổng likesCount của user (tổng likesCount của tất cả video của user
        // đó)
        updateUserTotalLikesCount(videoOwner.getId());

        return savedVideo;
    }

    /**
     * Tính lại tổng likesCount của user dựa trên tổng likesCount của tất cả video
     * của user đó
     */
    private void updateUserTotalLikesCount(Long userId) {
        List<Video> userVideos = videoRepository.findByUserId(userId);
        int totalLikes = userVideos.stream()
                .mapToInt(Video::getLikesCount)
                .sum();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        user.setLikesCount(totalLikes);
        userRepository.save(user);
    }
}
