package com.communityhub.poll.controller;

import com.communityhub.poll.dto.CreatePollRequest;
import com.communityhub.poll.dto.VoteRequest;
import com.communityhub.poll.entity.Poll;
import com.communityhub.poll.service.PollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities/{communityId}/polls")
@RequiredArgsConstructor
public class PollController {

    private final PollService pollService;

    // ✅ CREATE
    @PostMapping("/create")
    public Poll createPoll(@PathVariable String communityId,
                           @RequestBody CreatePollRequest request,
                           @RequestHeader("userId") String userId) {

        return pollService.createPoll(userId, communityId, request);
    }

    // ✅ GET
    @GetMapping
    public List<Poll> getPolls(@PathVariable String communityId) {
        return pollService.getPollsByCommunity(communityId);
    }

    // ✅ VOTE
    @PostMapping("/{pollId}/vote")
    public String vote(@PathVariable String communityId,
                       @PathVariable String pollId,
                       @RequestHeader("userId") String userId,
                       @RequestBody VoteRequest request) {

        return pollService.vote(communityId, pollId, userId, request);
    }

    // ✅ DELETE
    @DeleteMapping("/{pollId}")
    public String deletePoll(@PathVariable String communityId,
                             @PathVariable String pollId,
                             @RequestHeader("userId") String userId) {

        pollService.deletePoll(communityId, pollId, userId);
        return "Poll deleted successfully";
    }
}