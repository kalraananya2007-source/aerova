
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightType;
import com.aerova.model.FlightStatus;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;

public class FlightScheduler {

    private final FlightManager flightManager;

    public FlightScheduler() {
        this.flightManager = new FlightManager();
    }

    // Get all scheduled arrival flights
    public ArrayList<Flight> getScheduledArrivals() throws SQLException {
        return getScheduledFlights(FlightType.ARRIVAL);
    }

    // Get all scheduled departure flights
    public ArrayList<Flight> getScheduledDepartures() throws SQLException {
        return getScheduledFlights(FlightType.DEPARTURE);
    }

    // Get scheduled flights by type
    public ArrayList<Flight> getScheduledFlights(FlightType type)
            throws SQLException {

        ArrayList<Flight> result = new ArrayList<>();

        for (Flight flight : flightManager.getAllFlights()) {
            if (flight.getFlightType() == type
                    && flight.getStatus() == FlightStatus.SCHEDULED) {
                result.add(flight);
            }
        }

        result.sort(Comparator.comparing(
                Flight::getScheduledTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return result;
    }

    // Get all flights sorted by scheduled time
    public ArrayList<Flight> getFlightsByTime() throws SQLException {

        ArrayList<Flight> flights = flightManager.getAllFlights();

        flights.sort(Comparator.comparing(
                Flight::getScheduledTime,
                Comparator.nullsLast(Comparator.naturalOrder())
        ));

        return flights;
    }

    // Check whether the same aircraft has a flight at the same time
    public boolean hasScheduleConflict(Flight newFlight)
            throws SQLException {

        if (newFlight == null || newFlight.getAircraft() == null
                || newFlight.getScheduledTime() == null) {
            throw new IllegalArgumentException(
                    "Flight, aircraft and scheduled time are required."
            );
        }

        int aircraftId = newFlight.getAircraft().getAircraftId();

        for (Flight existingFlight : flightManager.getAllFlights()) {

            boolean sameFlight =
                    existingFlight.getFlightId() == newFlight.getFlightId();

            boolean sameAircraft =
                    existingFlight.getAircraft() != null
                    && existingFlight.getAircraft().getAircraftId()
                            == aircraftId;

            boolean activeFlight =
                    existingFlight.getStatus() != FlightStatus.CANCELLED;

            boolean sameTime =
                    newFlight.getScheduledTime().equals(
                            existingFlight.getScheduledTime()
                    );

            if (!sameFlight && sameAircraft && activeFlight && sameTime) {
                return true;
            }
        }

        return false;
    }

    // Update arrival or departure scheduled time
    public void updateScheduledTime(int flightId, LocalDateTime newTime)
            throws SQLException {

        if (newTime == null) {
            throw new IllegalArgumentException(
                    "New scheduled time cannot be null."
            );
        }

        Flight flight = flightManager.getFlightById(flightId);

        if (flight == null) {
            throw new SQLException("Flight not found: " + flightId);
        }

        if (flight.getStatus() == FlightStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled flight cannot be rescheduled."
            );
        }

        LocalDateTime oldTime = flight.getScheduledTime();
        flight.setScheduledTime(newTime);

        try {
            if (hasScheduleConflict(flight)) {
                throw new IllegalArgumentException(
                        "Schedule conflict: aircraft already has a flight "
                        + "at this time."
                );
            }

            flightManager.updateFlightDetails(flight);

        } catch (SQLException | RuntimeException exception) {
            flight.setScheduledTime(oldTime);
            throw exception;
        }
    }

    // Record flight delay and calculate actual time
    public void updateFlightDelay(int flightId, int delayMinutes)
            throws SQLException {

        if (delayMinutes < 0) {
            throw new IllegalArgumentException(
                    "Delay cannot be negative."
            );
        }

        Flight flight = flightManager.getFlightById(flightId);

        if (flight == null) {
            throw new SQLException("Flight not found: " + flightId);
        }

        if (flight.getStatus() == FlightStatus.CANCELLED) {
            throw new IllegalArgumentException(
                    "Cancelled flight cannot be delayed."
            );
        }

        flight.setDelayMinutes(delayMinutes);

        if (delayMinutes > 0) {
            flight.setActualTime(
                    flight.getScheduledTime().plusMinutes(delayMinutes)
            );
        } else {
            flight.setActualTime(null);
        }

        flightManager.updateFlightDetails(flight);
    }

    // Get delayed flights
    public ArrayList<Flight> getDelayedFlights() throws SQLException {

        ArrayList<Flight> delayedFlights = new ArrayList<>();

        for (Flight flight : flightManager.getAllFlights()) {
            if (flight.isDelayed()
                    && flight.getStatus() != FlightStatus.CANCELLED) {
                delayedFlights.add(flight);
            }
        }

        delayedFlights.sort(
                Comparator.comparingInt(Flight::getDelayMinutes).reversed()
        );

        return delayedFlights;
    }
}
