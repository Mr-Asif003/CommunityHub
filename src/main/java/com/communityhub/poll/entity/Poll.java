package com.communityhub.poll.entity;



import com.communityhub.poll.entity.embedded.PollOption;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="polls")
public class Poll {

    @Id
    private String id;

    private String communityId;

    private String createdBy;

    private String question;

    private List<PollOption> options;

    private LocalDateTime expiresAt;

    private boolean anonymous;
}
