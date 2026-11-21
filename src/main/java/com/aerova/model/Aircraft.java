
package com.aerova.model;

/** Represents an aircraft registered with the airport. */
public class Aircraft {

    private int aircraftId;
    private String registrationNo;
    private String aircraftModel;
    private AircraftSize aircraftSize;
    private int capacity;

    public Aircraft() {
    }

    // Constructor without database ID
    public Aircraft(String registrationNo, String aircraftModel,
                    AircraftSize aircraftSize) {
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
    }

    // Constructor with database ID
    public Aircraft(int aircraftId, String registrationNo,
                    String aircraftModel, AircraftSize aircraftSize) {
        this.aircraftId = aircraftId;
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
    }

    // Constructor with capacity
    public Aircraft(String registrationNo, String aircraftModel,
                    AircraftSize aircraftSize, int capacity) {
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
        setCapacity(capacity);
    }

    // Constructor with ID and capacity
    public Aircraft(int aircraftId, String registrationNo,
                    String aircraftModel, AircraftSize aircraftSize,
                    int capacity) {
        this.aircraftId = aircraftId;
        this.registrationNo = registrationNo;
        this.aircraftModel = aircraftModel;
        this.aircraftSize = aircraftSize;
        this.capacity = capacity;
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Aircraft capacity must be greater than zero."
            );
        }
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

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                "Aircraft capacity must be greater than zero."
            );
        }
        this.capacity = capacity;
    }

    @Override
    public String toString() {
        return registrationNo + " (" + aircraftModel
                + ", Capacity: " + capacity + ")";
    }
}
