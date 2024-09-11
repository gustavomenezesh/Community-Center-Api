package com.example.communitycenter.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AverageResourcesResponse {
    private float doctors;
    private float volunteers;
    private float medicalSuppliesKits;
    private float transportVehicles;
    private float basicFoodBaskets;
}
