package com.communityhub.security;

import lombok.extern.slf4j.Slf4j;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;

import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class JwtChannelInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class
        );

        if (accessor == null) {
            return message;
        }

        // Only authenticate on CONNECT; subsequent frames inherit the user principal
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            var sessionAttributes = accessor.getSessionAttributes();

            if (sessionAttributes == null) {
                log.warn("WebSocket CONNECT arrived with no session attributes — handshake may have failed");
                return message;
            }

            String email = (String) sessionAttributes.get("email");

            if (email != null) {

                // ✅ FIX: Pass a non-null authorities list.
                // Passing null as the third arg means the token is considered
                // "unauthenticated" by Spring Security even though it carries a principal,
                // which can cause 403s on @MessageMapping methods that check authorities.
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                List.of(new SimpleGrantedAuthority("ROLE_USER"))
                        );

                accessor.setUser(auth);

                log.info("WebSocket authenticated: {}", email);

            } else {
                log.warn("WebSocket CONNECT — no email found in session attributes");
            }
        }

        return message;
    }
}