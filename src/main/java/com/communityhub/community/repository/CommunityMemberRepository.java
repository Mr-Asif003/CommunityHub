package com.communityhub.community.repository;

import com.communityhub.community.entity.base.CommunityMember;
import com.communityhub.community.enums.MembershipStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepository
        extends MongoRepository<CommunityMember, String> {

    // =========================================
    // FIND MEMBER
    // =========================================
    Optional<CommunityMember>
    findByCommunityIdAndUserId(
            String communityId,
            String userId
    );

    // =========================================
    // CHECK MEMBER EXISTS
    // =========================================
    boolean existsByCommunityIdAndUserId(
            String communityId,
            String userId
    );

    // =========================================
    // FIND ACTIVE MEMBERS
    // =========================================
    List<CommunityMember>
    findByCommunityIdAndStatus(
            String communityId,
            MembershipStatus status
    );

    // =========================================
    // FIND ALL COMMUNITY MEMBERS
    // =========================================
    List<CommunityMember>
    findByCommunityId(
            String communityId
    );

    // =========================================
    // FIND USER MEMBERSHIPS
    // =========================================
    List<CommunityMember>
    findByUserId(
            String userId
    );

    // =========================================
    // COUNT MEMBERS
    // =========================================
    long countByCommunityIdAndStatus(
            String communityId,
            MembershipStatus status
    );

    // =========================================
    // DELETE MEMBER
    // =========================================
    void deleteByCommunityIdAndUserId(
            String communityId,
            String userId
    );

    // =========================================
    // DELETE ALL COMMUNITY MEMBERS
    // =========================================
    void deleteByCommunityId(
            String communityId
    );
}