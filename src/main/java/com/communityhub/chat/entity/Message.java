package com.communityhub.chat.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="messages")
public class Message {

    @Id
    private String id;

    private String communityId;

    private String senderId;

    private String content;

    private List<String> attachments;

    private LocalDateTime createdAt;
}
