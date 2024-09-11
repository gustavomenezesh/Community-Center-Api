package com.example.communitycenter.service;

import com.example.communitycenter.dtos.CreateCommunityCenterFormDTO;
import com.example.communitycenter.dtos.UpdateOccupancyFormDTO;
import com.example.communitycenter.exception.utils.httpException.ConflictException;
import com.example.communitycenter.exception.utils.httpException.NotFoundException;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.providers.rabbitMQ.RabbitMQProducer;
import com.example.communitycenter.repository.CommunityCenterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
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
        if(form.getOccupancy().equals(communityCenter.getCapacity())){
            String message = "Community Center '" + name + "' has reached maximum capacity.";
            rabbitMQProducer.sendMessage(message);
        }
    }

    public List<CommunityCenter> listHighOccupancyCenters() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("capacity").exists(true).and("currentOccupancy").exists(true)),
                Aggregation.project("id", "name", "address", "capacity", "currentOccupancy", "resources")
                        .andExpression("currentOccupancy / capacity").as("occupancyRate"),
                Aggregation.match(Criteria.where("occupancyRate").gt(0.9))
        );

        AggregationResults<CommunityCenter> results = mongoTemplate.aggregate(aggregation, "community_centers", CommunityCenter.class);
        return results.getMappedResults();
    }
}
