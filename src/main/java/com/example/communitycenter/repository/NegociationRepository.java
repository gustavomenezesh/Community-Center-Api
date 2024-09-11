package com.example.communitycenter.repository;

import com.example.communitycenter.model.Negociation;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NegociationRepository extends MongoRepository<Negociation, String> {
}
