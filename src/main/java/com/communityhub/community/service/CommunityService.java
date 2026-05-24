package com.communityhub.community.service;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.community.dto.CommunityCreateRequest;
import com.communityhub.community.dto.CommunityResponse;
import com.communityhub.community.dto.JoinCommunityRequest;
import com.communityhub.community.entity.base.Community;
import com.communityhub.community.entity.base.CommunityMember;
import com.communityhub.community.enums.CommunityRole;
import com.communityhub.community.enums.MembershipStatus;
import com.communityhub.community.repository.CommunityMemberRepository;
import com.communityhub.community.repository.CommunityRepository;
import com.communityhub.user.entity.User;
import com.communityhub.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // =========================================
    // FIND COMMUNITY BY ID
    // =========================================
    public ApiResponse findById(String id) {

        Community community =
                communityRepository.findById(id)
                        .orElse(null);

        if (community == null) {

            return ApiResponse.builder()
                    .success(false)
                    .message("Community not found")
                    .build();
        }

        return ApiResponse.builder()
                .success(true)
                .message("Community fetched successfully")
                .data(community)
                .build();
    }

    // =========================================
    // CREATE COMMUNITY
    // =========================================
    @Transactional
    public ApiResponse createCommunity(
            CommunityCreateRequest req,
            String email
    ) {

        // CHECK DUPLICATE NAME
        if (
                communityRepository.existsByNameIgnoreCase(
                        req.getName()
                )
        ) {

            return ApiResponse.builder()
                    .success(false)
                    .message("Community name already exists")
                    .build();
        }

        // FIND USER
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        // CREATE COMMUNITY
        Community community = new Community();

        community.setName(req.getName());
        community.setDescription(req.getDescription());
        community.setType(req.getType());
        community.setImageUrl(req.getImageUrl());
        community.setAddress(req.getAddress());
        community.setJoinPassword(passwordEncoder.encode(req.getPassword()));
        community.setOwnerId(user.getId());

        community.setMemberCount(1);
        community.setActiveMemberCount(1);

        community.setCreatedAt(LocalDateTime.now());
        community.setUpdatedAt(LocalDateTime.now());

        Community savedCommunity =
                communityRepository.save(community);

        // CREATE OWNER MEMBER ENTRY
        CommunityMember ownerMember =
                CommunityMember.builder()
                        .communityId(savedCommunity.getId())
                        .userId(user.getId())
                        .role(CommunityRole.OWNER)
                        .status(MembershipStatus.ACTIVE)
                        .joinedAt(LocalDateTime.now())
                        .build();

        communityMemberRepository.save(ownerMember);

        // RESPONSE DTO
        CommunityResponse response =
                CommunityResponse.builder()
                        .id(savedCommunity.getId())
                        .name(savedCommunity.getName())
                        .description(savedCommunity.getDescription())
                        .type(savedCommunity.getType())
                        .ownerId(savedCommunity.getOwnerId())
                        .memberCount(savedCommunity.getMemberCount())
                        .imageUrl(savedCommunity.getImageUrl())
                        .createdAt(savedCommunity.getCreatedAt())
                        .updatedAt(savedCommunity.getUpdatedAt())
                        .address(savedCommunity.getAddress())
                        .build();

        return ApiResponse.builder()
                .success(true)
                .message("Community created successfully")
                .data(response)
                .build();
    }

    // =========================================
    // JOIN COMMUNITY
    // =========================================

    @Transactional
    public ApiResponse joinCommunity(JoinCommunityRequest req, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Community community = communityRepository.findById(req.getCommunityId())
                .orElse(null);

        if (community == null) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Community not found")
                    .build();
        }

        boolean match = passwordEncoder.matches(
                req.getPassword(),
                community.getJoinPassword()
        );

        if (!match) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Invalid password")
                    .build();
        }

        boolean alreadyJoined =
                communityMemberRepository.existsByCommunityIdAndUserId(
                        req.getCommunityId(),
                        user.getId()
                );

        if (alreadyJoined) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Already joined this community")
                    .build();
        }

        CommunityMember member = CommunityMember.builder()
                .communityId(req.getCommunityId())
                .userId(user.getId())
                .role(CommunityRole.MEMBER)
                .status(MembershipStatus.ACTIVE)
                .joinedAt(LocalDateTime.now())
                .build();

        communityMemberRepository.save(member);

        community.setMemberCount(community.getMemberCount() + 1);
        community.setActiveMemberCount(community.getActiveMemberCount() + 1);
        community.setUpdatedAt(LocalDateTime.now());

        communityRepository.save(community);

        return ApiResponse.builder()
                .success(true)
                .message("Joined community successfully")
                .build();
    }
    //=======================================

    public ApiResponse getDiscoverCommunities(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // all communities
        List<Community> allCommunities = communityRepository.findAll();

        // communities user already joined
        List<CommunityMember> memberships =
                communityMemberRepository.findByUserId(user.getId());

        Set<String> joinedIds = memberships.stream()
                .map(CommunityMember::getCommunityId)
                .collect(Collectors.toSet());

        // filter discover
        List<Community> discover = allCommunities.stream()
                .filter(c -> !joinedIds.contains(c.getId()))
                .toList();

        return ApiResponse.builder()
                .success(true)
                .message("Discover communities fetched successfully")
                .data(discover)
                .build();
    }

    // =========================================
    // LEAVE COMMUNITY
    // =========================================
    @Transactional
    public ApiResponse leaveCommunity(String communityId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        CommunityMember member =
                communityMemberRepository.findByCommunityIdAndUserId(
                        communityId,
                        user.getId()
                ).orElse(null);

        if (member == null) {
            return ApiResponse.builder()
                    .success(false)
                    .message("You are not a member")
                    .build();
        }

        if (member.getRole() == CommunityRole.OWNER) {
            return ApiResponse.builder()
                    .success(false)
                    .message("Owner cannot leave community")
                    .build();
        }

        communityMemberRepository.deleteById(member.getId());

        Community community = communityRepository.findById(communityId)
                .orElseThrow(() -> new RuntimeException("Community not found"));

        community.setMemberCount(Math.max(0, community.getMemberCount() - 1));
        community.setActiveMemberCount(Math.max(0, community.getActiveMemberCount() - 1));
        community.setUpdatedAt(LocalDateTime.now());

        communityRepository.save(community);

        return ApiResponse.builder()
                .success(true)
                .message("Left community successfully")
                .build();
    }

    // =========================================
    // GET USER COMMUNITIES
    // =========================================
    public List<Community> getUserCommunities(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<String> communityIds = communityMemberRepository.findByUserId(user.getId())
                .stream()
                .map(CommunityMember::getCommunityId)
                .toList();

        if (communityIds.isEmpty()) {
            return List.of();
        }

        return communityRepository.findByIdIn(communityIds);
    }

    // =========================================
    // UPDATE COMMUNITY
    // =========================================
    @Transactional
    public ApiResponse updateCommunity(String communityId, CommunityCreateRequest req, String email) {

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
                .message("Community updated successfully")
                .build();
    }

    // =========================================
    // FIND MEMBERS
    // =========================================
    public ApiResponse findMembers(
            String communityId
    ) {

        List<CommunityMember> members =
                communityMemberRepository
                        .findByCommunityId(communityId);

        return ApiResponse.builder()
                .success(true)
                .message("Members fetched successfully")
                .data(members)
                .build();
    }

    // =========================================
    // DELETE COMMUNITY
    // =========================================
    @Transactional
    public ApiResponse deleteCommunity(
            String communityId,
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"
                        )
                );

        Community community =
                communityRepository.findById(communityId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Community not found"
                                )
                        );

        // ONLY OWNER CAN DELETE
        if (
                !community.getOwnerId().equals(
                        user.getId()
                )
        ) {

            return ApiResponse.builder()
                    .success(false)
                    .message(
                            "You are not allowed to delete this community"
                    )
                    .build();
        }

        // DELETE MEMBERS
        communityMemberRepository
                .deleteByCommunityId(communityId);

        // DELETE COMMUNITY
        communityRepository.deleteById(communityId);

        return ApiResponse.builder()
                .success(true)
                .message("Community deleted successfully")
                .build();
    }
}