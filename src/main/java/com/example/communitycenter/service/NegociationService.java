package com.example.communitycenter.service;

import com.example.communitycenter.dtos.CreateNegociationFormDTO;
import com.example.communitycenter.dtos.NegociationResourcesFormDTO;
import com.example.communitycenter.exception.utils.httpException.NotFoundException;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.model.Negociation;
import com.example.communitycenter.model.ResourceType;
import com.example.communitycenter.repository.CommunityCenterRepository;
import com.example.communitycenter.repository.NegociationRepository;
import com.example.communitycenter.repository.NegociationRepositoryCustom;
import com.example.communitycenter.repository.ResourcePointsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class NegociationService {
    private final NegociationRepository negociationRepository;
    private final NegociationRepositoryCustom negociationCustomRepository;
    private final CommunityCenterRepository communityCenterRepository;
    private final ResourcePointsRepository resourcePointsRepository;
    private final MongoTemplate mongoTemplate;

    @Transactional
    public Negociation create(CreateNegociationFormDTO form) {
        CommunityCenter originCenter = validateCenterExists(form.getOriginCenterName());
        CommunityCenter destinationCenter = validateCenterExists(form.getDestinationCenterName());

        boolean haveHighOccupancy = verifyHighOccupancy(originCenter, destinationCenter);

        if (!haveHighOccupancy) {
            int sumOriginPoints = calculateTotalPoints(form.getOriginResources());
            int sumDestinationPoints = calculateTotalPoints(form.getDestinationResources());

            if (sumOriginPoints != sumDestinationPoints) {
                throw new RuntimeException("The negociation points aren't equal");
            }
        }

        updateResources(originCenter, destinationCenter, form.getOriginResources(), true);
        updateResources(originCenter, destinationCenter, form.getDestinationResources(), false);

        communityCenterRepository.save(originCenter);
        communityCenterRepository.save(destinationCenter);
        return negociationRepository.save(form.transformToObject());
    }

    public List<Negociation> historic(String communityCenterName, LocalDateTime negociationDate) {
        validateCenterExists(communityCenterName);

        return negociationCustomRepository.findHistoric(communityCenterName, negociationDate);
    }


    private CommunityCenter validateCenterExists(String centerName) {
        return communityCenterRepository.findByName(centerName)
                .orElseThrow(() -> new NotFoundException("Not Found Community Center with name '" + centerName + "'"));
    }

    private boolean verifyHighOccupancy(CommunityCenter originCenter, CommunityCenter destinationCenter) {
        boolean isOriginCenterHighOccupancy = (double) originCenter.getCurrentOccupancy() / (double) originCenter.getCapacity() > 0.9;
        boolean isDestinationCenterHighOccupancy = (double) destinationCenter.getCurrentOccupancy() / (double) destinationCenter.getCapacity() > 0.9;

        return isOriginCenterHighOccupancy || isDestinationCenterHighOccupancy;
    }

    private int calculateTotalPoints(List<NegociationResourcesFormDTO> resources) {
        return resources.stream()
                .mapToInt(resource -> {
                    int value = resourcePointsRepository.findByName(resource.getName())
                            .orElseThrow(() -> new NotFoundException("Resource not found: " + resource.getName()))
                            .getPoint();
                    return resource.getQuantity() * value;
                }).sum();
    }

    private void updateResources(CommunityCenter originCenter, CommunityCenter destinationCenter,
                                 List<NegociationResourcesFormDTO> resources, boolean isOriginToDestination) {
        resources.forEach(resource -> {
            ResourceType resourceType = ResourceType.fromName(resource.getName());
            switch (resourceType) {
                case DOCTORS:
                    updateResource(
                            originCenter.getResources()::getDoctors, originCenter.getResources()::setDoctors,
                            destinationCenter.getResources()::getDoctors, destinationCenter.getResources()::setDoctors,
                            resource.getQuantity(), isOriginToDestination
                    );
                    break;
                case VOLUNTEERS:
                    updateResource(
                            originCenter.getResources()::getVolunteers, originCenter.getResources()::setVolunteers,
                            destinationCenter.getResources()::getVolunteers, destinationCenter.getResources()::setVolunteers,
                            resource.getQuantity(), isOriginToDestination
                    );
                    break;
                case MEDICAL_SUPPLIES_KITS:
                    updateResource(
                            originCenter.getResources()::getMedicalSuppliesKits, originCenter.getResources()::setMedicalSuppliesKits,
                            destinationCenter.getResources()::getMedicalSuppliesKits, destinationCenter.getResources()::setMedicalSuppliesKits,
                            resource.getQuantity(), isOriginToDestination
                    );
                    break;
                case TRANSPORT_VEHICLES:
                    updateResource(
                            originCenter.getResources()::getTransportVehicles, originCenter.getResources()::setTransportVehicles,
                            destinationCenter.getResources()::getTransportVehicles, destinationCenter.getResources()::setTransportVehicles,
                            resource.getQuantity(), isOriginToDestination
                    );
                    break;
                case BASIC_FOOD_BASKETS:
                    updateResource(
                            originCenter.getResources()::getBasicFoodBaskets, originCenter.getResources()::setBasicFoodBaskets,
                            destinationCenter.getResources()::getBasicFoodBaskets, destinationCenter.getResources()::setBasicFoodBaskets,
                            resource.getQuantity(), isOriginToDestination
                    );
                    break;
                default:
                    throw new IllegalArgumentException("Unknown resource type: " + resource.getName());
            }
        });
    }

    private void updateResource(Supplier<Integer> originGetter, Consumer<Integer> originSetter,
                                Supplier<Integer> destinationGetter, Consumer<Integer> destinationSetter,
                                int quantity, boolean isOriginToDestination) {
        int currentOriginValue = originGetter.get();
        int currentDestinationValue = destinationGetter.get();

        if (isOriginToDestination) {
            if (currentOriginValue < quantity) {
                throw new IllegalArgumentException("Insufficient resources in origin center.");
            }
            originSetter.accept(currentOriginValue - quantity);
            destinationSetter.accept(currentDestinationValue + quantity);
        } else {
            if (currentDestinationValue < quantity) {
                throw new IllegalArgumentException("Insufficient resources in destination center.");
            }
            originSetter.accept(currentOriginValue + quantity);
            destinationSetter.accept(currentDestinationValue - quantity);
        }
    }


}
