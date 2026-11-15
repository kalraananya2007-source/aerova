# Aerova ✈️

**Intelligent Airport Ground Operations and Conflict Management Simulator**

Aerova is a Java desktop application that simulates airport ground operations. It helps coordinate aircraft schedules, runway usage, parking-gate allocation, and ground-crew availability while detecting resource conflicts and prioritizing emergency flights.

## Problem Statement

Airports manage multiple aircraft arrivals, departures, runway movements, and ground operations simultaneously. Coordinating these activities can be challenging when aircraft compete for the same runway or gate, ground crew availability is limited, or emergencies occur. Aerova aims to simulate these operations, detect scheduling conflicts, prioritize emergencies, and improve the use of airport resources.

## Planned Features

- Aircraft and flight record management
- Runway arrival and departure scheduling
- Priority-based handling of emergency flights
- Parking-gate allocation and conflict detection
- Ground-crew availability and assignment
- Operations dashboard, alerts, and reports
- CSV export for reports

## Technology Stack

- Java 21
- JavaFX for the desktop user interface
- MySQL for persistent storage
- JDBC for database connectivity
- Maven for build and dependency management
- JUnit 5 for tests

## Team Work Division

| Member | Module | Main responsibilities |
|---|---|---|
| Member 1 | Aircraft & Flight Management | Aircraft records, flight details, status tracking and validation |
| Member 2 | Runway Scheduling | Runway availability, arrival/departure queues, emergency priority and runway conflicts |
| Member 3 | Gate, Crew & Conflict Management | Gate allocation, crew assignment, availability checks and conflict alerts |
| Member 4 | GUI, Database Integration & Reports | JavaFX dashboard, integration, report generation and CSV export |

All members should coordinate shared model classes, service method signatures, and database changes before integration.

## Project Structure

```text
aerova/
├── database/                 # MySQL schema and sample data
├── docs/                     # Project documentation
├── src/
│   ├── main/
│   │   ├── java/com/aerova/
│   │   │   ├── dao/           # JDBC data access
│   │   │   ├── exception/     # Custom exceptions
│   │   │   ├── model/         # Aircraft, Flight, Runway, Gate, Crew
│   │   │   ├── service/       # Scheduling and allocation logic
│   │   │   ├── ui/            # JavaFX screens and controllers
│   │   │   └── util/          # Shared utilities
│   │   └── resources/         # Application resources and config template
│   └── test/java/com/aerova/  # Unit tests
├── .gitignore
├── pom.xml
└── README.md
```

## Getting Started

1. Install JDK 21, Maven, and MySQL.
2. Create a MySQL database and run the script in `database/schema.sql`.
3. Copy `.env.example` to `.env` and enter your local database settings. Never commit real passwords.
4. Build the project with `mvn clean package`.
5. Run the JavaFX application with `mvn javafx:run` after the application entry point is implemented.

## Collaboration

- Create a feature branch for each task.
- Keep commits focused and use clear commit messages.
- Open a Pull Request to merge changes into `main`.
- Do not commit database passwords, IDE settings, build output, or personal files.

## Project Status

🚧 Initial project scaffold — implementation is in progress.
