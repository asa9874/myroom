package com.example.myroom.global.jwt;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JwtHandshakeInterceptor implements HandshakeInterceptor {
    
    private final JwtTokenProvider jwtTokenProvider;
    
    public JwtHandshakeInterceptor(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                 WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        // Query parameter에서 토큰 추출
        String token = getTokenFromQuery(request);
        
        if (token != null && jwtTokenProvider.validateToken(token)) {
            // 토큰이 유효하면 사용자 정보를 attributes에 저장
            String email = jwtTokenProvider.getEmailFromToken(token);
            String role = jwtTokenProvider.getRoleFromToken(token);
            Long userId = jwtTokenProvider.getIdFromToken(token);
            
            attributes.put("email", email);
            attributes.put("role", role);
            attributes.put("userId", userId);
            return true;
        }
        log.error("No token found in query parameters");
        return false; // 인증 실패시 연결 거부
    }
    
    private String getTokenFromQuery(ServerHttpRequest request) {
        String query = request.getURI().getQuery();
        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                if (param.startsWith("token=")) {
                    return param.substring(6); // "token=" 제거
                }
            }
        }
        return null;
    }
    
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                             WebSocketHandler wsHandler, Exception exception) {
        // 핸드셰이크 후 처리 (필요시)
    }
}