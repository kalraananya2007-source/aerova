package com.aerova.gate;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.aerova.model.AircraftSize;
import com.aerova.model.Flight;

public class GateAllocator {

    private GateRepository gateRepository;
    private GateConflictChecker conflictChecker;

    public GateAllocator() {
        gateRepository = new GateRepository();
        conflictChecker = new GateConflictChecker();
    }

    // Find a suitable gate for a flight
    public Gate findSuitableGate(Flight flight, int durationMinutes)
            throws SQLException {

        if (flight == null || flight.getAircraft() == null) {
            throw new IllegalArgumentException(
                    "Flight and its aircraft must be provided.");
        }

        if (flight.getScheduledTime() == null) {
            throw new IllegalArgumentException(
                    "Flight scheduled time is missing.");
        }

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero.");
        }

        AircraftSize aircraftSize =
                flight.getAircraft().getAircraftSize();

        LocalDateTime startTime = flight.getScheduledTime();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        ArrayList<Gate> gates = gateRepository.findAllGates();

        for (Gate gate : gates) {

            // Skip gates under maintenance
            if (gate.getStatus() == GateStatus.MAINTENANCE) {
                continue;
            }

            // Check whether gate size can accommodate aircraft
            if (!isSizeCompatible(aircraftSize, gate.getGateSize())) {
                continue;
            }

            // Check time conflict
            boolean conflict = conflictChecker.hasConflict(
                    gate.getGateId(), startTime, endTime);

            if (!conflict) {
                return gate;
            }
        }

        // No suitable gate found
        return null;
    }

    // Check gate and aircraft size compatibility
    private boolean isSizeCompatible(AircraftSize aircraftSize,
                                   String gateSize) {

        if (aircraftSize == null || gateSize == null) {
            return false;
        }

        switch (aircraftSize) {
            case SMALL:
                return gateSize.equals("SMALL")
                        || gateSize.equals("MEDIUM")
                        || gateSize.equals("LARGE");

            case MEDIUM:
                return gateSize.equals("MEDIUM")
                        || gateSize.equals("LARGE");

            case LARGE:
                return gateSize.equals("LARGE");

            default:
                return false;
        }
    }
}