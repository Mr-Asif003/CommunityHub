package com.communityhub.community.entity.base;

import com.communityhub.community.entity.embeddedentity.Address;
import com.communityhub.community.entity.embeddedentity.CommunitySettings;
import com.communityhub.community.enums.CommunityType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "communities")
public class Community {

    @Id
    private String id;

    // =========================================
    // BASIC INFO
    // =========================================
    @Indexed(unique = true)
    private String name;

    private String description;

    private CommunityType type;

    private String imageUrl;

    // =========================================
    // OWNER
    // =========================================
    @Indexed
    private String ownerId;

    // =========================================
    // MEMBERS
    // =========================================
    private int memberCount=0;

    private int activeMemberCount;

    // =========================================
    // SETTINGS
    // =========================================
    private CommunitySettings settings;

    // =========================================
    // ADDRESS
    // =========================================
    private Address address;

    // =========================================
    // TIMESTAMPS
    // =========================================
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
    private String joinPassword; // 🔐 BCrypt hashed password
}