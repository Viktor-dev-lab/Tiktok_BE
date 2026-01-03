# Hướng dẫn tích hợp Backend với Frontend

## 🔗 Cấu hình Frontend để kết nối Backend

### 1. Tạo file cấu hình HTTP Request

Tạo file `src/utils/httpRequest.js`:

```javascript
import axios from 'axios';

const httpRequest = axios.create({
    baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
    timeout: 10000,
    headers: {
        'Content-Type': 'application/json',
    }
});

// Request interceptor
httpRequest.interceptors.request.use(
    (config) => {
        // Có thể thêm token vào header ở đây
        // const token = localStorage.getItem('token');
        // if (token) {
        //     config.headers.Authorization = `Bearer ${token}`;
        // }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor
httpRequest.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        console.error('API Error:', error);
        return Promise.reject(error);
    }
);

export const get = httpRequest.get;
export const post = httpRequest.post;
export const put = httpRequest.put;
export const del = httpRequest.delete;

export default httpRequest;
```

### 2. Cập nhật Video Service

Sửa file `src/services/videoService.js`:

```javascript
import * as httpRequest from '~/utils/httpRequest';

export const videoService = {
    // Lấy videos gợi ý
    getSuggestVideo: async (page = 1, size = 5) => {
        try {
            const res = await httpRequest.get('/videos/suggest', {
                params: { page, size }
            });
            return res.data.data;
        } catch (error) {
            console.log('Error fetching suggest videos:', error);
            return [];
        }
    },

    // Lấy tất cả videos
    getAllVideos: async () => {
        try {
            const res = await httpRequest.get('/videos');
            return res.data.data;
        } catch (error) {
            console.log('Error fetching all videos:', error);
            return [];
        }
    },

    // Lấy video theo ID
    getVideoById: async (id) => {
        try {
            const res = await httpRequest.get(`/videos/${id}`);
            return res.data.data;
        } catch (error) {
            console.log('Error fetching video:', error);
            return null;
        }
    },

    // Tìm kiếm videos
    searchVideos: async (query) => {
        try {
            const res = await httpRequest.get('/videos/search', {
                params: { query }
            });
            return res.data.data;
        } catch (error) {
            console.log('Error searching videos:', error);
            return [];
        }
    },

    // Like video
    likeVideo: async (id) => {
        try {
            const res = await httpRequest.post(`/videos/${id}/like`);
            return res.data.data;
        } catch (error) {
            console.log('Error liking video:', error);
            return null;
        }
    },

    // Tăng view count
    incrementView: async (id) => {
        try {
            const res = await httpRequest.post(`/videos/${id}/view`);
            return res.data.data;
        } catch (error) {
            console.log('Error incrementing view:', error);
            return null;
        }
    },

    // Tạo video mới
    createVideo: async (videoData) => {
        try {
            const res = await httpRequest.post('/videos', videoData);
            return res.data.data;
        } catch (error) {
            console.log('Error creating video:', error);
            throw error;
        }
    },

    // Cập nhật video
    updateVideo: async (id, videoData) => {
        try {
            const res = await httpRequest.put(`/videos/${id}`, videoData);
            return res.data.data;
        } catch (error) {
            console.log('Error updating video:', error);
            throw error;
        }
    },

    // Xóa video
    deleteVideo: async (id) => {
        try {
            const res = await httpRequest.del(`/videos/${id}`);
            return res.data;
        } catch (error) {
            console.log('Error deleting video:', error);
            throw error;
        }
    }
};
```

### 3. Cập nhật Search Service

Tạo file `src/services/searchService.js`:

```javascript
import * as httpRequest from '~/utils/httpRequest';

export const search = async (query, type = 'less') => {
    try {
        const res = await httpRequest.get('/users/search', {
            params: { query }
        });
        return res.data.data;
    } catch (error) {
        console.log('Error searching:', error);
        return [];
    }
};

export const searchVideos = async (query) => {
    try {
        const res = await httpRequest.get('/videos/search', {
            params: { query }
        });
        return res.data.data;
    } catch (error) {
        console.log('Error searching videos:', error);
        return [];
    }
};
```

### 4. Cập nhật User Service

Tạo file `src/services/userService.js`:

```javascript
import * as httpRequest from '~/utils/httpRequest';

export const userService = {
    // Lấy tất cả users
    getAllUsers: async () => {
        try {
            const res = await httpRequest.get('/users');
            return res.data.data;
        } catch (error) {
            console.log('Error fetching users:', error);
            return [];
        }
    },

    // Lấy user theo nickname
    getUserByNickname: async (nickname) => {
        try {
            const res = await httpRequest.get(`/users/@${nickname}`);
            return res.data.data;
        } catch (error) {
            console.log('Error fetching user:', error);
            return null;
        }
    },

    // Lấy user theo ID
    getUserById: async (id) => {
        try {
            const res = await httpRequest.get(`/users/${id}`);
            return res.data.data;
        } catch (error) {
            console.log('Error fetching user:', error);
            return null;
        }
    },

    // Tìm kiếm users
    searchUsers: async (query) => {
        try {
            const res = await httpRequest.get('/users/search', {
                params: { query }
            });
            return res.data.data;
        } catch (error) {
            console.log('Error searching users:', error);
            return [];
        }
    },

    // Follow user
    followUser: async (userId, targetUserId) => {
        try {
            const res = await httpRequest.post(`/users/${userId}/follow/${targetUserId}`);
            return res.data.data;
        } catch (error) {
            console.log('Error following user:', error);
            throw error;
        }
    },

    // Unfollow user
    unfollowUser: async (userId, targetUserId) => {
        try {
            const res = await httpRequest.del(`/users/${userId}/follow/${targetUserId}`);
            return res.data.data;
        } catch (error) {
            console.log('Error unfollowing user:', error);
            throw error;
        }
    },

    // Tạo user mới
    createUser: async (userData) => {
        try {
            const res = await httpRequest.post('/users', userData);
            return res.data.data;
        } catch (error) {
            console.log('Error creating user:', error);
            throw error;
        }
    },

    // Cập nhật user
    updateUser: async (id, userData) => {
        try {
            const res = await httpRequest.put(`/users/${id}`, userData);
            return res.data.data;
        } catch (error) {
            console.log('Error updating user:', error);
            throw error;
        }
    }
};
```

### 5. Cập nhật component Explore

Sửa file `src/pages/Explore/index.js`:

```javascript
import { useEffect, useRef, useState } from 'react';
import { InView } from 'react-intersection-observer';
import classNames from 'classnames/bind';

import styles from './Explore.module.scss';
import SuggestVideo from '~/components/Videos/SuggestVideo';
import { videoService } from '~/services/videoService';
import TiktokLoading from '~/components/TiktokLoading';
import SvgIcon from '~/components/SvgIcon';
import VideoContext from '~/Context/VideoContext';

const cx = classNames.bind(styles);

function Explore() {
    const [videoList, setVideoList] = useState([]);
    const [page, setPage] = useState(1);
    const [loading, setLoading] = useState(false);
    const [volume, setVolume] = useState(0.5);
    const [muted, setMuted] = useState(true);

    const inViewArr = useRef([]);

    const contextValue = {
        volumeState: [volume, setVolume],
        mutedState: [muted, setMuted],
        inViewArr: inViewArr.current,
    };

    useEffect(() => {
        const fetchVideoList = async () => {
            setLoading(true);
            try {
                const result = await videoService.getSuggestVideo(page, 5);
                setVideoList(prev => [...prev, ...result]);
            } catch (error) {
                console.error('Error loading videos:', error);
            } finally {
                setLoading(false);
            }
        };

        fetchVideoList();
    }, [page]);

    const handleLoadMore = () => {
        if (!loading) {
            setPage(prev => prev + 1);
        }
    };

    return (
        <VideoContext value={contextValue}>
            <div className={cx('wrapper')}>
                {videoList.map((video, index) => (
                    <InView key={video.id} threshold={0.8}>
                        {({ inView, ref: observeRef }) => (
                            <SuggestVideo 
                                ref={observeRef} 
                                isInView={inView} 
                                videoInfo={video} 
                                videoId={index} 
                            />
                        )}
                    </InView>
                ))}
                
                <InView onChange={(inView) => inView && handleLoadMore()}>
                    <SvgIcon 
                        className={cx('auto-load-more')} 
                        icon={<TiktokLoading />} 
                    />
                </InView>
            </div>
        </VideoContext>
    );
}

export default Explore;
```

### 6. Cập nhật component Profile

Sửa file `src/pages/Profile/index.js`:

```javascript
import { useState, useEffect, useContext } from 'react';
import { useParams } from 'react-router-dom';
import classNames from 'classnames/bind';

import styles from './Profile.module.scss';
import { userService } from '~/services/userService';
// ... rest of imports

const cx = classNames.bind(styles);

function Profile() {
    const { nickname } = useParams();
    const [userData, setUserData] = useState(null);
    const [videos, setVideos] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchUserData = async () => {
            setLoading(true);
            try {
                const data = await userService.getUserByNickname(nickname);
                setUserData(data.user);
                setVideos(data.videos);
            } catch (error) {
                console.error('Error loading user data:', error);
            } finally {
                setLoading(false);
            }
        };

        if (nickname) {
            fetchUserData();
        }
    }, [nickname]);

    if (loading) {
        return <div>Loading...</div>;
    }

    if (!userData) {
        return <div>User not found</div>;
    }

    return (
        // ... JSX với userData và videos
    );
}

export default Profile;
```

### 7. Biến môi trường

Tạo file `.env` trong thư mục frontend:

```env
REACT_APP_API_URL=http://localhost:8080/api
```

Cho production:

```env
REACT_APP_API_URL=https://your-production-api.com/api
```

## 🚀 Chạy ứng dụng

### Backend
```bash
cd tiktok-backend
mvn spring-boot:run
```
Backend chạy tại: http://localhost:8080

### Frontend
```bash
cd tiktok-frontend
npm start
```
Frontend chạy tại: http://localhost:3000

## 📝 Lưu ý

1. **CORS**: Backend đã được cấu hình để chấp nhận request từ mọi origin. Trong production nên giới hạn cụ thể.

2. **Error Handling**: Đã thêm try-catch trong tất cả API calls để xử lý lỗi gracefully.

3. **Loading States**: Nên thêm loading indicators khi fetch data từ API.

4. **Data Validation**: Backend đã có validation, frontend cũng nên validate trước khi gửi request.

5. **Authentication**: Hiện tại chưa có authentication. Cần implement JWT token để bảo mật API.

## 🔐 Bước tiếp theo

1. Implement JWT Authentication
2. Add image/video upload functionality
3. Add real-time notifications với WebSocket
4. Implement caching với Redis
5. Add rate limiting để tránh spam
