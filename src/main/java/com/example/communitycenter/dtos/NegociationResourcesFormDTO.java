package com.example.communitycenter.dtos;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@Setter
public class NegociationResourcesFormDTO {

    @NotNull(message = "Resource name is required")
    @NotBlank(message = "Resource name cannot be empty")
    @Pattern(regexp = "doctors|volunteers|medicalSuppliesKits|transportVehicles|basicFoodBaskets", message = "Invalid resource name")
    private String name;

    @Min(value = 1, message = "quantity must be at least 1")
    private Integer quantity;

}
