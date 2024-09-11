package com.example.communitycenter.repository;

import com.example.communitycenter.model.CommunityCenter;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CommunityCenterRepository extends MongoRepository<CommunityCenter, String> {
    boolean existsByName(String name);
    Optional<CommunityCenter> findByName(String name);
}
