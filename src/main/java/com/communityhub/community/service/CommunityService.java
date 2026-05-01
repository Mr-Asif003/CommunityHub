package com.communityhub.community.service;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.community.dto.CommunityCreateRequest;
import com.communityhub.community.dto.CommunityResponse;
import com.communityhub.community.entity.base.Community;
import com.communityhub.community.entity.base.CommunityMember;
import com.communityhub.community.enums.Role;
import com.communityhub.community.repository.CommunityMemberRepository;
import com.communityhub.community.repository.CommunityRepository;
import com.communityhub.user.entity.User;
import com.communityhub.user.repository.UserRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;


    public CommunityService(CommunityRepository communityRepository,
                            CommunityMemberRepository communityMemberRepository,
                            UserRepository userRepository) {
        this.communityRepository = communityRepository;
        this.communityMemberRepository = communityMemberRepository;
        this.userRepository = userRepository;
    }


    @Transactional
    public ApiResponse createCommunity(CommunityCreateRequest cr, String email) {


        if (communityRepository.existsByNameIgnoreCase(cr.getName())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Community name already exists")
                    .build();
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));


        Community community = new Community();
        community.setName(cr.getName());
        community.setDescription(cr.getDescription());
        community.setOwnerId(user.getId());
        community.setAddress(cr.getAddress());
        community.setCreatedAt(LocalDateTime.now());
        community.setUpdatedAt(LocalDateTime.now());
        community.setImageUrl(cr.getImageUrl());
        community.setMemberCount(1);
        community.setType(cr.getType());
        community.setActiveMemberCount(0);

        Community savedCommunity = communityRepository.save(community);


        CommunityMember member = new CommunityMember();
        member.setCommunityId(savedCommunity.getId());
        member.setUserId(user.getId());
        member.setRole(Role.OWNER.name()); // enum used
        member.setJoinedAt(LocalDateTime.now());

        communityMemberRepository.save(member);

        CommunityResponse response = new CommunityResponse();
        response.setName(savedCommunity.getName());
        response.setOwnerId(savedCommunity.getOwnerId());
        response.setAddress(savedCommunity.getAddress());
        response.setId(savedCommunity.getId());
        response.setMemberCount(savedCommunity.getMemberCount());


        return ApiResponse.builder()
                .success(true)
                .message("Community created successfully")
                .data(response)
                .build();
    }

    public List<Community> getUserCommunities(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String userId = user.getId();


        List<CommunityMember> memberships =
                communityMemberRepository.findByUserId(userId);


        List<String> communityIds = memberships.stream()
                .map(CommunityMember::getCommunityId)
                .toList();


        return communityRepository.findByIdIn(communityIds);
    }

    public ApiResponse updateCommunity(String communityId,
                                       CommunityCreateRequest req,
                                       String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));


        if (!community.getOwnerId().equals(user.getId())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not allowed to update this community")
                    .build();
        }


        if (req.getName() != null) community.setName(req.getName());
        if (req.getDescription() != null) community.setDescription(req.getDescription());
        if (req.getType() != null) community.setType(req.getType());
        if (req.getImageUrl() != null) community.setImageUrl(req.getImageUrl());
        if (req.getAddress() != null) community.setAddress(req.getAddress());

        community.setUpdatedAt(LocalDateTime.now());

        communityRepository.save(community);

        return ApiResponse.builder()
                .success(true)
                .message("Community updated .")
                .build();
    }

     public ApiResponse  findMembers(String communityId){
           List<CommunityMember> members=communityMemberRepository.findByCommunityId(communityId);
           if(members.isEmpty()) {
               return ApiResponse.builder()
                       .success(false)
                       .message("members not exists or invalid community Id")
                       .build();
           }
           return ApiResponse.builder()
                   .success(true)
                   .message("fetch members successfully")
                   .data(members)
                   .build();
     }

    public ApiResponse deleteCommunity(String communityId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        // 🔥 Only owner can delete
        if (!community.getOwnerId().equals(user.getId())) {
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not allowed to delete this repo")
                    .build();
        }

        communityRepository.deleteById(communityId);

        // Optional: delete members also
        communityMemberRepository.deleteByCommunityId(communityId);

        return ApiResponse.builder()
                .success(false)
                .message("Community deletion succesfull .")
                .build();
    }
}