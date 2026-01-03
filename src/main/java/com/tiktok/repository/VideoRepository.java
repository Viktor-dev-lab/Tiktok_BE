package com.tiktok.repository;

import com.tiktok.model.Video;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {
    
    Page<Video> findAllByOrderByCreatedAtDesc(Pageable pageable);
    
    List<Video> findByUserId(Long userId);
    
    @Query("SELECT v FROM Video v WHERE v.viewable = 'public' ORDER BY RAND()")
    Page<Video> findRandomPublicVideos(Pageable pageable);
    
    @Query("SELECT v FROM Video v WHERE " +
           "LOWER(v.description) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(v.music) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Video> searchVideos(@Param("query") String query);
    
    @Query("SELECT v FROM Video v WHERE v.user.id = :userId AND v.viewable = 'public'")
    List<Video> findPublicVideosByUserId(@Param("userId") Long userId);
}
