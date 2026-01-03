# TikTok Clone Backend - RESTful API

Backend Java Spring Boot cho ứng dụng TikTok Clone với RESTful API chuẩn.

## 🚀 Công nghệ sử dụng

- **Java 11**
- **Spring Boot 2.7.14**
- **Spring Data JPA**
- **MySQL 8.0**
- **Lombok**
- **SpringDoc OpenAPI (Swagger)**
- **Maven**

## 📋 Yêu cầu hệ thống

- JDK 11 trở lên
- MySQL 8.0
- Maven 3.6+
- IDE: IntelliJ IDEA / Eclipse / VS Code

## ⚙️ Cài đặt

### 1. Clone repository

```bash
git clone <repository-url>
cd tiktok-backend
```

### 2. Cấu hình Database

Tạo database MySQL:

```sql
CREATE DATABASE tiktok_db;
```

Cập nhật file `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/tiktok_db
spring.datasource.username=root
spring.datasource.password=your_password
```

### 3. Build và chạy ứng dụng

```bash
# Build project
mvn clean install

# Chạy ứng dụng
mvn spring-boot:run
```

Ứng dụng sẽ chạy tại: `http://localhost:8080`

## 📚 API Documentation

### Swagger UI

Truy cập Swagger UI để xem và test API:

```
http://localhost:8080/swagger-ui.html
```

### API Endpoints

#### 🎥 Video APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/videos` | Lấy tất cả videos |
| GET | `/api/videos/suggest?page=1&size=5` | Lấy videos gợi ý (random) |
| GET | `/api/videos/{id}` | Lấy video theo ID |
| GET | `/api/videos/search?query=keyword` | Tìm kiếm video |
| POST | `/api/videos` | Tạo video mới |
| PUT | `/api/videos/{id}` | Cập nhật video |
| DELETE | `/api/videos/{id}` | Xóa video |
| POST | `/api/videos/{id}/like` | Like/Unlike video |
| POST | `/api/videos/{id}/view` | Tăng view count |

#### 👤 User APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Lấy tất cả users |
| GET | `/api/users/{id}` | Lấy user theo ID |
| GET | `/api/users/@{nickname}` | Lấy user theo nickname |
| GET | `/api/users/search?query=keyword` | Tìm kiếm user |
| POST | `/api/users` | Tạo user mới |
| PUT | `/api/users/{id}` | Cập nhật user |
| DELETE | `/api/users/{id}` | Xóa user |
| POST | `/api/users/{userId}/follow/{targetUserId}` | Follow user |
| DELETE | `/api/users/{userId}/follow/{targetUserId}` | Unfollow user |


### Comment
POST /api/videos/{videoId}/comments?user_id={userId}&content={content} - Tạo comment
GET /api/videos/{videoId}/comments - Lấy danh sách comments
POST /api/videos/{videoId}/comments/{commentId}/replies?user_id={userId}&content={content} - Reply comment
POST /api/videos/{videoId}/comments/{commentId}/like?user_id={userId} - Like comment
DELETE /api/videos/{videoId}/comments/{commentId}/like - Unlike comment
DELETE /api/videos/{videoId}/comments/{commentId} - Xóa comment
Khi có comment mới:
Tăng comments_count của video
Gửi WebSocket notification đến chủ video (nếu không phải chính họ)
Gửi WebSocket notification khi có reply (đến chủ comment gốc)

### Chat
Get User by Email
- httpGET /api/users/email/{email}
2. Chat APIs
Get Chat List (Danh sách cuộc hội thoại)
- httpGET /api/chats?userId=1
Get Chat Detail (Chi tiết cuộc hội thoại)
- httpGET /api/chats/detail?userId=1&otherUserId=2
Send Message (Gửi tin nhắn)
- httpPOST /api/chats/messages?senderId=1
Mark Messages as Read (Đánh dấu đã đọc)
- httpPUT /api/chats/mark-read?userId=1&otherUserId=2
3. Message APIs
Get All Messages Between Users
- httpGET /api/messages?userId1=1&userId2=2
Get Unread Count
- httpGET /api/messages/unread-count?userId=1&otherUserId=2

### 📝 Request/Response Examples

#### Get Suggested Videos

**Request:**
```bash
GET /api/videos/suggest?page=1&size=5
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": [
    {
      "id": 1,
      "userId": 1,
      "type": "",
      "thumbUrl": "https://example.com/thumb.jpg",
      "fileUrl": "https://example.com/video.mp4",
      "description": "Em quay di ta mat nhau 🫢🫢",
      "music": "Wrong Times - Puppy & Dangrangto",
      "isLiked": false,
      "likesCount": 904,
      "commentsCount": 73,
      "sharesCount": 3,
      "viewsCount": 1302,
      "viewable": "public",
      "allows": ["comment", "duet", "stitch"],
      "user": {
        "id": 1,
        "firstName": "Hoàng",
        "lastName": "Minh",
        "nickname": "SunMinh🤖",
        "avatar": "https://example.com/avatar.jpg",
        "bio": "Trang này chứa thông tin chi tiết của một nhân tài 🤫🤫",
        "tick": true,
        "followingsCount": 1,
        "followersCount": 384,
        "likesCount": 7070
      },
      "meta": {
        "fileFormat": "mp4",
        "mimeType": "video/mp4",
        "video": {
          "resolutionX": 768,
          "resolutionY": 1071
        }
      }
    }
  ]
}
```

#### Get User Profile with Videos

**Request:**
```bash
GET /api/users/@SunMinh🤖
```

**Response:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "user": {
      "id": 1,
      "firstName": "Hoàng",
      "lastName": "Minh",
      "nickname": "SunMinh🤖",
      "email": "user@example.com",
      "avatar": "https://example.com/avatar.jpg",
      "bio": "Trang này chứa thông tin chi tiết của một nhân tài 🤫🤫",
      "tick": true,
      "followingsCount": 1,
      "followersCount": 384,
      "likesCount": 7070,
      "websiteUrl": "https://www.facebook.com/nhm.fb.me/"
    },
    "videos": [...]
  }
}
```

#### Create Video

**Request:**
```bash
POST /api/videos
Content-Type: application/json

{
  "userId": 1,
  "thumbUrl": "https://example.com/thumb.jpg",
  "fileUrl": "https://example.com/video.mp4",
  "description": "My new video",
  "music": "Song name",
  "viewable": "public",
  "allows": ["comment", "duet", "stitch"],
  "meta": {
    "fileFormat": "mp4",
    "mimeType": "video/mp4",
    "video": {
      "resolutionX": 1080,
      "resolutionY": 1920
    }
  }
}
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    nickname VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    avatar TEXT,
    bio TEXT,
    tick BOOLEAN DEFAULT FALSE,
    followings_count INT DEFAULT 0,
    followers_count INT DEFAULT 0,
    likes_count INT DEFAULT 0,
    website_url VARCHAR(255),
    facebook_url VARCHAR(255),
    youtube_url VARCHAR(255),
    twitter_url VARCHAR(255),
    instagram_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Videos Table
```sql
CREATE TABLE videos (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    type VARCHAR(50),
    thumb_url TEXT,
    file_url TEXT NOT NULL,
    description TEXT,
    music TEXT,
    is_liked BOOLEAN DEFAULT FALSE,
    likes_count INT DEFAULT 0,
    comments_count INT DEFAULT 0,
    shares_count INT DEFAULT 0,
    views_count INT DEFAULT 0,
    viewable VARCHAR(20) DEFAULT 'public',
    file_format VARCHAR(10),
    mime_type VARCHAR(50),
    resolution_x INT,
    resolution_y INT,
    published_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);
```

## 🔧 Tích hợp Frontend

### Update Frontend Service

Cập nhật file `src/services/videoService.js` trong frontend:

```javascript
import * as httpRequest from '~/utils/httpRequest';

export const getSuggestVideo = async (page = 1, size = 5) => {
    try {
        const res = await httpRequest.get('/videos/suggest', {
            params: { page, size }
        });
        return res.data.data;
    } catch (error) {
        console.log(error);
        return [];
    }
};
```

### CORS Configuration

Backend đã được cấu hình CORS cho phép tất cả origins. Trong production, nên giới hạn origins cụ thể:

```java
registry.addMapping("/api/**")
        .allowedOrigins("http://localhost:3000", "https://yourdomain.com")
        .allowedMethods("GET", "POST", "PUT", "DELETE")
```

## 🛠️ Development

### Chạy ở môi trường Development

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Hot Reload

Spring Boot DevTools đã được cấu hình để tự động reload khi có thay đổi code.

## 📦 Deployment

### Build JAR file

```bash
mvn clean package
```

JAR file sẽ được tạo tại: `target/tiktok-backend-1.0.0.jar`

### Chạy JAR file

```bash
java -jar target/tiktok-backend-1.0.0.jar
```

## 🔒 Security (TODO)

Hiện tại API chưa có authentication. Để thêm security:

1. Thêm Spring Security dependency
2. Implement JWT authentication
3. Thêm User authentication và authorization

## 📄 License

MIT License

## 👨‍💻 Author

TikTok Clone Backend API
