package com.communityhub.chat.entity;

import com.communityhub.chat.enums.MessageStatus;
import com.communityhub.chat.enums.MessageType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    private String communityId;

    private String senderId;
    private String senderName; // ✅ avoid extra DB calls

    private String content;

    private MessageType type;

    private List<String> attachments;

    private String replyToMessageId;

    private MessageStatus status;

    private Map<String, List<String>> reactions; // 👍 → [user1, user2]

    private boolean edited;   // ✅ edited flag
    private boolean deleted;  // soft delete

    private LocalDateTime createdAt;
}