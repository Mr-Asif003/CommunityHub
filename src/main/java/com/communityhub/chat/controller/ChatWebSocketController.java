package com.communityhub.chat.controller;

import com.communityhub.chat.dto.ChatMessageRequest;
import com.communityhub.chat.dto.ChatMessageResponse;
import com.communityhub.chat.service.ChatService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Handles both:
 *  - REST  GET /api/chat/{communityId}/messages  — load history on page open
 *  - STOMP     /app/chat.sendMessage             — send a new message via WebSocket
 *
 * NOTE: @RestController is correct here. Spring handles @MessageMapping methods
 * separately from the HTTP dispatcher, so mixing both in one class is fine.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * REST — fetch existing message history for a community.
     * Called once when the chat screen mounts.
     * Requires a valid JWT cookie (enforced by SecurityConfig + JwtFilter).
     */
    @GetMapping("/{communityId}/messages")
    public ResponseEntity<?> getMessages(@PathVariable String communityId) {

        List<ChatMessageResponse> messages =
                chatService.getCommunityMessages(communityId);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "data", messages
        ));
    }

    /**
     * WebSocket — receive a new chat message from a connected client.
     * The Authentication principal is set by JwtChannelInterceptor on CONNECT.
     *
     * Flow:
     *  1. Client publishes to /app/chat.sendMessage
     *  2. This method saves it to MongoDB
     *  3. Broadcasts the saved response to /topic/community/{id}
     *  4. All subscribers (including the sender) receive it via WebSocket
     *
     * ✅ The sender should NOT optimistically add the message to their own UI —
     *    they will receive it back through the broadcast like everyone else.
     *    This prevents duplicates.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(
            @Payload ChatMessageRequest request,
            Authentication auth
    ) {
        if (auth == null || auth.getName() == null) {
            log.warn("Unauthenticated WebSocket message attempt — ignoring");
            return;
        }

        String senderEmail = auth.getName();

        log.debug("Message from {} to community {}", senderEmail, request.getCommunityId());

        ChatMessageResponse response =
                chatService.saveMessage(request, senderEmail);

        // Broadcast to ALL subscribers including the sender
        messagingTemplate.convertAndSend(
                "/topic/community/" + request.getCommunityId(),
                response
        );
    }
}