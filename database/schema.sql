-- Aerova initial MySQL schema
-- Run this script in MySQL Workbench or using the MySQL CLI.

CREATE DATABASE IF NOT EXISTS aerova_db;
USE aerova_db;


CREATE TABLE IF NOT EXISTS aircraft (
    aircraft_id INT AUTO_INCREMENT PRIMARY KEY,
    registration_no VARCHAR(20) NOT NULL UNIQUE,
    aircraft_model VARCHAR(80) NOT NULL,
    aircraft_size ENUM('SMALL', 'MEDIUM', 'LARGE') NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    capacity INT NOT NULL
);



CREATE TABLE IF NOT EXISTS flights (
    flight_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_number VARCHAR(20) NOT NULL UNIQUE,
    aircraft_id INT NOT NULL,
    flight_type ENUM('ARRIVAL', 'DEPARTURE') NOT NULL,
    scheduled_time DATETIME NOT NULL,
    actual_time DATETIME NULL,

    flight_status ENUM(
        'SCHEDULED',
        'LANDED',
        'TAXIING',
        'AT_GATE',

        'DEPARTED',

        'CANCELLED'
    ) NOT NULL DEFAULT 'SCHEDULED',

    is_emergency BOOLEAN NOT NULL DEFAULT FALSE,
    airline VARCHAR(100),
    delay_minutes INT NOT NULL DEFAULT 0,

    CONSTRAINT fk_flights_aircraft
        FOREIGN KEY (aircraft_id)
        REFERENCES aircraft(aircraft_id)
);


CREATE TABLE IF NOT EXISTS runways (
    runway_id INT AUTO_INCREMENT PRIMARY KEY,
    runway_code VARCHAR(20) NOT NULL UNIQUE,
    runway_status ENUM('AVAILABLE', 'IN_USE', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS runway_assignments (
    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    runway_id INT NOT NULL,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    assignment_status ENUM('SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_runway_assignment_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
    CONSTRAINT fk_runway_assignment_runway FOREIGN KEY (runway_id) REFERENCES runways(runway_id)
);

CREATE TABLE IF NOT EXISTS gates (
    gate_id INT AUTO_INCREMENT PRIMARY KEY,
    gate_code VARCHAR(20) NOT NULL UNIQUE,
    gate_size ENUM('SMALL', 'MEDIUM', 'LARGE') NOT NULL,
    gate_status ENUM('AVAILABLE', 'OCCUPIED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS ground_crew (
    crew_id INT AUTO_INCREMENT PRIMARY KEY,
    crew_name VARCHAR(100) NOT NULL,
    crew_role VARCHAR(60) NOT NULL,
    crew_status ENUM('AVAILABLE', 'ASSIGNED', 'OFF_DUTY') NOT NULL DEFAULT 'AVAILABLE'
);

CREATE TABLE IF NOT EXISTS gate_assignments (
    gate_assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    gate_id INT NOT NULL,
    assigned_from DATETIME NOT NULL,
    assigned_until DATETIME NOT NULL,
    assignment_status ENUM('SCHEDULED', 'ACTIVE', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'SCHEDULED',
    CONSTRAINT fk_gate_assignment_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id),
    CONSTRAINT fk_gate_assignment_gate FOREIGN KEY (gate_id) REFERENCES gates(gate_id)
);

CREATE TABLE IF NOT EXISTS crew_assignments (
    crew_assignment_id INT AUTO_INCREMENT PRIMARY KEY,
    crew_id INT NOT NULL,
    flight_id INT NOT NULL,
    assigned_from DATETIME NOT NULL,
    assigned_until DATETIME NOT NULL,
    assignment_status ENUM('ASSIGNED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'ASSIGNED',
    CONSTRAINT fk_crew_assignment_crew FOREIGN KEY (crew_id) REFERENCES ground_crew(crew_id),
    CONSTRAINT fk_crew_assignment_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);

CREATE TABLE IF NOT EXISTS operation_alerts (
    alert_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NULL,
    alert_type VARCHAR(50) NOT NULL,
    alert_message VARCHAR(500) NOT NULL,
    alert_priority ENUM('LOW', 'MEDIUM', 'HIGH', 'EMERGENCY') NOT NULL DEFAULT 'LOW',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_resolved BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_alert_flight FOREIGN KEY (flight_id) REFERENCES flights(flight_id)
);


-- =========================================
-- TAXIWAY NETWORK AND AIRCRAFT MOVEMENT
-- =========================================

-- 1. Taxiway nodes
CREATE TABLE IF NOT EXISTS taxiway_nodes (
    node_id VARCHAR(20) PRIMARY KEY,
    node_name VARCHAR(100) NOT NULL
);

-- 2. Taxiway segments connecting nodes
CREATE TABLE IF NOT EXISTS taxiway_segments (
    segment_id VARCHAR(20) PRIMARY KEY,
    start_node_id VARCHAR(20) NOT NULL,
    end_node_id VARCHAR(20) NOT NULL,
    distance DECIMAL(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL DEFAULT TRUE,

    CONSTRAINT fk_segment_start_node
        FOREIGN KEY (start_node_id)
        REFERENCES taxiway_nodes(node_id),

    CONSTRAINT fk_segment_end_node
        FOREIGN KEY (end_node_id)
        REFERENCES taxiway_nodes(node_id),

    CHECK (distance > 0),
    CHECK (start_node_id <> end_node_id)
);

-- 3. Aircraft movement records
CREATE TABLE IF NOT EXISTS aircraft_movements (
    movement_id INT AUTO_INCREMENT PRIMARY KEY,
    flight_id INT NOT NULL,
    source_node_id VARCHAR(20) NOT NULL,
    destination_node_id VARCHAR(20) NOT NULL,
    current_node_id VARCHAR(20),
    movement_status ENUM(
        'WAITING',
        'MOVING',
        'STOPPED',
        'COMPLETED'
    ) NOT NULL DEFAULT 'WAITING',
    planned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at DATETIME NULL,
    completed_at DATETIME NULL,

    CONSTRAINT fk_movement_flight
        FOREIGN KEY (flight_id)
        REFERENCES flights(flight_id),

    CONSTRAINT fk_movement_source
        FOREIGN KEY (source_node_id)
        REFERENCES taxiway_nodes(node_id),

    CONSTRAINT fk_movement_destination
        FOREIGN KEY (destination_node_id)
        REFERENCES taxiway_nodes(node_id),

    CONSTRAINT fk_movement_current
        FOREIGN KEY (current_node_id)
        REFERENCES taxiway_nodes(node_id)
);

-- 4. Stores the ordered nodes in each planned route
CREATE TABLE IF NOT EXISTS movement_route_nodes (
    movement_id INT NOT NULL,
    sequence_no INT NOT NULL,
    node_id VARCHAR(20) NOT NULL,

    PRIMARY KEY (movement_id, sequence_no),

    CONSTRAINT fk_route_movement
        FOREIGN KEY (movement_id)
        REFERENCES aircraft_movements(movement_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_route_node
        FOREIGN KEY (node_id)
        REFERENCES taxiway_nodes(node_id),

    CHECK (sequence_no >= 0)
);

-- 5. Movement history and event logs
CREATE TABLE IF NOT EXISTS movement_logs (
    log_id INT AUTO_INCREMENT PRIMARY KEY,
    movement_id INT NOT NULL,
    event_type VARCHAR(30) NOT NULL,
    node_id VARCHAR(20),
    log_message VARCHAR(500),
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_log_movement
        FOREIGN KEY (movement_id)
        REFERENCES aircraft_movements(movement_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_log_node
        FOREIGN KEY (node_id)
        REFERENCES taxiway_nodes(node_id)
);
SHOW TABLES;