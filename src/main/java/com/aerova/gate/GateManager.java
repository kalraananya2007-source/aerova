package com.aerova.gate;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import com.aerova.model.Flight;

public class GateManager {

    private GateRepository gateRepository;
    private GateAllocator gateAllocator;

    public GateManager() {
        gateRepository = new GateRepository();
        gateAllocator = new GateAllocator();
    }

    // Get all gates and refresh their statuses
    public List<Gate> getAllGates() throws SQLException {
        gateRepository.refreshGateStatuses();
        return gateRepository.findAllGates();
    }

    // Get gate by ID and refresh status
    public Gate getGateById(int gateId) throws SQLException {
        gateRepository.refreshGateStatuses();
        return gateRepository.findGateById(gateId);
    }

    // Find a suitable gate for a flight
    public Gate findGateForFlight(
            Flight flight, int durationMinutes) throws SQLException {

        return gateAllocator.findSuitableGate(
                flight, durationMinutes);
    }

    // Assign gate to a flight and save assignment
    public Gate assignGateToFlight(
            Flight flight, int durationMinutes) throws SQLException {

        LocalDateTime startTime = flight.getScheduledTime();
        LocalDateTime endTime = startTime.plusMinutes(durationMinutes);

        Gate suitableGate = gateAllocator.findSuitableGate(
                flight, durationMinutes);

        if (suitableGate == null) {
            return null;
        }

        gateRepository.saveGateAssignment(
                flight.getFlightId(),
                suitableGate.getGateId(),
                startTime,
                endTime
        );

        // Refresh gate status after assignment
        gateRepository.refreshGateStatuses();

        return gateRepository.findGateById(
                suitableGate.getGateId());
    }
}