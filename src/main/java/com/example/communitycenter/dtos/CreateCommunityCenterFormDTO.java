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
public class CreateCommunityCenterFormDTO {

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

    public CommunityCenter transformToObject(){
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

@Getter
@Setter
class AddressFormDTO {

    @NotNull(message = "Street is required")
    @NotBlank(message = "Street cannot be empty")
    private String street;

    @NotNull(message = "City is required")
    @NotBlank(message = "City cannot be empty")
    private String city;

    @NotNull(message = "State is required")
    @NotBlank(message = "State cannot be empty")
    private String state;

    @NotNull(message = "ZipCode is required")
    @NotBlank(message = "ZipCode cannot be empty")
    private String zipCode;
}

@Getter
@Setter
class ResourcesFormDTO {

    @Min(value = 0, message = "Doctors must be at least 0")
    private Integer doctors;

    @Min(value = 0, message = "Volunteers must be at least 0")
    private Integer volunteers;

    @Min(value = 0, message = "Medical Supplies Kits must be at least 0")
    private Integer medicalSuppliesKits;

    @Min(value = 0, message = "Transport Vehicles must be at least 0")
    private Integer transportVehicles;

    @Min(value = 0, message = "Basic Food Baskets must be at least 0")
    private Integer basicFoodBaskets;
}
