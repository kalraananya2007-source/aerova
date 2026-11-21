
package com.aerova.ui;

import com.aerova.model.*;
import com.aerova.service.*;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class FlightMenu {

    private final Scanner scanner = new Scanner(System.in);

    private final FlightManager flightManager = new FlightManager();
    private final AircraftManager aircraftManager = new AircraftManager();
    private final FlightScheduler flightScheduler = new FlightScheduler();
    private final FlightPriorityManager priorityManager =
            new FlightPriorityManager();
    private final FlightAnalytics analytics = new FlightAnalytics();

    public void showMenu() {

        int choice;

        do {
            System.out.println("\n========== AEROVA FLIGHT MENU ==========");
            System.out.println("1. View All Flights");
            System.out.println("2. Search Flight by ID");
            System.out.println("3. Search Flight by Number");
            System.out.println("4. Search Flights by Airline");
            System.out.println("5. Add New Flight");
            System.out.println("6. Update Flight Status");
            System.out.println("7. Update Flight Details");
            System.out.println("8. Cancel Flight");
            System.out.println("9. Delete Flight");
            System.out.println("10. Filter Flights by Status");
            System.out.println("11. Sort Flights by Scheduled Time");
            System.out.println("12. View Scheduled Arrivals");
            System.out.println("13. View Scheduled Departures");
            System.out.println("14. Reschedule Flight");
            System.out.println("15. Record Flight Delay");
            System.out.println("16. View Delayed Flights");
            System.out.println("17. View Flights by Priority");
            System.out.println("18. View Flight Analytics");
            System.out.println("0. Exit");
            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> viewFlights();
                    case 2 -> searchFlightById();
                    case 3 -> searchFlightByNumber();
                    case 4 -> searchFlightsByAirline();
                    case 5 -> addFlight();
                    case 6 -> updateFlightStatus();
                    case 7 -> updateFlightDetails();
                    case 8 -> cancelFlight();
                    case 9 -> deleteFlight();
                    case 10 -> filterFlightsByStatus();
                    case 11 -> sortFlightsByTime();
                    case 12 -> viewScheduledArrivals();
                    case 13 -> viewScheduledDepartures();
                    case 14 -> rescheduleFlight();
                    case 15 -> recordFlightDelay();
                    case 16 -> viewDelayedFlights();
                    case 17 -> viewFlightsByPriority();
                    case 18 -> viewAnalytics();
                    case 0 -> System.out.println("Exiting Flight Menu...");
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

    // Display all flights
    private void viewFlights() throws SQLException {

        ArrayList<Flight> flights = flightManager.getAllFlights();

        displayFlights(flights);
    }

    // Search using database ID
    private void searchFlightById() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Flight flight = flightManager.getFlightById(id);

        if (flight == null) {
            System.out.println("Flight not found.");
        } else {
            displayFlight(flight);
        }
    }

    // Search using flight number
    private void searchFlightByNumber() throws SQLException {

        System.out.print("Enter Flight Number: ");
        String number = scanner.nextLine();

        Flight flight = flightManager.searchByFlightNumber(number);

        if (flight == null) {
            System.out.println("Flight not found.");
        } else {
            displayFlight(flight);
        }
    }

    // Search using airline name
    private void searchFlightsByAirline() throws SQLException {

        System.out.print("Enter Airline Name: ");
        String airline = scanner.nextLine();

        displayFlights(flightManager.searchByAirline(airline));
    }

    // Register a new flight
    private void addFlight() throws SQLException {

        System.out.print("Enter Flight Number: ");
        String number = scanner.nextLine();

        System.out.print("Enter Airline Name: ");
        String airline = scanner.nextLine();

        System.out.println("\nAvailable Aircraft:");

        ArrayList<Aircraft> aircraftList =
                aircraftManager.getAllAircraft();

        if (aircraftList.isEmpty()) {
            System.out.println("No aircraft registered.");
            return;
        }

        for (Aircraft aircraft : aircraftList) {
            System.out.println(aircraft);
        }

        System.out.print("Enter Aircraft ID: ");
        int aircraftId = Integer.parseInt(scanner.nextLine());

        Aircraft aircraft = aircraftManager.getAircraftById(aircraftId);

        if (aircraft == null) {
            System.out.println("Aircraft not found.");
            return;
        }

        System.out.print("Enter Flight Type (ARRIVAL/DEPARTURE): ");
        FlightType type = FlightType.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        System.out.print("Enter Scheduled Time (yyyy-MM-ddTHH:mm): ");
        LocalDateTime time =
                LocalDateTime.parse(scanner.nextLine());

        System.out.print("Is this an emergency flight? (true/false): ");
        boolean emergency =
                Boolean.parseBoolean(scanner.nextLine());

        Flight flight = new Flight(
                number, aircraft, type, time, emergency
        );

        flight.setAirline(airline);

        flightManager.addFlight(flight);

        System.out.println("Flight registered successfully!");
    }

    // Update flight status
    private void updateFlightStatus() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.println("Available Statuses:");

        for (FlightStatus status : FlightStatus.values()) {
            System.out.println(status);
        }

        System.out.print("Enter new status: ");
        FlightStatus status = FlightStatus.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        flightManager.updateFlightStatus(id, status);

        System.out.println("Flight status updated successfully!");
    }

    // Update flight details
    private void updateFlightDetails() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Flight flight = flightManager.getFlightById(id);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.println("Current Flight Details:");
        displayFlight(flight);

        System.out.print("Enter new Flight Number: ");
        flight.setFlightNumber(scanner.nextLine());

        System.out.print("Enter new Airline Name: ");
        flight.setAirline(scanner.nextLine());

        System.out.print("Enter new Scheduled Time (yyyy-MM-ddTHH:mm): ");
        flight.setScheduledTime(
                LocalDateTime.parse(scanner.nextLine())
        );

        flightManager.updateFlightDetails(flight);

        System.out.println("Flight details updated successfully!");
    }

    // Cancel flight without deleting its record
    private void cancelFlight() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        flightManager.cancelFlight(id);

        System.out.println("Flight cancelled successfully!");
    }

    // Permanently delete flight
    private void deleteFlight() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        flightManager.deleteFlight(id);

        System.out.println("Flight deleted successfully!");
    }

    // Filter flights by status
    private void filterFlightsByStatus() throws SQLException {

        System.out.println("Available Statuses:");

        for (FlightStatus status : FlightStatus.values()) {
            System.out.println(status);
        }

        System.out.print("Enter status: ");
        FlightStatus status = FlightStatus.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        displayFlights(flightManager.filterByStatus(status));
    }

    // Sort flights by scheduled time
    private void sortFlightsByTime() throws SQLException {

        System.out.println("Flights sorted by scheduled time:");

        displayFlights(flightManager.sortByScheduledTime());
    }

    // View scheduled arrivals
    private void viewScheduledArrivals() throws SQLException {

        displayFlights(flightScheduler.getScheduledArrivals());
    }

    // View scheduled departures
    private void viewScheduledDepartures() throws SQLException {

        displayFlights(flightScheduler.getScheduledDepartures());
    }

    // Change scheduled time
    private void rescheduleFlight() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter new Scheduled Time (yyyy-MM-ddTHH:mm): ");
        LocalDateTime newTime =
                LocalDateTime.parse(scanner.nextLine());

        flightScheduler.updateScheduledTime(id, newTime);

        System.out.println("Flight rescheduled successfully!");
    }

    // Record flight delay
    private void recordFlightDelay() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        System.out.print("Enter delay in minutes: ");
        int minutes = Integer.parseInt(scanner.nextLine());

        flightScheduler.updateFlightDelay(id, minutes);

        System.out.println("Flight delay recorded successfully!");
    }

    // Display delayed flights
    private void viewDelayedFlights() throws SQLException {

        displayFlights(flightScheduler.getDelayedFlights());
    }

    // Display flights according to priority
    private void viewFlightsByPriority() throws SQLException {

        ArrayList<Flight> flights = flightManager.getAllFlights();

        PriorityQueue<Flight> queue =
                priorityManager.createPriorityQueue(flights);

        if (queue.isEmpty()) {
            System.out.println("No flights available.");
            return;
        }

        System.out.println("\nFlights arranged by priority:");

        while (!queue.isEmpty()) {
            Flight flight = queue.poll();

            System.out.println(
                    "Priority: " + priorityManager.getPriority(flight)
            );

            displayFlight(flight);
        }
    }

    // Display flight analytics
    private void viewAnalytics() throws SQLException {

        ArrayList<Flight> flights = flightManager.getAllFlights();

        System.out.println("\n========== FLIGHT ANALYTICS ==========");
        System.out.println("Total Flights: " +
                analytics.getTotalFlights(flights));

        System.out.println("Arrivals: " +
                analytics.getArrivalCount(flights));

        System.out.println("Departures: " +
                analytics.getDepartureCount(flights));

        System.out.println("Delayed Flights: " +
                analytics.getDelayedFlightCount(flights));

        System.out.println("Cancelled Flights: " +
                analytics.getCancelledFlightCount(flights));

        System.out.println("Departed Flights: " +
                analytics.getDepartedFlightCount(flights));

        System.out.println("Emergency Flights: " +
                analytics.getEmergencyFlightCount(flights));

        System.out.println("Average Delay: " +
                analytics.getAverageDelay(flights) + " minutes");

        System.out.println("\nAirline-wise Flight Count:");

        analytics.getAirlineWiseCount(flights).forEach(
                (airline, count) ->
                        System.out.println(airline + ": " + count)
        );
    }

    // Display multiple flights
    private void displayFlights(ArrayList<Flight> flights) {

        if (flights == null || flights.isEmpty()) {
            System.out.println("No flights found.");
            return;
        }

        for (Flight flight : flights) {
            displayFlight(flight);
        }
    }

    // Display one flight
    private void displayFlight(Flight flight) {

        System.out.println("\n--------------------------------");
        System.out.println("Flight ID: " + flight.getFlightId());
        System.out.println("Flight Number: " + flight.getFlightNumber());
        System.out.println("Airline: " + flight.getAirline());

        System.out.println("Aircraft: " +
                (flight.getAircraft() == null
                        ? "Not Assigned"
                        : flight.getAircraft().getRegistrationNo()));

        System.out.println("Flight Type: " + flight.getFlightType());
        System.out.println("Scheduled Time: " + flight.getScheduledTime());
        System.out.println("Actual Time: " + flight.getActualTime());
        System.out.println("Status: " + flight.getStatus());
        System.out.println("Emergency: " + flight.isEmergency());
        System.out.println("Delay: " + flight.getDelayMinutes() + " minutes");
        System.out.println("--------------------------------");
    }
}
