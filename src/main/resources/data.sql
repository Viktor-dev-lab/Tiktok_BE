-- TikTok Clone Database Initialization Script

-- Create Database
CREATE DATABASE IF NOT EXISTS tiktok_db 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE tiktok_db;

-- Insert Sample Users
INSERT INTO users (id, first_name, last_name, nickname, email, password, avatar, bio, tick, followings_count, followers_count, likes_count, website_url, created_at, updated_at) 
VALUES 
(1, 'Hoàng', 'Minh', 'SunMinh🤖', 'hoangminh@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'https://example.com/avatar1.jpg', 'Trang này chứa thông tin chi tiết của một nhân tài 🤫🤫', TRUE, 1, 384, 7070, 'https://www.facebook.com/nhm.fb.me/', NOW(), NOW()),
(2, 'Nguyễn', 'An', 'NguyenAn', 'nguyenan@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'https://example.com/avatar2.jpg', 'Content creator 🎬', TRUE, 150, 5200, 12000, 'https://example.com', NOW(), NOW()),
(3, 'Trần', 'Bình', 'TranBinh', 'tranbinh@example.com', '$2a$10$abcdefghijklmnopqrstuvwxyz', 'https://example.com/avatar3.jpg', 'Yêu âm nhạc 🎵', FALSE, 50, 850, 3000, NULL, NOW(), NOW());

-- Insert Sample Videos for User 1 (SunMinh🤖)
INSERT INTO videos (user_id, type, thumb_url, file_url, description, music, is_liked, likes_count, comments_count, shares_count, views_count, viewable, file_format, mime_type, resolution_x, resolution_y, published_at, created_at, updated_at)
VALUES 
(1, '', 'https://example.com/thumb1.jpg', 'https://example.com/video1.mp4', 'Em quay di ta "mat nhau" 🫢🫢 #spotify #xuhuong #foryou #wrongtimes #nhachaymoingay🎧🎶🎵', 'Wrong Times - Puppy & Dangrangto', TRUE, 904, 73, 3, 1302, 'public', 'mp4', 'video/mp4', 768, 1071, NOW(), NOW(), NOW()),
(1, '', 'https://example.com/thumb2.jpg', 'https://example.com/video2.mp4', 'Slightly warmer 💓 #fyp #xuhuong #detoiomembanggiaidieunay #kaidinh #min #greyd #nhachaymoingay', 'Để tôi ôm em bằng giai điệu này - Kai Đinh & MIN & GREY D', TRUE, 1094, 163, 17, 1524, 'public', 'mp4', 'video/mp4', 768, 1071, NOW(), NOW(), NOW()),
(1, '', 'https://example.com/thumb3.jpg', 'https://example.com/video3.mp4', 'A little peaceful 💓 #hoangdung #nepvaoanhvangheanhhat #amnhac #nhachay #nhacnaychillphet', 'Nép vào anh và nghe anh hát - Cover - Hoàng Dũng', TRUE, 999, 202, 29, 1408, 'public', 'mp4', 'video/mp4', 768, 1071, NOW(), NOW(), NOW()),
(1, '', 'https://example.com/thumb4.jpg', 'https://example.com/video4.mp4', 'Anh biết em muốn gì nèk 🤫 | #hoanhao #bray #nhacnaynghelanghien #trumnetwork #music', 'Hoàn Hảo - B Ray', TRUE, 1404, 308, 18, 2002, 'public', 'mp4', 'video/mp4', 768, 1071, NOW(), NOW(), NOW()),
(1, '', 'https://example.com/thumb5.jpg', 'https://example.com/video5.mp4', 'Chân thành anh đã đổi được gì... | Sáng tác | #nhactamtrang #nhachaymoingay #nhacchill #music', 'nhạc nền - SunMinh🤖', TRUE, 670, 73, 11, 968, 'public', 'mp4', 'video/mp4', 768, 1071, NOW(), NOW(), NOW());

-- Insert Sample Videos for User 2
INSERT INTO videos (user_id, type, thumb_url, file_url, description, music, is_liked, likes_count, comments_count, shares_count, views_count, viewable, file_format, mime_type, resolution_x, resolution_y, published_at, created_at, updated_at)
VALUES 
(2, '', 'https://example.com/thumb6.jpg', 'https://example.com/video6.mp4', 'Dancing in the rain ☔️ #dance #trending #viral', 'Original Sound - NguyenAn', FALSE, 2500, 450, 78, 8900, 'public', 'mp4', 'video/mp4', 1080, 1920, NOW(), NOW(), NOW()),
(2, '', 'https://example.com/thumb7.jpg', 'https://example.com/video7.mp4', 'Cooking tutorial 🍳 #cooking #food #tutorial', 'Chill Music - Background', FALSE, 1800, 220, 45, 5600, 'public', 'mp4', 'video/mp4', 1080, 1920, NOW(), NOW(), NOW());

-- Insert Sample Videos for User 3
INSERT INTO videos (user_id, type, thumb_url, file_url, description, music, is_liked, likes_count, comments_count, shares_count, views_count, viewable, file_format, mime_type, resolution_x, resolution_y, published_at, created_at, updated_at)
VALUES 
(3, '', 'https://example.com/thumb8.jpg', 'https://example.com/video8.mp4', 'Morning vibe ☀️ #morning #goodvibes #music', 'Morning Coffee - Acoustic', FALSE, 650, 89, 12, 1200, 'public', 'mp4', 'video/mp4', 720, 1280, NOW(), NOW(), NOW());

-- Insert Video Allows
INSERT INTO video_allows (video_id, allow_type) VALUES
(1, 'comment'), (1, 'duet'), (1, 'stitch'),
(2, 'comment'), (2, 'duet'), (2, 'stitch'),
(3, 'comment'), (3, 'duet'), (3, 'stitch'),
(4, 'comment'), (4, 'duet'), (4, 'stitch'),
(5, 'comment'), (5, 'duet'), (5, 'stitch'),
(6, 'comment'), (6, 'duet'),
(7, 'comment'), (7, 'stitch'),
(8, 'comment'), (8, 'duet'), (8, 'stitch');

-- Insert Sample Comments
INSERT INTO comments (video_id, user_id, content, likes_count, created_at)
VALUES 
(1, 2, 'Video hay quá! 😍', 15, NOW()),
(1, 3, 'Nhạc chill phết ❤️', 8, NOW()),
(2, 3, 'Mê bài này lắm 🎵', 12, NOW()),
(3, 2, 'Cover nghe hay hơn bản gốc luôn!', 25, NOW());

-- Verify data
SELECT 'Users Count:' as info, COUNT(*) as count FROM users
UNION ALL
SELECT 'Videos Count:', COUNT(*) FROM videos
UNION ALL
SELECT 'Comments Count:', COUNT(*) FROM comments;
