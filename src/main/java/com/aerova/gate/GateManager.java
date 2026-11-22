package com.aerova.gate;

import java.sql.SQLException;
import java.util.ArrayList;

import com.aerova.model.Flight;

public class GateManager {

    private GateRepository gateRepository;
    private GateAllocator gateAllocator;

    public GateManager() {
        gateRepository = new GateRepository();
        gateAllocator = new GateAllocator();
    }

    // Get all gates
    public ArrayList<Gate> getAllGates() throws SQLException {
        return gateRepository.findAllGates();
    }

    // Find a gate by ID
    public Gate getGateById(int gateId) throws SQLException {
        return gateRepository.findGateById(gateId);
    }

    // Find a suitable gate for a flight
    public Gate findGateForFlight(Flight flight, int durationMinutes)
            throws SQLException {

        return gateAllocator.findSuitableGate(flight, durationMinutes);
    }
}