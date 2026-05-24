package com.communityhub.community.entity.embeddedentity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunitySettings {

    // =========================================
    // VOTING
    // =========================================
    private boolean allowVoting;

    // =========================================
    // PUBLIC / PRIVATE
    // =========================================
    private boolean isPublic;

    // =========================================
    // CHAT ACCESS
    // =========================================
    private boolean allWriteMessage;

    // =========================================
    // POSTS
    // =========================================
    private boolean allowPosts;
}