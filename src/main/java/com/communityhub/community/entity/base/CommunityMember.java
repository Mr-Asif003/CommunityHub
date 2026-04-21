package com.communityhub.community.entity.base;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="community_members")
public class CommunityMember {

    @Id
    private String id;
    @Indexed
    private String userId;
    @Indexed
    private String communityId;

    private String role;

    private String status;

    private LocalDateTime joinedAt;
}