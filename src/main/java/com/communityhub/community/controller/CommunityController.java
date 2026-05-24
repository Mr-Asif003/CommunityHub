package com.communityhub.community.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.community.dto.CommunityCreateRequest;
import com.communityhub.community.dto.JoinCommunityRequest;
import com.communityhub.community.entity.base.Community;
import com.communityhub.community.service.CommunityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public List<Community> getUserCommunities(Authentication auth) {
        return communityService.getUserCommunities(auth.getName());
    }

    @GetMapping("/discover")
    public ApiResponse discoverCommunities(Authentication auth) {
        return communityService.getDiscoverCommunities(auth.getName());
    }

    @PostMapping
    public ApiResponse createCommunity(
            @Valid @RequestBody CommunityCreateRequest req,
            Authentication auth
    ) {
        return communityService.createCommunity(req, auth.getName());
    }

    @PutMapping("/{communityId}")
    public ApiResponse updateCommunity(
            @PathVariable String communityId,
            @RequestBody CommunityCreateRequest req,
            Authentication auth
    ) {
        return communityService.updateCommunity(communityId, req, auth.getName());
    }

    @DeleteMapping("/{communityId}")
    public ApiResponse deleteCommunity(
            @PathVariable String communityId,
            Authentication auth
    ) {
        return communityService.deleteCommunity(communityId, auth.getName());
    }

    @GetMapping("/{communityId}")
    public ApiResponse getCommunityById(@PathVariable String communityId) {
        return communityService.findById(communityId);
    }

    @GetMapping("/{communityId}/members")
    public ApiResponse getMembers(@PathVariable String communityId) {
        return communityService.findMembers(communityId);
    }

    @PostMapping("/{communityId}/join")
    public ApiResponse joinCommunity(
            @PathVariable String communityId,
            @Valid @RequestBody JoinCommunityRequest req,
            Authentication auth
    ) {
        req.setCommunityId(communityId);
        return communityService.joinCommunity(req, auth.getName());
    }

    @PostMapping("/{communityId}/leave")
    public ApiResponse leaveCommunity(
            @PathVariable String communityId,
            Authentication auth
    ) {
        return communityService.leaveCommunity(communityId, auth.getName());
    }
}