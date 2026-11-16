package com.aerova.model;

import java.time.LocalDateTime;

/** Represents an arrival or departure handled by Aerova. */
public class Flight {
    private int flightId;
    private String flightNumber;
    private Aircraft aircraft;
    private FlightType flightType;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualTime;
    private FlightStatus status;
    private boolean emergency;

    public Flight() {
        this.status = FlightStatus.SCHEDULED;
    }

    public Flight(String flightNumber, Aircraft aircraft, FlightType flightType,
                  LocalDateTime scheduledTime, boolean emergency) {
        this.flightNumber = flightNumber;
        this.aircraft = aircraft;
        this.flightType = flightType;
        this.scheduledTime = scheduledTime;
        this.emergency = emergency;
        this.status = FlightStatus.SCHEDULED;
    }

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) {
        this.flightId = flightId;
    }

    public String getFlightNumber() {
        return flightNumber;
    }

    public void setFlightNumber(String flightNumber) {
        this.flightNumber = flightNumber;
    }

    public Aircraft getAircraft() {
        return aircraft;
    }

    public void setAircraft(Aircraft aircraft) {
        this.aircraft = aircraft;
    }

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getActualTime() {
        return actualTime;
    }

    public void setActualTime(LocalDateTime actualTime) {
        this.actualTime = actualTime;
    }

    public FlightStatus getStatus() {
        return status;
    }

    public void setStatus(FlightStatus status) {
        this.status = status;
    }

    public boolean isEmergency() {
        return emergency;
    }

    public void setEmergency(boolean emergency) {
        this.emergency = emergency;
    }
}
