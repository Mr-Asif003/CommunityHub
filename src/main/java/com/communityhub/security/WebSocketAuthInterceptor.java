package com.communityhub.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

import lombok.RequiredArgsConstructor;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import org.springframework.stereotype.Component;

import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor
        implements HandshakeInterceptor {

    private final JwtUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) {

        if (request instanceof ServletServerHttpRequest servletRequest) {

            HttpServletRequest req =
                    servletRequest.getServletRequest();

            if (req.getCookies() != null) {

                for (Cookie cookie : req.getCookies()) {

                    if ("jwt".equals(cookie.getName())) {

                        String token = cookie.getValue();

                        if (jwtUtil.isTokenValid(token)) {

                            String email =
                                    jwtUtil.extractUsername(token);

                            attributes.put("email", email);

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {

    }
}