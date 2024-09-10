package com.example.communitycenter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class Resources {
    private int doctors;
    private int volunteers;
    private int medicalSuppliesKits;
    private int transportVehicles;
    private int basicFoodBaskets;

    // Getters and Setters
    public int getDoctors() {
        return doctors;
    }

    public void setDoctors(int doctors) {
        this.doctors = doctors;
    }

    public int getVolunteers() {
        return volunteers;
    }

    public void setVolunteers(int volunteers) {
        this.volunteers = volunteers;
    }

    public int getMedicalSuppliesKits() {
        return medicalSuppliesKits;
    }

    public void setMedicalSuppliesKits(int medicalSuppliesKits) {
        this.medicalSuppliesKits = medicalSuppliesKits;
    }

    public int getTransportVehicles() {
        return transportVehicles;
    }

    public void setTransportVehicles(int transportVehicles) {
        this.transportVehicles = transportVehicles;
    }

    public int getBasicFoodBaskets() {
        return basicFoodBaskets;
    }

    public void setBasicFoodBaskets(int basicFoodBaskets) {
        this.basicFoodBaskets = basicFoodBaskets;
    }
}