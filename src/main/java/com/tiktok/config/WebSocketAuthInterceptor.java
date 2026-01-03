package com.tiktok.config;

import com.tiktok.service.JwtService;
import com.tiktok.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.tiktok.repository.UserRepository;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserRepository userRepository;

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest servletRequest = (ServletServerHttpRequest) request;
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            
            // Lấy token từ query parameter (SockJS gửi token trong URL)
            String token = httpRequest.getParameter("token");
            
            // Nếu không có trong query, thử lấy từ header
            if (token == null || token.isEmpty()) {
                String authHeader = httpRequest.getHeader("Authorization");
                if (authHeader != null && authHeader.startsWith("Bearer ")) {
                    token = authHeader.substring(7);
                }
            }
            
            if (token != null && !token.isEmpty()) {
                try {
                    String email = jwtService.extractUsername(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                    // Validate token
                    if (jwtService.isTokenValid(token, userDetails)) {
                        // Lấy userId từ token và lưu vào attributes để dùng sau
                        Long userId = userRepository.findByEmail(email).get().getId();
                        // Có thể lấy userId từ email hoặc từ token claims
                        // attributes.put("userId", userId);
                        log.info("WebSocket authentication successful for token");
                        return true;
                    } else {
                        log.warn("Invalid token for WebSocket connection");
                        response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                        return false;
                    }
                } catch (Exception e) {
                    log.error("Error validating token for WebSocket: {}", e.getMessage());
                    response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                    return false;
                }
            } else {
                log.warn("No token provided for WebSocket connection");
                response.setStatusCode(org.springframework.http.HttpStatus.FORBIDDEN);
                return false;
            }
        }
        
        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // Không cần xử lý gì sau handshake
    }
}