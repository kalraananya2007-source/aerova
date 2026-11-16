package com.aerova.model;

/** Represents an aircraft registered with the airport. */
public class Aircraft {
    private int aircraftId;
    private String registrationNo;
    private String aircraftModel;
    private AircraftSize aircraftSize;

    public Aircraft() {
    }

    public Aircraft(String registrationNo, String aircraftModel, AircraftSize aircraftSize) {
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
    }

    public Aircraft(int aircraftId, String registrationNo, String aircraftModel,
                    AircraftSize aircraftSize) {
        this.aircraftId = aircraftId;
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
    }

    public int getAircraftId() {
        return aircraftId;
    }

    public void setAircraftId(int aircraftId) {
        this.aircraftId = aircraftId;
    }

    public String getRegistrationNo() {
        return registrationNo;
    }

    public void setRegistrationNo(String registrationNo) {
        this.registrationNo = registrationNo;
    }

    public String getAircraftModel() {
        return aircraftModel;
    }

    public void setAircraftModel(String aircraftModel) {
        this.aircraftModel = aircraftModel;
    }

    public AircraftSize getAircraftSize() {
        return aircraftSize;
    }

    public void setAircraftSize(AircraftSize aircraftSize) {
        this.aircraftSize = aircraftSize;
    }

    @Override
    public String toString() {
        return registrationNo + " (" + aircraftModel + ")";
    }
}
