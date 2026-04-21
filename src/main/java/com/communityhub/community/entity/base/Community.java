package com.communityhub.community.entity.base;

import com.communityhub.community.entity.embeddedentity.Address;
import com.communityhub.community.entity.embeddedentity.CommunitySettings;
import com.communityhub.community.enums.CommunityType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "communities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public  class Community {
    private String id;

    @Indexed(unique = true)
    private String name;

    private String description;

    private String ownerId;

    private boolean isPublic;

    private String imageUrl;

    private Address address;
    private int memberCount;
    private int activeMemberCount;

    private  CommunityType type;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
