package com.communityhub.chat.repository;

import com.communityhub.chat.entity.CommunityMessage;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CommunityMessageRepository
        extends MongoRepository<CommunityMessage, String> {

    List<CommunityMessage> findByCommunityIdOrderByCreatedAtAsc(
            String communityId
    );
}