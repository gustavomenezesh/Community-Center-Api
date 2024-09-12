package com.example.communitycenter.repository;

import com.example.communitycenter.model.Negociation;

import java.time.LocalDateTime;
import java.util.List;

public interface NegociationRepositoryCustom {
    List<Negociation> findHistoric(String communityCenterName, LocalDateTime negociationDate);
}
