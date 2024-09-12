package com.example.communitycenter.dtos;

import com.example.communitycenter.model.Address;
import com.example.communitycenter.model.CommunityCenter;
import com.example.communitycenter.model.Resources;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CreateCommunityCenterFormDTO {                                     // CreateCmmunityCenter Validator Body

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name cannot be empty")
    private String name;

    @NotNull(message = "Address is required")
    @Valid
    private AddressFormDTO address;

    @Min(value = 0, message = "Capacity must be at least 0")
    private Integer capacity;

    @Min(value = 0, message = "Current occupancy must be at least 0")
    private Integer currentOccupancy;

    @NotNull(message = "Resources are required")
    @Valid
    private ResourcesFormDTO resources;

    public CommunityCenter transformToObject(){                                     // Mapper to transform in model class
        return new CommunityCenter(
                null,
                getName(),
                new Address(getAddress().getStreet(),
                        getAddress().getCity(),
                        getAddress().getState(),
                        getAddress().getZipCode()
                ),
                getCapacity(),
                getCurrentOccupancy(),
                new Resources(
                        getResources().getDoctors(),
                        getResources().getVolunteers(),
                        getResources().getMedicalSuppliesKits(),
                        getResources().getTransportVehicles(),
                        getResources().getBasicFoodBaskets()
                )
        );
    }

}

