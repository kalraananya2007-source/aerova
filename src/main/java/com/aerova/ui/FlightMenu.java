
package com.aerova.ui;

import com.aerova.model.*;
import com.aerova.service.FlightManager;
import com.aerova.service.AircraftManager;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Scanner;

public class FlightMenu {

    private final Scanner scanner = new Scanner(System.in);
    private final FlightManager flightManager = new FlightManager();
    private final AircraftManager aircraftManager = new AircraftManager();

    public void showMenu() {

        int choice;

        do {
            System.out.println("\n===== AEROVA FLIGHT MENU =====");
            System.out.println("1. View All Flights");
            System.out.println("2. Search Flight");
            System.out.println("3. Add Flight");
            System.out.println("4. Update Flight Status");
            System.out.println("5. Delete Flight");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> viewFlights();
                    case 2 -> searchFlight();
                    case 3 -> addFlight();
                    case 4 -> updateFlightStatus();
                    case 5 -> deleteFlight();
                    case 0 -> System.out.println("Exiting Flight Menu...");
                    default -> System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
                choice = -1;
            }

        } while (choice != 0);
    }

    private void viewFlights() {
        try {
            ArrayList<Flight> flights = flightManager.getAllFlights();

            if (flights.isEmpty()) {
                System.out.println("No flights found.");
                return;
            }

            for (Flight flight : flights) {
                System.out.println("\nFlight ID: " + flight.getFlightId());
                System.out.println("Flight Number: " + flight.getFlightNumber());
                System.out.println("Aircraft: " + flight.getAircraft());
                System.out.println("Type: " + flight.getFlightType());
                System.out.println("Scheduled Time: " + flight.getScheduledTime());
                System.out.println("Status: " + flight.getStatus());
                System.out.println("Emergency: " + flight.isEmergency());
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void searchFlight() {
        try {
            System.out.print("Enter Flight ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            Flight flight = flightManager.getFlightById(id);

            if (flight == null) {
                System.out.println("Flight not found.");
            } else {
                System.out.println("Flight Number: " + flight.getFlightNumber());
                System.out.println("Status: " + flight.getStatus());
                System.out.println("Type: " + flight.getFlightType());
            }

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void addFlight() {
        try {
            System.out.print("Enter Flight Number: ");
            String flightNumber = scanner.nextLine();

            ArrayList<Aircraft> aircraftList =
                    aircraftManager.getAllAircraft();

            if (aircraftList.isEmpty()) {
                System.out.println("No aircraft available. Add an aircraft first.");
                return;
            }

            System.out.println("\nAvailable Aircraft:");
            for (Aircraft aircraft : aircraftList) {
                System.out.println(
                    aircraft.getAircraftId() + ". " + aircraft
                );
            }

            System.out.print("Enter Aircraft ID: ");
            int aircraftId = Integer.parseInt(scanner.nextLine());

            Aircraft aircraft =
                    aircraftManager.getAircraftById(aircraftId);

            if (aircraft == null) {
                System.out.println("Aircraft not found.");
                return;
            }

            System.out.print("Enter Flight Type (ARRIVAL/DEPARTURE): ");
            FlightType type =
                    FlightType.valueOf(scanner.nextLine().toUpperCase());

            System.out.print("Enter Scheduled Time (yyyy-MM-ddTHH:mm): ");
            LocalDateTime time =
                    LocalDateTime.parse(scanner.nextLine());

            System.out.print("Is Emergency? (true/false): ");
            boolean emergency =
                    Boolean.parseBoolean(scanner.nextLine());

            Flight flight = new Flight(
                    flightNumber, aircraft, type, time, emergency
            );

            flightManager.addFlight(flight);

            System.out.println("Flight added successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void updateFlightStatus() {
        try {
            System.out.print("Enter Flight ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            System.out.print("Enter New Status: ");
            FlightStatus status =
                    FlightStatus.valueOf(scanner.nextLine().toUpperCase());

            flightManager.updateFlightStatus(id, status);

            System.out.println("Flight status updated successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void deleteFlight() {
        try {
            System.out.print("Enter Flight ID: ");
            int id = Integer.parseInt(scanner.nextLine());

            flightManager.deleteFlight(id);

            System.out.println("Flight deleted successfully!");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

