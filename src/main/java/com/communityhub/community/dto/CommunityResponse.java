package com.communityhub.community.dto;

import com.communityhub.community.entity.embeddedentity.Address;
import com.communityhub.community.enums.CommunityType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityResponse {

    // =========================================
    // COMMUNITY ID
    // =========================================
    private String id;

    // =========================================
    // BASIC INFO
    // =========================================
    private String name;

    private String description;

    private CommunityType type;

    private String imageUrl;

    // =========================================
    // OWNER
    // =========================================
    private String ownerId;

    // =========================================
    // MEMBERS
    // =========================================
    private int memberCount;

    private int activeMemberCount;

    // =========================================
    // ADDRESS
    // =========================================
    private Address address;

    // =========================================
    // TIMESTAMPS
    // =========================================
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}