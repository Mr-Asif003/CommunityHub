package com.communityhub.chat.service;

import com.communityhub.chat.dto.ChatMessageRequest;
import com.communityhub.chat.dto.ChatMessageResponse;
import com.communityhub.chat.entity.CommunityMessage;
import com.communityhub.chat.entity.MessageType;
import com.communityhub.chat.repository.CommunityMessageRepository;
import com.communityhub.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final CommunityMessageRepository messageRepo;
    private final UserRepository userRepository;

    /**
     * Persists a new chat message to MongoDB and returns the saved document
     * mapped to a response DTO.
     */
    public ChatMessageResponse saveMessage(ChatMessageRequest req, String senderEmail) {

        // Resolve display name from DB; fall back to email if user not found
        String senderName = userRepository.findByEmail(senderEmail)
                .map(user -> user.getFullName())
                .orElse(senderEmail);

        CommunityMessage message = CommunityMessage.builder()
                .communityId(req.getCommunityId())
                .content(req.getContent())
                .senderEmail(senderEmail)
                .senderName(senderName)
                .type(MessageType.CHAT)
                .createdAt(LocalDateTime.now())
                .build();

        CommunityMessage saved = messageRepo.save(message);

        log.debug("Saved message {} for community {}", saved.getId(), saved.getCommunityId());

        return toResponse(saved);
    }

    /**
     * Returns all messages for a community ordered oldest-first.
     * Called by the REST endpoint when the chat screen loads.
     */
    public List<ChatMessageResponse> getCommunityMessages(String communityId) {

        return messageRepo
                .findByCommunityIdOrderByCreatedAtAsc(communityId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ── private helpers ──────────────────────────────────────────────────────

    private ChatMessageResponse toResponse(CommunityMessage msg) {
        return ChatMessageResponse.builder()
                .id(msg.getId())
                .communityId(msg.getCommunityId())
                .senderEmail(msg.getSenderEmail())
                .senderName(msg.getSenderName())
                .content(msg.getContent())
                .type(msg.getType())
                .createdAt(msg.getCreatedAt())
                .build();
    }
}