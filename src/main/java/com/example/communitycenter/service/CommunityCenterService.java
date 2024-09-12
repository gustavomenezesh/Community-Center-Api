package com.example.communitycenter.service;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.dtos.UpdateOccupancyFormDTO;
import com.example.communitycenter.exception.utils.httpException.ConflictException;
import com.example.communitycenter.exception.utils.httpException.NotFoundException;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.providers.rabbitMQ.RabbitMQProducer;
import com.example.communitycenter.repository.CommunityCenterRepository;
import com.example.communitycenter.response.AverageResourcesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityCenterService {

    private final CommunityCenterRepository communityCenterRepository;
    private final RabbitMQProducer rabbitMQProducer;
    private final MongoTemplate mongoTemplate;

    private CommunityCenter validateCenterExists(String centerName) {
        return communityCenterRepository.findByName(centerName)
                .orElseThrow(() -> new NotFoundException("Not Found Community Center with name '" + centerName + "'"));
    }
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
        if(form.getOccupancy().equals(communityCenter.getCapacity())){                                                  // If the center is full, notify the queue
            String message = "Community Center '" + name + "' has reached maximum capacity.";
            rabbitMQProducer.sendMessage(message);
        }
    }

    public List<CommunityCenter> listHighOccupancyCenters() {
        return communityCenterRepository.findHighOccupancyCenters();
    }

    public AverageResourcesResponse getAverageResources() {
        return communityCenterRepository.getAverageResources();
    }


}
