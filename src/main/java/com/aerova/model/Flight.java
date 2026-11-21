
package com.aerova.model;

import java.time.LocalDateTime;

public class Flight implements Comparable<Flight> {

    private int flightId;
    private String flightNumber;
    private String airline;
    private Aircraft aircraft;
    private FlightType flightType;
    private LocalDateTime scheduledTime;
    private LocalDateTime actualTime;
    private FlightStatus status;
    private boolean emergency;
    private int delayMinutes;

    public Flight() {
        this.status = FlightStatus.SCHEDULED;
        this.delayMinutes = 0;
    }

    public Flight(String flightNumber, Aircraft aircraft,
                  FlightType flightType, LocalDateTime scheduledTime,
                  boolean emergency) {

        this.flightNumber = flightNumber;
        this.aircraft = aircraft;
        this.flightType = flightType;
        this.scheduledTime = scheduledTime;
        this.emergency = emergency;
        this.status = FlightStatus.SCHEDULED;
        this.delayMinutes = 0;
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

    public String getAirline() {
        return airline;
    }

    public void setAirline(String airline) {
        this.airline = airline;
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

    public int getDelayMinutes() {
        return delayMinutes;
    }

    public void setDelayMinutes(int delayMinutes) {
        if (delayMinutes < 0) {
            throw new IllegalArgumentException(
                "Delay cannot be negative."
            );
        }

        this.delayMinutes = delayMinutes;
    }

    public boolean isDelayed() {
        return delayMinutes > 0;
    }

    @Override
    public int compareTo(Flight other) {
        return this.scheduledTime.compareTo(other.scheduledTime);
    }
}
