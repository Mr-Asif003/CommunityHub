package com.communityhub.poll.websocket;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PollWebSocketPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public void sendVoteUpdate(String pollId, Object data) {
        messagingTemplate.convertAndSend(
                "/topic/poll/" + pollId,
                data
        );
    }
}