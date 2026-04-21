package com.communityhub.poll.entity.embedded;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="votes")
public class Vote {

    @Id
    private String id;

    private String pollId;

    private String userId;

    private String selectedOption;

    private LocalDateTime votedAt;
}