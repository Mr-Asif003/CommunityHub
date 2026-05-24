package com.communityhub.community.entity.base;

import com.communityhub.community.enums.CommunityRole;
import com.communityhub.community.enums.MembershipStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "community_members")

@CompoundIndex(
        name = "community_user_unique_idx",
        def = "{'communityId':1,'userId':1}",
        unique = true
)
public class CommunityMember {

    @Id
    private String id;

    // =========================================
    // USER INFO
    // =========================================
    @Indexed
    private String userId;

    // =========================================
    // COMMUNITY INFO
    // =========================================
    @Indexed
    private String communityId;

    // =========================================
    // ROLE
    // =========================================
    private CommunityRole role;

    // =========================================
    // MEMBERSHIP STATUS
    // =========================================
    private MembershipStatus status;

    // =========================================
    // JOIN DATE
    // =========================================
    private LocalDateTime joinedAt;
}