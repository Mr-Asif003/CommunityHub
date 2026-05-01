package com.communityhub.poll.repository;


import com.communityhub.poll.entity.embedded.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface VoteRepository extends MongoRepository<Vote, String> {

    List<Vote> findByPollId(String pollId);

    Optional<Vote> findByPollIdAndUserId(String pollId, String userId);
}
