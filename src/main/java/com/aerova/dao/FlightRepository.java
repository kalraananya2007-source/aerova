
package com.aerova.dao;

import com.aerova.model.Aircraft;
import com.aerova.model.AircraftSize;
import com.aerova.model.Flight;
import com.aerova.model.FlightStatus;
import com.aerova.model.FlightType;
import com.aerova.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

public class FlightRepository {

    // Save aircraft in database
    public void saveAircraft(Aircraft aircraft) throws SQLException {

        String sql = "INSERT INTO aircraft "
                + "(registration_no, aircraft_model, aircraft_size, capacity) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, aircraft.getRegistrationNo());
            ps.setString(2, aircraft.getAircraftModel());
            ps.setString(3, aircraft.getAircraftSize().name());
            ps.setInt(4, aircraft.getCapacity());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    aircraft.setAircraftId(rs.getInt(1));
                }
            }
        }
    }

    // Find aircraft using ID
    public Aircraft findAircraftById(int aircraftId) throws SQLException {

        String sql = "SELECT * FROM aircraft WHERE aircraft_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, aircraftId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAircraft(rs);
                }
            }
        }

        return null;
    }

    // Display all aircraft
    public ArrayList<Aircraft> findAllAircraft() throws SQLException {

        ArrayList<Aircraft> aircraftList = new ArrayList<>();

        String sql = "SELECT * FROM aircraft";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                aircraftList.add(mapAircraft(rs));
            }
        }

        return aircraftList;
    }

    // Update aircraft
    public void updateAircraft(Aircraft aircraft) throws SQLException {

        String sql = "UPDATE aircraft SET registration_no = ?, "
                + "aircraft_model = ?, aircraft_size = ?, capacity = ? "
                + "WHERE aircraft_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, aircraft.getRegistrationNo());
            ps.setString(2, aircraft.getAircraftModel());
            ps.setString(3, aircraft.getAircraftSize().name());
            ps.setInt(4, aircraft.getCapacity());
            ps.setInt(5, aircraft.getAircraftId());

            ps.executeUpdate();
        }
    }

    // Save flight in database
    public void saveFlight(Flight flight) throws SQLException {

        String sql = "INSERT INTO flights "
                + "(flight_number, airline, aircraft_id, flight_type, "
                + "scheduled_time, actual_time, delay_minutes, "
                + "flight_status, is_emergency) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirline());
            ps.setInt(3, flight.getAircraft().getAircraftId());
            ps.setString(4, flight.getFlightType().name());
            ps.setTimestamp(5, Timestamp.valueOf(flight.getScheduledTime()));

            if (flight.getActualTime() == null) {
                ps.setNull(6, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(flight.getActualTime()));
            }

            ps.setInt(7, flight.getDelayMinutes());
            ps.setString(8, flight.getStatus().name());
            ps.setBoolean(9, flight.isEmergency());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    flight.setFlightId(rs.getInt(1));
                }
            }
        }
    }

    // Find flight using ID
    public Flight findFlightById(int flightId) throws SQLException {

        String sql = "SELECT * FROM flights WHERE flight_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapFlight(con, rs);
                }
            }
        }

        return null;
    }

    // Display all flights
    public ArrayList<Flight> findAllFlights() throws SQLException {

        ArrayList<Flight> flightList = new ArrayList<>();

        String sql = "SELECT * FROM flights";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                flightList.add(mapFlight(con, rs));
            }
        }

        return flightList;
    }

    // Update flight
    public void updateFlight(Flight flight) throws SQLException {

        String sql = "UPDATE flights SET flight_number = ?, "
                + "airline = ?, aircraft_id = ?, flight_type = ?, "
                + "scheduled_time = ?, actual_time = ?, delay_minutes = ?, "
                + "flight_status = ?, is_emergency = ? "
                + "WHERE flight_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, flight.getFlightNumber());
            ps.setString(2, flight.getAirline());
            ps.setInt(3, flight.getAircraft().getAircraftId());
            ps.setString(4, flight.getFlightType().name());
            ps.setTimestamp(5, Timestamp.valueOf(flight.getScheduledTime()));

            if (flight.getActualTime() == null) {
                ps.setNull(6, java.sql.Types.TIMESTAMP);
            } else {
                ps.setTimestamp(6, Timestamp.valueOf(flight.getActualTime()));
            }

            ps.setInt(7, flight.getDelayMinutes());
            ps.setString(8, flight.getStatus().name());
            ps.setBoolean(9, flight.isEmergency());
            ps.setInt(10, flight.getFlightId());

            ps.executeUpdate();
        }
    }

    // Delete flight
    public void deleteFlight(int flightId) throws SQLException {

        String sql = "DELETE FROM flights WHERE flight_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, flightId);
            ps.executeUpdate();
        }
    }

    // Convert database record into Aircraft object
    private Aircraft mapAircraft(ResultSet rs) throws SQLException {

        return new Aircraft(
                rs.getInt("aircraft_id"),
                rs.getString("registration_no"),
                rs.getString("aircraft_model"),
                AircraftSize.valueOf(rs.getString("aircraft_size")),
                rs.getInt("capacity")
        );
    }

    // Convert database record into Flight object
    private Flight mapFlight(Connection con, ResultSet rs)
            throws SQLException {

        int aircraftId = rs.getInt("aircraft_id");

        Aircraft aircraft = findAircraftById(con, aircraftId);

        Flight flight = new Flight();

        flight.setFlightId(rs.getInt("flight_id"));
        flight.setFlightNumber(rs.getString("flight_number"));
        flight.setAirline(rs.getString("airline"));
        flight.setAircraft(aircraft);

        flight.setFlightType(
                FlightType.valueOf(rs.getString("flight_type")));

        flight.setScheduledTime(
                rs.getTimestamp("scheduled_time").toLocalDateTime());

        Timestamp actualTime = rs.getTimestamp("actual_time");

        if (actualTime != null) {
            flight.setActualTime(actualTime.toLocalDateTime());
        }

        flight.setDelayMinutes(rs.getInt("delay_minutes"));

        flight.setStatus(
                FlightStatus.valueOf(rs.getString("flight_status")));

        flight.setEmergency(rs.getBoolean("is_emergency"));

        return flight;
    }

    // Find aircraft using an existing connection
    private Aircraft findAircraftById(Connection con, int aircraftId)
            throws SQLException {

        String sql = "SELECT * FROM aircraft WHERE aircraft_id = ?";

        try (PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, aircraftId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapAircraft(rs);
                }
            }
        }

        return null;
    }
}
