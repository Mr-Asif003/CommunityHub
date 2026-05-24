package com.communityhub.community.repository;

import com.communityhub.community.entity.base.Community;
import com.communityhub.community.enums.CommunityType;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository
        extends MongoRepository<Community, String> {

    // =========================================
    // CHECK COMMUNITY NAME EXISTS
    // =========================================
    boolean existsByNameIgnoreCase(
            String name
    );

    // =========================================
    // FIND BY OWNER
    // =========================================
    List<Community> findByOwnerId(
            String ownerId
    );

    // =========================================
    // FIND BY IDS
    // =========================================
    List<Community> findByIdIn(
            List<String> communityIds
    );

    // =========================================
    // FIND BY TYPE
    // =========================================
    List<Community> findByType(
            CommunityType type
    );

    // =========================================
    // SEARCH BY NAME
    // =========================================
    List<Community> findByNameContainingIgnoreCase(
            String name
    );
}