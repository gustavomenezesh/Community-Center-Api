package com.example.communitycenter.repository;

import com.example.communitycenter.model.CommunityCenter;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CommunityCenterRepository extends MongoRepository<CommunityCenter, String> {
    boolean existsByName(String name);
}
