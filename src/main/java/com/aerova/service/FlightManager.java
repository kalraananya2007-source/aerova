
package com.aerova.service;

import com.aerova.dao.FlightRepository;
import com.aerova.model.Flight;
import com.aerova.model.FlightStatus;

import java.sql.SQLException;
import java.util.ArrayList;

public class FlightManager {

    private final FlightRepository flightRepository;

    public FlightManager() {
        this.flightRepository = new FlightRepository();
    }

    // Add a new flight
    public void addFlight(Flight flight) throws SQLException {
        flightRepository.saveFlight(flight);
    }

    // Search flight using flight ID
    public Flight getFlightById(int flightId) throws SQLException {
        return flightRepository.findFlightById(flightId);
    }

    // Get all flights
    public ArrayList<Flight> getAllFlights() throws SQLException {
        return flightRepository.findAllFlights();
    }

    // Update flight status
    public void updateFlightStatus(int flightId, FlightStatus status)
            throws SQLException {

        Flight flight = flightRepository.findFlightById(flightId);

        if (flight == null) {
            throw new SQLException("Flight not found with ID: " + flightId);
        }

        flight.setStatus(status);
        flightRepository.updateFlight(flight);
    }

    // Delete a flight
    public void deleteFlight(int flightId) throws SQLException {
        flightRepository.deleteFlight(flightId);
    }
}
