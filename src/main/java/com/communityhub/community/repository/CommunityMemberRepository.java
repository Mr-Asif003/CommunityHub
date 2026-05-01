package com.communityhub.community.repository;

import com.communityhub.community.entity.base.Community;
import com.communityhub.community.entity.base.CommunityMember;
import com.communityhub.community.enums.CommunityType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityMemberRepository extends MongoRepository<CommunityMember, String> {


    boolean existsByUserIdAndCommunityId(String userId, String communityId);

    List<CommunityMember> findByUserId(String userId);
    CommunityMember deleteByCommunityId(String communityId);

    List<CommunityMember> findByCommunityId(String communityId);

}