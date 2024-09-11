package com.example.communitycenter.service;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.dtos.UpdateOccupancyFormDTO;
import com.example.communitycenter.exception.utils.httpException.ConflictException;
import com.example.communitycenter.exception.utils.httpException.NotFoundException;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.providers.rabbitMQ.RabbitMQProducer;
import com.example.communitycenter.repository.CommunityCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommunityCenterService {

    private final CommunityCenterRepository communityCenterRepository;
    private final RabbitMQProducer rabbitMQProducer;

    @Transactional
    public CommunityCenter create(CreateCommunityCenterFormDTO form) {
        if (communityCenterRepository.existsByName(form.getName())) {
            throw new ConflictException("Community center with name '" + form.getName() + "' already exists.");
        }
        return communityCenterRepository.save(form.transformToObject());
    }

    @Transactional
    public void updateOccupancy(String name, UpdateOccupancyFormDTO form) {
        CommunityCenter communityCenter = validateCenterExists(name);

        if(form.getOccupancy() > communityCenter.getCapacity()) {
            throw new RuntimeException("New occupancy is greater than capacity");
        }

        communityCenter.setCurrentOccupancy(form.getOccupancy());
        communityCenterRepository.save(communityCenter);
        if(form.getOccupancy().equals(communityCenter.getCapacity())){
            String message = "Community Center '" + name + "' has reached maximum capacity.";
            rabbitMQProducer.sendMessage(message);
        }
    }

    private CommunityCenter validateCenterExists(String centerName) {
        return communityCenterRepository.findByName(centerName)
                .orElseThrow(() -> new NotFoundException("Not Found Community Center with name '" + centerName + "'"));
    }
}
