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

    private String id;
    private String name;
    private String description;
    private CommunityType type;

    private String ownerId;

    // ✅ scalable (instead of List<String>)
    private int memberCount;

    private String imageUrl;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Address address;
}