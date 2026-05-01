package com.communityhub.poll.repository;


import com.communityhub.poll.entity.Poll;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PollRepository extends MongoRepository<Poll, String> {
    List<Poll> findByCommunityId(String communityId);
    List<Poll> findByExpiresAtBeforeAndDecisionMadeIsNull(LocalDateTime time);
}
