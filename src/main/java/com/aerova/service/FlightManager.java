
package com.aerova.service;

import com.aerova.dao.FlightRepository;
import com.aerova.model.Flight;
import com.aerova.model.FlightStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;

public class FlightManager {

    private final FlightRepository flightRepository;

    public FlightManager() {
        this.flightRepository = new FlightRepository();
    }

    // Add a new flight
    public void addFlight(Flight flight) throws SQLException {
        validateFlight(flight);
        checkAircraftAssignment(flight);
        flightRepository.saveFlight(flight);
    }

    // Search flight using database ID
    public Flight getFlightById(int flightId) throws SQLException {
        return flightRepository.findFlightById(flightId);
    }

    // Get all flights
    public ArrayList<Flight> getAllFlights() throws SQLException {
        return flightRepository.findAllFlights();
    }

    // Search using flight number
    public Flight searchByFlightNumber(String flightNumber)
            throws SQLException {

        if (flightNumber == null || flightNumber.isBlank()) {
            throw new IllegalArgumentException(
                    "Flight number cannot be empty."
            );
        }

        for (Flight flight : getAllFlights()) {
            if (flight.getFlightNumber() != null
                    && flight.getFlightNumber().equalsIgnoreCase(
                            flightNumber.trim())) {
                return flight;
            }
        }

        return null;
    }

    // Search flights using airline name
    public ArrayList<Flight> searchByAirline(String airline)
            throws SQLException {

        if (airline == null || airline.isBlank()) {
            throw new IllegalArgumentException(
                    "Airline name cannot be empty."
            );
        }

        ArrayList<Flight> result = new ArrayList<>();

        for (Flight flight : getAllFlights()) {
            if (flight.getAirline() != null
                    && flight.getAirline().toLowerCase()
                            .contains(airline.trim().toLowerCase())) {
                result.add(flight);
            }
        }

        return result;
    }

    // Filter flights by status
    public ArrayList<Flight> filterByStatus(FlightStatus status)
            throws SQLException {

        Objects.requireNonNull(status, "Flight status cannot be null.");

        ArrayList<Flight> result = new ArrayList<>();

        for (Flight flight : getAllFlights()) {
            if (flight.getStatus() == status) {
                result.add(flight);
            }
        }

        return result;
    }

    // Sort flights by scheduled arrival/departure time
    public ArrayList<Flight> sortByScheduledTime()
            throws SQLException {

        ArrayList<Flight> flights = getAllFlights();

        flights.sort(
                Comparator.comparing(
                        Flight::getScheduledTime,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );

        return flights;
    }

    // Update complete flight details
    public void updateFlightDetails(Flight flight) throws SQLException {

        validateFlight(flight);

        Flight existingFlight =
                flightRepository.findFlightById(flight.getFlightId());

        if (existingFlight == null) {
            throw new SQLException(
                    "Flight not found with ID: " + flight.getFlightId()
            );
        }

        checkAircraftAssignment(flight);
        flightRepository.updateFlight(flight);
    }

    // Update only flight status
    public void updateFlightStatus(int flightId, FlightStatus status)
            throws SQLException {

        Objects.requireNonNull(status, "Flight status cannot be null.");

        Flight flight = flightRepository.findFlightById(flightId);

        if (flight == null) {
            throw new SQLException(
                    "Flight not found with ID: " + flightId
            );
        }

        flight.setStatus(status);
        flightRepository.updateFlight(flight);
    }

    // Cancel flight without deleting its record
    public void cancelFlight(int flightId) throws SQLException {
        updateFlightStatus(flightId, FlightStatus.CANCELLED);
    }

    // Check whether an aircraft is already assigned
    public boolean isAircraftAssigned(int aircraftId) throws SQLException {

        for (Flight flight : getAllFlights()) {
            if (flight.getStatus() != FlightStatus.CANCELLED
                    && flight.getAircraft() != null
                    && flight.getAircraft().getAircraftId() == aircraftId) {
                return true;
            }
        }

        return false;
    }

    // Prevent duplicate aircraft assignments
    private void checkAircraftAssignment(Flight newFlight)
            throws SQLException {

        if (newFlight.getAircraft() == null) {
            throw new IllegalArgumentException(
                    "An aircraft must be assigned to the flight."
            );
        }

        int aircraftId = newFlight.getAircraft().getAircraftId();

        for (Flight existingFlight : getAllFlights()) {

            boolean sameFlight =
                    existingFlight.getFlightId() == newFlight.getFlightId();

            boolean sameAircraft =
                    existingFlight.getAircraft() != null
                    && existingFlight.getAircraft().getAircraftId()
                            == aircraftId;

            boolean notCancelled =
                    existingFlight.getStatus() != FlightStatus.CANCELLED;

            if (!sameFlight && sameAircraft && notCancelled) {
                throw new IllegalArgumentException(
                        "This aircraft is already assigned to another flight."
                );
            }
        }
    }

    // Basic flight validation
    private void validateFlight(Flight flight) {

        Objects.requireNonNull(flight, "Flight cannot be null.");

        if (flight.getFlightNumber() == null
                || flight.getFlightNumber().isBlank()) {
            throw new IllegalArgumentException(
                    "Flight number cannot be empty."
            );
        }

        Objects.requireNonNull(
                flight.getFlightType(),
                "Flight type cannot be null."
        );

        Objects.requireNonNull(
                flight.getScheduledTime(),
                "Scheduled time cannot be null."
        );
    }

    // Permanently delete a flight record
    public void deleteFlight(int flightId) throws SQLException {
        flightRepository.deleteFlight(flightId);
    }
}
