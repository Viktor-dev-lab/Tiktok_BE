package com.tiktok.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

  private Long id;
  private String firstName;
  private String lastName;
  private String nickname;
  private String email;
  private String avatar;
  private String bio;
  private Boolean tick;

  @JsonProperty("followingsCount")
  private Integer followingsCount;

  @JsonProperty("followersCount")
  private Integer followersCount;

  @JsonProperty("likesCount")
  private Integer likesCount;

  private String websiteUrl;
  private String facebookUrl;
  private String youtubeUrl;
  private String twitterUrl;
  private String instagramUrl;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  @JsonProperty("fullName")
  public String getFullName() {
    return firstName + " " + lastName;
  }
}