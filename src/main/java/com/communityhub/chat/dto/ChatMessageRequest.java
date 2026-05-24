package com.communityhub.chat.dto;

import lombok.Data;

@Data
public class ChatMessageRequest {

    private String communityId;

    private String content;
}