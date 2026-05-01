package com.communityhub.community.controller;

import com.communityhub.auth.dto.ApiResponse;
import com.communityhub.community.dto.CommunityCreateRequest;
import com.communityhub.community.entity.base.Community;
import com.communityhub.community.entity.base.CommunityMember;
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

        if (auth == null || !auth.isAuthenticated()) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();

        return communityService.getUserCommunities(email);
    }

    @PostMapping("/create")
    public ApiResponse createCommunity(@Valid @RequestBody CommunityCreateRequest req,
                                       Authentication auth) {

        if (auth == null || !auth.isAuthenticated()) {
            return ApiResponse.builder()
                    .success(false)
                    .message("User not authenticated")
                    .build();

        }

        String email = auth.getName();
        return communityService.createCommunity(req, email);
    }


    @PutMapping("/{communityId}")
    public ApiResponse updateCommunity(@PathVariable String communityId,
                                       @RequestBody CommunityCreateRequest req,
                                       Authentication auth) {

        String email = auth.getName();
        return communityService.updateCommunity(communityId, req, email);
    }
    @DeleteMapping("/{communityId}")
    public ApiResponse deleteCommunity(@PathVariable String communityId,
                                       Authentication auth) {

        String email = auth.getName();
        return communityService.deleteCommunity(communityId, email);
    }

    @GetMapping("/{communityId}/members")
    public ApiResponse getMembers(@PathVariable String communityId){
        return communityService.findMembers(communityId);
    }

}