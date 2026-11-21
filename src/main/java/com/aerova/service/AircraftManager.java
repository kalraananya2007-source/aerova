
package com.aerova.service;

import com.aerova.dao.FlightRepository;
import com.aerova.model.Aircraft;

import java.sql.SQLException;
import java.util.ArrayList;

public class AircraftManager {

    private final FlightRepository flightRepository;

    public AircraftManager() {
        this.flightRepository = new FlightRepository();
    }

    // Add a new aircraft
    public void addAircraft(Aircraft aircraft) throws SQLException {
        flightRepository.saveAircraft(aircraft);
    }

    // Search aircraft using ID
    public Aircraft getAircraftById(int aircraftId) throws SQLException {
        return flightRepository.findAircraftById(aircraftId);
    }

    // Get all aircraft
    public ArrayList<Aircraft> getAllAircraft() throws SQLException {
        return flightRepository.findAllAircraft();
    }

    // Update aircraft details
    public void updateAircraft(Aircraft aircraft) throws SQLException {
        flightRepository.updateAircraft(aircraft);
    }
}

