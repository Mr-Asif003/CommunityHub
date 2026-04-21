package com.communityhub.announcement.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Document(collection="announcements")
public class Announcement {

   @Id
   private String id;

   private String communityId;

   private String createdBy;

   private String title;

   private String content;

   private LocalDateTime createdAt;
}