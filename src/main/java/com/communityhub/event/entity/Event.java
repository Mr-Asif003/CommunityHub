package com.communityhub.event.entity;


import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="events")
public class Event {

    @Id
    private String id;

    private String communityId;

    private String title;

    private String description;

    private LocalDateTime eventDate;
    private boolean isPublicEvent;
    private List<String> invitationList;

    private String location;
}
