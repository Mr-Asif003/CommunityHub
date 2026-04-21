package com.communityhub.community.entity.embeddedentity;



import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class CommunitySettings {

    private boolean allowVoting;
    private boolean isPrivate;
    private boolean allWriteMessage;
    private boolean allowPosts;
}
