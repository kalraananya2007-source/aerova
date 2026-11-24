
package com.aerova.gate;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import com.aerova.dao.FlightRepository;
import com.aerova.model.Flight;

public class GateMenu {

    private final Scanner scanner;

    private final GateManager gateManager = new GateManager();
    private final GateRepository gateRepository = new GateRepository();
    private final FlightRepository flightRepository = new FlightRepository();

    // Constructor: shares Scanner with FlightMenu
    public GateMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    // Main Gate Menu
    public void showMenu() {

        int choice = -1;

        do {
            System.out.println("\n========== AEROVA GATE MENU ==========");
            System.out.println("1. View All Gates");
            System.out.println("2. Search Gate by ID");
            System.out.println("3. Add New Gate");
            System.out.println("4. Update Gate Status");
            System.out.println("5. Assign Gate to Flight");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> viewAllGates();
                    case 2 -> searchGate();
                    case 3 -> addGate();
                    case 4 -> updateGateStatus();
                    case 5 -> assignGateToFlight();
                    case 0 -> System.out.println("Exiting Gate Menu...");
                    default -> System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = -1;

            } catch (SQLException | IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
                choice = -1;
            }

        } while (choice != 0);
    }

    // Display all gates
    private void viewAllGates() throws SQLException {

        List<Gate> gates = gateManager.getAllGates();

        if (gates.isEmpty()) {
            System.out.println("No gates found.");
            return;
        }

        for (Gate gate : gates) {
            displayGate(gate);
        }
    }

    // Search gate using its ID
    private void searchGate() throws SQLException {

        System.out.print("Enter Gate ID: ");
        int gateId = Integer.parseInt(scanner.nextLine());

        Gate gate = gateManager.getGateById(gateId);

        if (gate == null) {
            System.out.println("Gate not found.");
        } else {
            displayGate(gate);
        }
    }

    // Add a new gate
    private void addGate() throws SQLException {

        System.out.print("Enter Gate Code: ");
        String gateCode = scanner.nextLine().trim();

        System.out.print("Enter Gate Size (SMALL/MEDIUM/LARGE): ");
        String gateSize = scanner.nextLine().trim().toUpperCase();

        if (!gateSize.equals("SMALL")
                && !gateSize.equals("MEDIUM")
                && !gateSize.equals("LARGE")) {

            System.out.println("Invalid gate size.");
            return;
        }

        Gate gate = new Gate(0, gateCode, gateSize);

        int gateId = gateRepository.saveGate(gate);

        if (gateId > 0) {
            System.out.println(
                    "Gate added successfully! Gate ID: " + gateId);
        } else {
            System.out.println("Gate could not be added.");
        }
    }

    // Update status of an existing gate
    private void updateGateStatus() throws SQLException {

        System.out.print("Enter Gate ID: ");
        int gateId = Integer.parseInt(scanner.nextLine());

        Gate gate = gateManager.getGateById(gateId);

        if (gate == null) {
            System.out.println("Gate not found.");
            return;
        }

        System.out.println("Available statuses:");
        System.out.println("1. AVAILABLE");
        System.out.println("2. OCCUPIED");
        System.out.println("3. MAINTENANCE");

        System.out.print("Choose new status: ");
        int statusChoice = Integer.parseInt(scanner.nextLine());

        GateStatus newStatus;

        switch (statusChoice) {
            case 1 -> newStatus = GateStatus.AVAILABLE;
            case 2 -> newStatus = GateStatus.OCCUPIED;
            case 3 -> newStatus = GateStatus.MAINTENANCE;
            default -> {
                System.out.println("Invalid status choice.");
                return;
            }
        }

        gateRepository.updateGateStatus(gateId, newStatus);

        System.out.println("Gate status updated successfully!");
    }

    // Automatically assign a suitable gate to a flight
    private void assignGateToFlight() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        Flight flight = flightRepository.findFlightById(flightId);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        // Check whether aircraft details are available
        if (flight.getAircraft() == null) {
            System.out.println(
                    "Aircraft details not found for this flight.");
            return;
        }

        System.out.println("Flight Number: " + flight.getFlightNumber());
        System.out.println("Aircraft Size: "
                + flight.getAircraft().getAircraftSize());
        System.out.println("Scheduled Time: " + flight.getScheduledTime());

        System.out.print(
                "Enter gate occupancy duration (minutes): ");

        int durationMinutes = Integer.parseInt(scanner.nextLine());

        if (durationMinutes <= 0) {
            System.out.println(
                    "Duration must be greater than zero.");
            return;
        }

        Gate assignedGate = gateManager.assignGateToFlight(
                flight, durationMinutes);

        if (assignedGate == null) {
            System.out.println(
                    "No suitable gate available for this flight.");

        } else {
            System.out.println("\nGate assigned successfully!");
            System.out.println("Gate ID: " + assignedGate.getGateId());
            System.out.println("Gate Code: " + assignedGate.getGateCode());
            System.out.println("Gate Size: " + assignedGate.getGateSize());

            System.out.println(
                    "Assigned From: " + flight.getScheduledTime());

            System.out.println("Assigned Until: "
                    + flight.getScheduledTime()
                    .plusMinutes(durationMinutes));
        }
    }

    // Display one gate
    private void displayGate(Gate gate) {

        System.out.println("\n--------------------------------");
        System.out.println("Gate ID: " + gate.getGateId());
        System.out.println("Gate Code: " + gate.getGateCode());
        System.out.println("Gate Size: " + gate.getGateSize());
        System.out.println("Gate Status: " + gate.getStatus());

        // Display scheduled booking information
        System.out.println("\n--- Scheduled Bookings ---");

        try {
            System.out.println(
                    gateRepository.getGateBookingInfo(gate.getGateId()));
        } catch (SQLException e) {
            System.out.println("Could not retrieve gate bookings: "
                    + e.getMessage());
        }
        System.out.println("--------------------------------");
    }
}
