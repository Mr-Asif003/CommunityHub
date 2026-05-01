package com.communityhub.complaint.repository;

import com.communityhub.complaint.entity.Complaint;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends MongoRepository<Complaint, String> {

     List<Complaint> findByCommunityId(String communityId);
     Optional<Complaint> findById(String id);

}