package com.aerova.gate;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class GateMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final GateManager gateManager = new GateManager();
    private final GateRepository gateRepository = new GateRepository();

    public void showMenu() {

        int choice = -1;

        do {
            System.out.println("\n========== AEROVA GATE MENU ==========");
            System.out.println("1. View All Gates");
            System.out.println("2. Search Gate by ID");
            System.out.println("3. Add New Gate");
            System.out.println("4. Update Gate Status");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> viewAllGates();
                    case 2 -> searchGate();
                    case 3 -> addGate();
                    case 4 -> updateGateStatus();
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

        ArrayList<Gate> gates = gateManager.getAllGates();

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
            System.out.println("Gate added successfully! Gate ID: " + gateId);
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

    // Display one gate
    private void displayGate(Gate gate) {

        System.out.println("\n--------------------------------");
        System.out.println("Gate ID: " + gate.getGateId());
        System.out.println("Gate Code: " + gate.getGateCode());
        System.out.println("Gate Size: " + gate.getGateSize());
        System.out.println("Gate Status: " + gate.getStatus());
        System.out.println("--------------------------------");
    }
}