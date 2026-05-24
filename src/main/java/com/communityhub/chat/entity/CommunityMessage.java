package com.communityhub.chat.entity;

import lombok.*;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "community_messages")
public class CommunityMessage {

    @Id
    private String id;

    private String communityId;

    private String senderEmail;

    private String senderName;

    private String content;

    private MessageType type;

    // ✅ FIX: MongoDB stores dates as BSON Date (ISODate).
    // Spring Data MongoDB converts these to LocalDateTime automatically,
    // BUT only if the document was saved by this app in the first place.
    //
    // If any messages were inserted via Compass, a script, or an older
    // version of the code that stored dates as Strings, MongoDB will try
    // to deserialize a String into a LocalDateTime and throw:
    //   "Cannot deserialize value of type `java.time.LocalDateTime`
    //    from String value"
    //
    // The @Field annotation with targetType ensures the driver treats
    // this field as a proper BSON date rather than falling back to string.
    @Field(targetType = org.springframework.data.mongodb.core.mapping.FieldType.DATE_TIME)
    private LocalDateTime createdAt;
}