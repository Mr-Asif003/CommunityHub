package com.communityhub.poll.service;

import com.communityhub.poll.dto.CreatePollRequest;
import com.communityhub.poll.dto.VoteRequest;
import com.communityhub.poll.entity.Poll;
import com.communityhub.poll.entity.embedded.PollOption;
import com.communityhub.poll.entity.embedded.Vote;
import com.communityhub.poll.repository.PollRepository;
import com.communityhub.poll.repository.VoteRepository;
import com.communityhub.poll.websocket.PollWebSocketPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PollService {

    private final PollRepository pollRepository;
    private final VoteRepository voteRepository;
    private final PollWebSocketPublisher pollWebSocketPublisher;

    // ================= CREATE POLL =================
    public Poll createPoll(String userId, String communityId, CreatePollRequest request) {

        if (request.getOptions() == null || request.getOptions().size() < 2) {
            throw new RuntimeException("At least 2 options required");
        }

        if (request.getExpiresAt() == null ||
                request.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Expiry must be in future");
        }

        List<PollOption> options = request.getOptions()
                .stream()
                .map(PollOption::new)
                .collect(Collectors.toList());

        Poll poll = Poll.builder()
                .communityId(communityId)
                .createdBy(userId)
                .question(request.getQuestion())
                .options(options)
                .expiresAt(request.getExpiresAt())
                .decisionMade(null)
                .anonymous(request.isAnonymous())
                .build();

        return pollRepository.save(poll);
    }

    // ================= GET POLLS =================
    public List<Poll> getPollsByCommunity(String communityId) {
        return pollRepository.findByCommunityId(communityId);
    }

    // ================= VOTE =================
    public String vote(String communityId, String pollId, String userId, VoteRequest request) {

        Poll poll = getPollOrThrow(pollId);

        validateCommunity(poll, communityId);
        validateNotExpired(poll);

        // validate selected option exists
        boolean validOption = poll.getOptions()
                .stream()
                .anyMatch(opt -> opt.getOptionText().equals(request.getSelectedOption()));

        if (!validOption) {
            throw new RuntimeException("Invalid option selected");
        }

        //  prevent duplicate vote (logical check)
        if (voteRepository.findByPollIdAndUserId(pollId, userId).isPresent()) {
            return "Already voted!";
        }

        Vote vote = Vote.builder()
                .pollId(pollId)
                .userId(userId)
                .selectedOption(request.getSelectedOption())
                .votedAt(LocalDateTime.now())
                .build();

        voteRepository.save(vote);
        Map<String,Long> voteCount=voteRepository.findByPollId(pollId)
                .stream()
                .collect(Collectors.groupingBy(
                        Vote::getSelectedOption,
                        Collectors.counting()
                ));
        pollWebSocketPublisher.sendVoteUpdate(pollId,voteCount);
        return "Vote successful";
    }

    // ================= DELETE POLL =================
    public void deletePoll(String communityId, String pollId, String userId) {

        Poll poll = getPollOrThrow(pollId);

        validateCommunity(poll, communityId);

        //  only creator can delete
        if (!poll.getCreatedBy().equals(userId)) {
            throw new RuntimeException("Unauthorized: Only creator can delete poll");
        }

        pollRepository.deleteById(pollId);
    }

    // ================= PROCESS EXPIRED POLLS =================
    public void processExpiredPolls() {

        List<Poll> expiredPolls =
                pollRepository.findByExpiresAtBeforeAndDecisionMadeIsNull(LocalDateTime.now());

        for (Poll poll : expiredPolls) {

            List<Vote> votes = voteRepository.findByPollId(poll.getId());

            if (votes.isEmpty()) {
                poll.setDecisionMade("NO_VOTES");
                pollRepository.save(poll);
                continue;
            }

            // count votes
            Map<String, Long> voteCount =
                    votes.stream()
                            .collect(Collectors.groupingBy(
                                    Vote::getSelectedOption,
                                    Collectors.counting()
                            ));

            long maxVotes = Collections.max(voteCount.values());

            List<String> winners = voteCount.entrySet()
                    .stream()
                    .filter(e -> e.getValue() == maxVotes)
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

            if (winners.size() > 1) {
                poll.setDecisionMade("TIE");
            } else {
                poll.setDecisionMade(winners.get(0));
            }

            pollRepository.save(poll);
        }
    }

    // ================= HELPER METHODS =================

    private Poll getPollOrThrow(String pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new RuntimeException("Poll not found"));
    }

    private void validateCommunity(Poll poll, String communityId) {
        if (!poll.getCommunityId().equals(communityId)) {
            throw new RuntimeException("Invalid community access");
        }
    }

    private void validateNotExpired(Poll poll) {
        if (poll.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Poll expired!");
        }
    }
}