package com.example.communitycenter.service;

import com.example.communitycenter.exception.utils.ConflictException;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.repository.CommunityCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityCenterService {

    private final CommunityCenterRepository communityCenterRepository;

    @Transactional
    public CommunityCenter createCommunityCenter(CommunityCenter communityCenter) {
        if (communityCenterRepository.existsByName(communityCenter.getName())) {
            throw new ConflictException("Community center with name '" + communityCenter.getName() + "' already exists.");
        }
        return communityCenterRepository.save(communityCenter);
    }
}
