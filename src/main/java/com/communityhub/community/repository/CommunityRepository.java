package com.communityhub.community.repository;

import com.communityhub.community.entity.base.Community;
import com.communityhub.community.enums.CommunityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends MongoRepository<Community, String> {

    // 🔍 Check if community exists (case insensitive)
    boolean existsByNameIgnoreCase(String name);

    // 🔍 Find communities created by a user
    List<Community> findByOwnerId(String ownerId);
    List<Community> findByIdIn(List<String> communityIds);

    // 🔍 Filter by type
    List<Community> findByType(CommunityType type);

    // 🔍 Search communities by name
    List<Community> findByNameContainingIgnoreCase(String name);


}