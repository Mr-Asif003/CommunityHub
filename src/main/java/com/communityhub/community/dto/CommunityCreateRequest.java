package com.communityhub.community.dto;

import com.communityhub.community.entity.embeddedentity.Address;
import com.communityhub.community.enums.CommunityType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequest {

    // =========================================
    // COMMUNITY NAME
    // =========================================
    @NotBlank(message = "Community name is required")
    private String name;

    // =========================================
    // DESCRIPTION
    // =========================================
    @NotBlank(message = "Description is required")
    private String description;

    // =========================================
    // COMMUNITY TYPE
    // =========================================
    @NotNull(message = "Community type is required")
    private CommunityType type;

    // =========================================
    // IMAGE
    // =========================================
    private String imageUrl;

    // =========================================
    // ADDRESS
    // =========================================
    private Address address;
    private String password;
}