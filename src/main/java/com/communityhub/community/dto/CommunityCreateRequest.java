package com.communityhub.community.dto;

import com.communityhub.community.entity.embeddedentity.Address;
import com.communityhub.community.entity.embeddedentity.CommunitySettings;
import com.communityhub.community.enums.CommunityType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;



@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommunityCreateRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Community type is required")
    private CommunityType type;

    private String imageUrl;

    private Address address;
}