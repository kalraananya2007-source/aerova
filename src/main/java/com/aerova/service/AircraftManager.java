
package com.aerova.service;

import com.aerova.dao.FlightRepository;
import com.aerova.model.Aircraft;

import java.sql.SQLException;
import java.util.ArrayList;

public class AircraftManager {

    private final FlightRepository flightRepository;
    private final FlightManager flightManager;

    public AircraftManager() {
        this.flightRepository = new FlightRepository();
        this.flightManager = new FlightManager();
    }

    // Register a new aircraft
    public void addAircraft(Aircraft aircraft) throws SQLException {

        validateAircraft(aircraft);

        if (searchByRegistrationNo(aircraft.getRegistrationNo()) != null) {
            throw new IllegalArgumentException(
                    "Aircraft registration number already exists."
            );
        }

        flightRepository.saveAircraft(aircraft);
    }

    // Search aircraft using ID
    public Aircraft getAircraftById(int aircraftId) throws SQLException {

        if (aircraftId <= 0) {
            throw new IllegalArgumentException(
                    "Aircraft ID must be greater than zero."
            );
        }

        return flightRepository.findAircraftById(aircraftId);
    }

    // Search aircraft using registration number
    public Aircraft searchByRegistrationNo(String registrationNo)
            throws SQLException {

        if (registrationNo == null || registrationNo.isBlank()) {
            throw new IllegalArgumentException(
                    "Registration number cannot be empty."
            );
        }

        for (Aircraft aircraft : getAllAircraft()) {
            if (aircraft.getRegistrationNo() != null
                    && aircraft.getRegistrationNo().equalsIgnoreCase(
                            registrationNo.trim())) {
                return aircraft;
            }
        }

        return null;
    }

    // Get all aircraft
    public ArrayList<Aircraft> getAllAircraft() throws SQLException {
        return flightRepository.findAllAircraft();
    }

    // Update aircraft details
    public void updateAircraft(Aircraft aircraft) throws SQLException {

        validateAircraft(aircraft);

        Aircraft existingAircraft =
                flightRepository.findAircraftById(aircraft.getAircraftId());

        if (existingAircraft == null) {
            throw new SQLException(
                    "Aircraft not found with ID: " + aircraft.getAircraftId()
            );
        }

        Aircraft duplicate =
                searchByRegistrationNo(aircraft.getRegistrationNo());

        if (duplicate != null
                && duplicate.getAircraftId() != aircraft.getAircraftId()) {
            throw new IllegalArgumentException(
                    "Another aircraft already uses this registration number."
            );
        }

        flightRepository.updateAircraft(aircraft);
    }

    // Check whether aircraft is available for assignment
    public boolean isAircraftAvailable(int aircraftId) throws SQLException {

        Aircraft aircraft = getAircraftById(aircraftId);

        if (aircraft == null) {
            return false;
        }

        return !flightManager.isAircraftAssigned(aircraftId);
    }

    // Validate aircraft details
    private void validateAircraft(Aircraft aircraft) {

        if (aircraft == null) {
            throw new IllegalArgumentException(
                    "Aircraft cannot be null."
            );
        }

        if (aircraft.getRegistrationNo() == null
                || aircraft.getRegistrationNo().isBlank()) {
            throw new IllegalArgumentException(
                    "Registration number cannot be empty."
            );
        }

        if (aircraft.getAircraftModel() == null
                || aircraft.getAircraftModel().isBlank()) {
            throw new IllegalArgumentException(
                    "Aircraft model cannot be empty."
            );
        }

        if (aircraft.getAircraftSize() == null) {
            throw new IllegalArgumentException(
                    "Aircraft size cannot be empty."
            );
        }

        if (aircraft.getCapacity() <= 0) {
            throw new IllegalArgumentException(
                    "Aircraft capacity must be greater than zero."
            );
        }
    }
}
