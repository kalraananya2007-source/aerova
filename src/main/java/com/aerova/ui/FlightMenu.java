package com.aerova.ui;

import com.aerova.model.*;
import com.aerova.service.*;
import com.aerova.taxiway.*;
import com.aerova.gate.GateMenu;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

public class FlightMenu {

    private final Scanner scanner = new Scanner(System.in);
private final GateMenu gateMenu = new GateMenu(scanner);
    // Existing flight managers
    private final FlightManager flightManager = new FlightManager();
    private final AircraftManager aircraftManager = new AircraftManager();
    private final FlightScheduler flightScheduler = new FlightScheduler();
    private final FlightPriorityManager priorityManager =
            new FlightPriorityManager();
    private final FlightAnalytics analytics = new FlightAnalytics();

    // Member 3 CSV service
    private final FlightCsvService csvService = new FlightCsvService();

    // Aircraft movement manager
    private AircraftMovementManager movementManager;

    // Ground Services and Crew Management
    private final CrewManager crewManager = new CrewManager();
    private final ServiceManager serviceManager =
            new ServiceManager(crewManager);
    private final TurnaroundManager turnaroundManager =
            new TurnaroundManager(serviceManager);

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

            System.out.println("\n========== CSV & AIRCRAFT MANAGEMENT ==========");
            System.out.println("19. Import Flights from CSV");
            System.out.println("20. Export Flights to CSV");
            System.out.println("21. Register New Aircraft");
            System.out.println("22. View All Aircraft");
            System.out.println("23. Search Aircraft by ID");
            System.out.println("24. Update Aircraft Details");

            System.out.println("\n========== AIRCRAFT MOVEMENT ==========");
            System.out.println("25. Plan Aircraft Movement");
            System.out.println("26. Start Aircraft Movement");
            System.out.println("27. Move Aircraft to Next Node");
            System.out.println("28. Stop Aircraft Movement");
            System.out.println("29. View Movement Status");
            System.out.println("30. Run Aircraft Simulation");

            System.out.println("\n========== GROUND SERVICES & CREW MANAGEMENT ==========");
            System.out.println("31. Register Ground Crew");
            System.out.println("32. View All Ground Crew");
            System.out.println("33. View Available Crew");
            System.out.println("34. Create Ground Service");
            System.out.println("35. View All Ground Services");
            System.out.println("36. Search Service by ID");
            System.out.println("37. Assign Crew to Service");
            System.out.println("38. Start / Complete Service");
            System.out.println("39. Check Aircraft Turnaround Readiness");
            System.out.println("\n========== GATE MANAGEMENT ==========");
System.out.println("40. Open Gate Management");

            System.out.println("\n0. Exit");

            System.out.print("Enter your choice: ");

            try {
                choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {

                    // Flight management
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

                    // CSV and aircraft management
                    case 19 -> importFlightsFromCsv();
                    case 20 -> exportFlightsToCsv();
                    case 21 -> registerAircraft();
                    case 22 -> viewAllAircraft();
                    case 23 -> searchAircraftById();
                    case 24 -> updateAircraftDetails();

                    // Aircraft movement
                    case 25 -> planAircraftMovement();
                    case 26 -> startAircraftMovement();
                    case 27 -> moveAircraftToNextNode();
                    case 28 -> stopAircraftMovement();
                    case 29 -> viewMovementStatus();
                    case 30 -> runAircraftSimulation();

                    // Ground services and crew management
                    case 31 -> registerGroundCrew();
                    case 32 -> viewAllGroundCrew();
                    case 33 -> viewAvailableCrew();
                    case 34 -> createGroundService();
                    case 35 -> viewAllGroundServices();
                    case 36 -> searchServiceById();
                    case 37 -> assignCrewToService();
                    case 38 -> manageServiceStatus();
                    case 39 -> checkTurnaroundReadiness();
                    case 40 -> gateMenu.showMenu();
                    case 0 ->
                            System.out.println("Exiting Aerova Application...");

                    default ->
                            System.out.println("Invalid choice!");
                }

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
                choice = -1;

            } catch (SQLException | IOException e) {

                System.out.println("Database/File Error: " + e.getMessage());
                choice = -1;

            } catch (RuntimeException e) {

                System.out.println("Error: " + e.getMessage());
                choice = -1;
            }

        } while (choice != 0);
    }

    // =========================================
    // FLIGHT MANAGEMENT
    // =========================================

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

        ArrayList<Aircraft> aircraftList = aircraftManager.getAllAircraft();

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
        LocalDateTime time = LocalDateTime.parse(scanner.nextLine());

        System.out.print("Is this an emergency flight? (true/false): ");
        boolean emergency = Boolean.parseBoolean(scanner.nextLine());

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
        flight.setScheduledTime(LocalDateTime.parse(scanner.nextLine()));

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
        LocalDateTime newTime = LocalDateTime.parse(scanner.nextLine());

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

    // =========================================
    // CSV OPERATIONS
    // =========================================

    // Import flights from CSV file
    private void importFlightsFromCsv() throws SQLException, IOException {

        System.out.print("Enter CSV file path: ");
        String filePath = scanner.nextLine();

        int imported = csvService.importFlights(filePath);

        System.out.println(imported + " flights imported successfully!");
    }

    // Export flights to CSV file
    private void exportFlightsToCsv() throws SQLException, IOException {

        System.out.print("Enter destination CSV file path: ");
        String filePath = scanner.nextLine();

        csvService.exportFlights(filePath);

        System.out.println("Flights exported successfully!");
    }

    // =========================================
    // AIRCRAFT MANAGEMENT
    // =========================================

    // Register a new aircraft
    private void registerAircraft() throws SQLException {

        System.out.print("Enter Registration Number: ");
        String registration = scanner.nextLine();

        System.out.print("Enter Aircraft Model: ");
        String model = scanner.nextLine();

        System.out.print("Enter Aircraft Size (SMALL/MEDIUM/LARGE): ");

        AircraftSize size = AircraftSize.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        System.out.print("Enter Aircraft Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        Aircraft aircraft = new Aircraft(
                registration, model, size, capacity
        );

        aircraftManager.addAircraft(aircraft);

        System.out.println("Aircraft registered successfully!");
    }

    // View all aircraft
    private void viewAllAircraft() throws SQLException {

        ArrayList<Aircraft> aircraftList = aircraftManager.getAllAircraft();

        if (aircraftList.isEmpty()) {
            System.out.println("No aircraft found.");
            return;
        }

        for (Aircraft aircraft : aircraftList) {

            System.out.println("Aircraft ID: " + aircraft.getAircraftId());
            System.out.println(aircraft);
            System.out.println("Size: " + aircraft.getAircraftSize());
            System.out.println("----------------------");
        }
    }

    // Search aircraft by ID
    private void searchAircraftById() throws SQLException {

        System.out.print("Enter Aircraft ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Aircraft aircraft = aircraftManager.getAircraftById(id);

        if (aircraft == null) {

            System.out.println("Aircraft not found.");

        } else {

            System.out.println("Aircraft ID: " + aircraft.getAircraftId());
            System.out.println(aircraft);
            System.out.println("Size: " + aircraft.getAircraftSize());
        }
    }

    // Update aircraft details
    private void updateAircraftDetails() throws SQLException {

        System.out.print("Enter Aircraft ID: ");
        int id = Integer.parseInt(scanner.nextLine());

        Aircraft aircraft = aircraftManager.getAircraftById(id);

        if (aircraft == null) {
            System.out.println("Aircraft not found.");
            return;
        }

        System.out.print("Enter new Registration Number: ");
        aircraft.setRegistrationNo(scanner.nextLine());

        System.out.print("Enter new Aircraft Model: ");
        aircraft.setAircraftModel(scanner.nextLine());

        System.out.print("Enter new Aircraft Size (SMALL/MEDIUM/LARGE): ");

        aircraft.setAircraftSize(
                AircraftSize.valueOf(
                        scanner.nextLine().trim().toUpperCase()
                )
        );

        System.out.print("Enter new Capacity: ");
        aircraft.setCapacity(Integer.parseInt(scanner.nextLine()));

        aircraftManager.updateAircraft(aircraft);

        System.out.println("Aircraft updated successfully!");
    }

    // =========================================
    // DISPLAY FLIGHTS
    // =========================================

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

    // =========================================
    // AIRCRAFT MOVEMENT MANAGEMENT
    // =========================================

    // Initialize taxiway network and movement manager
    private void initializeMovementManager() {

        if (movementManager != null) {
            return;
        }

        try {

            TaxiwayNetwork network = new TaxiwayNetwork();

            network.loadNetworkFromDatabase();

            RouteFinder routeFinder = new RouteFinder(network);

            movementManager =
                    new AircraftMovementManager(routeFinder);

            System.out.println("Movement system initialized.");

        } catch (Exception e) {

            System.out.println(
                    "Unable to initialize movement system: "
                            + e.getMessage()
            );
        }
    }

    // Plan aircraft movement
    private void planAircraftMovement() throws SQLException {

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        Flight flight = flightManager.getFlightById(flightId);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        initializeMovementManager();

        if (movementManager == null) {
            return;
        }

        System.out.print("Enter Source Taxiway Node ID: ");
        String sourceId = scanner.nextLine().trim();

        System.out.print("Enter Destination Taxiway Node ID: ");
        String destinationId = scanner.nextLine().trim();

        movementManager.planMovement(
                flight, sourceId, destinationId
        );
    }

    // Start aircraft movement
    private void startAircraftMovement() {

        if (movementManager == null) {
            System.out.println("Plan a movement first.");
            return;
        }

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        movementManager.startMovement(flightId);
    }

    // Move aircraft to the next taxiway node
    private void moveAircraftToNextNode() {

        if (movementManager == null) {
            System.out.println("Plan a movement first.");
            return;
        }

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        TaxiwayNode node =
                movementManager.moveToNextNode(flightId);

        System.out.println("Current Location: " + node);

        System.out.println(
                "Movement Status: "
                        + movementManager.getMovementStatus(flightId)
        );
    }

    // Stop aircraft movement
    private void stopAircraftMovement() {

        if (movementManager == null) {
            System.out.println("Plan a movement first.");
            return;
        }

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        movementManager.stopMovement(flightId);
    }

    // View current movement status
    private void viewMovementStatus() {

        if (movementManager == null) {
            System.out.println("No movement planned in this session.");
            return;
        }

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        System.out.println("\n========== AIRCRAFT MOVEMENT ==========");

        System.out.println(
                "Current Location: "
                        + movementManager.getCurrentNode(flightId)
        );

        System.out.println(
                "Movement Status: "
                        + movementManager.getMovementStatus(flightId)
        );

        System.out.println("Planned Route:");

        for (TaxiwayNode node :
                movementManager.getFlightRoute(flightId)) {

            System.out.println(
                    " -> " + node.getNodeName()
                            + " (" + node.getNodeId() + ")"
            );
        }

        System.out.println("=======================================");
    }

    // =========================================
    // AIRCRAFT SIMULATION - MULTITHREADING
    // =========================================

    // Run aircraft simulation in a separate thread
    private void runAircraftSimulation() {

        if (movementManager == null) {

            System.out.println(
                    "Please plan an aircraft movement first (Option 25)."
            );

            return;
        }

        System.out.print("Enter Flight ID for simulation: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        if (movementManager.getMovementStatus(flightId) == null) {

            System.out.println(
                    "No movement found for this flight in the current session."
            );

            return;
        }

        if (movementManager.getMovementStatus(flightId)
                != MovementStatus.WAITING) {

            System.out.println(
                    "Simulation requires a movement with WAITING status."
            );

            return;
        }

        AircraftSimulation simulation =
                new AircraftSimulation(flightId, movementManager);

        Thread simulationThread =
                new Thread(simulation, "Aircraft-Simulation-" + flightId);

        simulationThread.start();

        System.out.println(
                "Aircraft simulation started in a separate thread."
        );
    }

    // =========================================
    // GROUND CREW MANAGEMENT
    // =========================================

    // Register a new ground crew member
    private void registerGroundCrew() {

        System.out.println("\n========== REGISTER GROUND CREW ==========");

        System.out.print("Enter Crew ID: ");
        String crewId = scanner.nextLine().trim();

        System.out.print("Enter Crew Name: ");
        String name = scanner.nextLine().trim();

        System.out.println("\nAvailable Crew Skills:");

        for (CrewSkill skill : CrewSkill.values()) {
            System.out.println("- " + skill);
        }

        System.out.print("Enter Crew Skill: ");

        CrewSkill skill = CrewSkill.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        GroundCrew crew = new GroundCrew(
                crewId, name, skill
        );

        crewManager.addCrew(crew);

        System.out.println("Ground crew registered successfully!");
    }

    // View all ground crew
    private void viewAllGroundCrew() {

        System.out.println("\n========== ALL GROUND CREW ==========");

        ArrayList<GroundCrew> crewList =
                crewManager.getAllCrew();

        if (crewList.isEmpty()) {
            System.out.println("No ground crew registered.");
            return;
        }

        for (GroundCrew crew : crewList) {
            crew.displayCrew();
        }
    }

    // View available crew
    private void viewAvailableCrew() {

        System.out.println("\n========== AVAILABLE GROUND CREW ==========");

        ArrayList<GroundCrew> crewList =
                crewManager.getAvailableCrew();

        if (crewList.isEmpty()) {
            System.out.println("No available crew members.");
            return;
        }

        for (GroundCrew crew : crewList) {
            crew.displayCrew();
        }
    }

    // =========================================
    // GROUND SERVICE MANAGEMENT
    // =========================================

    // Create a new ground service
    private void createGroundService() throws SQLException {

        System.out.println("\n========== CREATE GROUND SERVICE ==========");

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        Flight flight = flightManager.getFlightById(flightId);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.println("\nAvailable Service Types:");

        for (ServiceType type : ServiceType.values()) {
            System.out.println("- " + type);
        }

        System.out.print("Enter Service Type: ");

        ServiceType type = ServiceType.valueOf(
                scanner.nextLine().trim().toUpperCase()
        );

        GroundService service = serviceManager.createService(
                String.valueOf(flightId), type
        );

        System.out.println("Ground service created successfully!");
        System.out.println("Generated Service ID: " + service.getServiceId());

        service.displayService();
    }

    // View all ground services
    private void viewAllGroundServices() {

        System.out.println("\n========== ALL GROUND SERVICES ==========");

        ArrayList<GroundService> services =
                serviceManager.getAllServices();

        if (services.isEmpty()) {
            System.out.println("No ground services found.");
            return;
        }

        for (GroundService service : services) {
            service.displayService();
        }
    }

    // Search ground service by ID
    private void searchServiceById() {

        System.out.println("\n========== SEARCH GROUND SERVICE ==========");

        System.out.print("Enter Service ID: ");
        String serviceId = scanner.nextLine().trim();

        GroundService service =
                serviceManager.getServiceById(serviceId);

        if (service == null) {
            System.out.println("Ground service not found.");
            return;
        }

        service.displayService();
    }

    // Assign crew to a ground service
    private void assignCrewToService() {

        System.out.println("\n========== ASSIGN CREW TO SERVICE ==========");

        System.out.print("Enter Service ID: ");
        String serviceId = scanner.nextLine().trim();

        GroundService service =
                serviceManager.getServiceById(serviceId);

        if (service == null) {
            System.out.println("Ground service not found.");
            return;
        }

        System.out.println("\nService Details:");
        service.displayService();

        System.out.println("\nAvailable Crew:");

        ArrayList<GroundCrew> availableCrew =
                crewManager.getAvailableCrewBySkill(
                        getRequiredCrewSkill(service.getServiceType())
                );

        if (availableCrew.isEmpty()) {
            System.out.println(
                    "No available crew members with the required skill."
            );
            return;
        }

        for (GroundCrew crew : availableCrew) {
            crew.displayCrew();
        }

        System.out.print("Enter Crew ID to assign: ");
        String crewId = scanner.nextLine().trim();

        serviceManager.assignCrewToService(
                serviceId, crewId
        );

        System.out.println("Crew assigned successfully!");
    }

    // Determine the required crew skill for a service
    private CrewSkill getRequiredCrewSkill(ServiceType type) {

        switch (type) {

            case BAGGAGE:
                return CrewSkill.BAGGAGE;

            case CLEANING:
                return CrewSkill.CLEANING;

            case REFUELING:
                return CrewSkill.REFUELING;

            case MAINTENANCE:
                return CrewSkill.MAINTENANCE;

            default:
                throw new IllegalArgumentException(
                        "Unsupported service type."
                );
        }
    }

    // =========================================
    // SERVICE STATUS MANAGEMENT
    // =========================================

    // Start or complete a ground service
    private void manageServiceStatus() {

        System.out.println("\n========== SERVICE STATUS MANAGEMENT ==========");

        System.out.print("Enter Service ID: ");
        String serviceId = scanner.nextLine().trim();

        GroundService service =
                serviceManager.getServiceById(serviceId);

        if (service == null) {
            System.out.println("Ground service not found.");
            return;
        }

        System.out.println("\nCurrent Service Details:");
        service.displayService();

        System.out.println("\n1. Start Service");
        System.out.println("2. Complete Service");

        System.out.print("Enter your choice: ");

        int choice = Integer.parseInt(scanner.nextLine());

        switch (choice) {

            case 1 -> {
                serviceManager.startService(serviceId);
                System.out.println("Service started successfully!");
            }

            case 2 -> {
                serviceManager.completeService(serviceId);
                System.out.println("Service completed successfully!");
                System.out.println("Assigned crew has been released.");
            }

            default -> System.out.println("Invalid choice!");
        }

        System.out.println("\nUpdated Service Details:");
        service.displayService();
    }

    // =========================================
    // AIRCRAFT TURNAROUND MANAGEMENT
    // =========================================

    // Check whether all ground services are completed
    private void checkTurnaroundReadiness() throws SQLException {

        System.out.println("\n========== AIRCRAFT TURNAROUND READINESS ==========");

        System.out.print("Enter Flight ID: ");
        int flightId = Integer.parseInt(scanner.nextLine());

        Flight flight = flightManager.getFlightById(flightId);

        if (flight == null) {
            System.out.println("Flight not found.");
            return;
        }

        String id = String.valueOf(flightId);

        ArrayList<GroundService> services =
                serviceManager.getServicesByFlight(id);

        if (services.isEmpty()) {
            System.out.println(
                    "No ground services have been created for this flight."
            );
            System.out.println(
                    "Aircraft cannot be marked ready without configured services."
            );
            return;
        }

        System.out.println("\nGround Services for Flight " + flightId + ":");

        for (GroundService service : services) {
            service.displayService();
        }

        boolean ready =
                turnaroundManager.markAircraftReady(id);

        if (!ready) {

            System.out.println("\nIncomplete Services:");

            ArrayList<GroundService> incompleteServices =
                    turnaroundManager.getPendingServicesForFlight(id);

            for (GroundService service : incompleteServices) {
                service.displayService();
            }
        }
    }
}
