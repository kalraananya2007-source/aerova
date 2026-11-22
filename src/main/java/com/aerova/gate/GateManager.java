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

    public List<Gate> getAllGates() throws SQLException {
        return gateRepository.findAllGates();
    }

    public Gate getGateById(int gateId) throws SQLException {
        return gateRepository.findGateById(gateId);
    }

    public Gate findGateForFlight(Flight flight, int durationMinutes)
            throws SQLException {
        return gateAllocator.findSuitableGate(flight, durationMinutes);
    }

    // Assign gate to a flight and save assignment in database
    public Gate assignGateToFlight(Flight flight, int durationMinutes)
            throws SQLException {

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

        return suitableGate;
    }
}