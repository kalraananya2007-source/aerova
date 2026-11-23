
package com.aerova.gate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;

import com.aerova.util.DatabaseConnection;

public class GateRepository {

    // Save a new gate
    public int saveGate(Gate gate) throws SQLException {

        String sql = "INSERT INTO gates "
                + "(gate_code, gate_size, gate_status) "
                + "VALUES (?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, gate.getGateCode());
            ps.setString(2, gate.getGateSize());
            ps.setString(3, gate.getStatus().name());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // Find gate using ID
    public Gate findGateById(int gateId) throws SQLException {

        String sql = "SELECT * FROM gates WHERE gate_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, gateId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapGate(rs);
                }
            }
        }

        return null;
    }

    // Get all gates
    public ArrayList<Gate> findAllGates() throws SQLException {

        ArrayList<Gate> gateList = new ArrayList<>();

        String sql = "SELECT * FROM gates";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                gateList.add(mapGate(rs));
            }
        }

        return gateList;
    }

    // Automatically refresh all gate statuses based on booking times
    public void refreshGateStatuses() throws SQLException {

        LocalDateTime now = LocalDateTime.now();

        ArrayList<Gate> gates = findAllGates();

        String sql = "SELECT assigned_from, assigned_until "
                + "FROM gate_assignments "
                + "WHERE gate_id = ? "
                + "AND assignment_status IN ('SCHEDULED', 'ACTIVE')";

        for (Gate gate : gates) {

            // Never overwrite maintenance status
            if (gate.getStatus() == GateStatus.MAINTENANCE) {
                continue;
            }

            boolean currentlyOccupied = false;
            boolean hasFutureBooking = false;

            try (Connection con = DatabaseConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {

                ps.setInt(1, gate.getGateId());

                try (ResultSet rs = ps.executeQuery()) {

                    while (rs.next()) {

                        // Read DATETIME directly as text to avoid timezone shift
                        LocalDateTime start = parseDbDateTime(
                                rs.getString("assigned_from"));

                        LocalDateTime end = parseDbDateTime(
                                rs.getString("assigned_until"));

                        // Booking is currently active
                        if (!now.isBefore(start) && now.isBefore(end)) {
                            currentlyOccupied = true;
                        }

                        // Booking is in the future
                        else if (now.isBefore(start)) {
                            hasFutureBooking = true;
                        }
                    }
                }
            }

            // Determine new status
            GateStatus newStatus;

            if (currentlyOccupied) {
                newStatus = GateStatus.OCCUPIED;
            } else if (hasFutureBooking) {
                newStatus = GateStatus.RESERVED;
            } else {
                newStatus = GateStatus.AVAILABLE;
            }

            // Update database only if status actually changed
            if (gate.getStatus() != newStatus) {
                updateGateStatus(gate.getGateId(), newStatus);
            }
        }
    }

    // Get scheduled assignments for a gate
    public String getGateBookingInfo(int gateId) throws SQLException {

        String sql = "SELECT f.flight_number, ga.assigned_from, "
                + "ga.assigned_until "
                + "FROM gate_assignments ga "
                + "JOIN flights f ON ga.flight_id = f.flight_id "
                + "WHERE ga.gate_id = ? "
                + "AND ga.assignment_status IN ('SCHEDULED', 'ACTIVE') "
                + "ORDER BY ga.assigned_from";

        StringBuilder info = new StringBuilder();

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, gateId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    info.append("Flight: ")
                            .append(rs.getString("flight_number"))
                            .append(" | From: ")
                            .append(rs.getString("assigned_from"))
                            .append(" | Until: ")
                            .append(rs.getString("assigned_until"))
                            .append(System.lineSeparator());
                }
            }
        }

        if (info.length() == 0) {
            return "No upcoming bookings";
        }

        return info.toString();
    }

    // Update gate status
    public void updateGateStatus(int gateId, GateStatus status)
            throws SQLException {

        String sql = "UPDATE gates SET gate_status = ? "
                + "WHERE gate_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, gateId);

            ps.executeUpdate();
        }
    }

    // NEW: Update gate size
    public void updateGateSize(int gateId, String gateSize)
            throws SQLException {

        if (!gateSize.equals("SMALL")
                && !gateSize.equals("MEDIUM")
                && !gateSize.equals("LARGE")) {

            throw new IllegalArgumentException("Invalid gate size.");
        }

        String sql = "UPDATE gates SET gate_size = ? "
                + "WHERE gate_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, gateSize);
            ps.setInt(2, gateId);

            ps.executeUpdate();
        }
    }

    // Save a flight's gate assignment
    public int saveGateAssignment(
            int flightId,
            int gateId,
            LocalDateTime assignedFrom,
            LocalDateTime assignedUntil) throws SQLException {

        if (assignedFrom == null || assignedUntil == null
                || !assignedFrom.isBefore(assignedUntil)) {

            throw new IllegalArgumentException(
                    "Invalid assignment time range.");
        }

        String sql = "INSERT INTO gate_assignments "
                + "(flight_id, gate_id, assigned_from, assigned_until, "
                + "assignment_status) VALUES (?, ?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, flightId);
            ps.setInt(2, gateId);
            ps.setTimestamp(3, Timestamp.valueOf(assignedFrom));
            ps.setTimestamp(4, Timestamp.valueOf(assignedUntil));
            ps.setString(5, "SCHEDULED");

            ps.executeUpdate();

            // Status will be refreshed by GateManager after saving.
            // This avoids overwriting MAINTENANCE status here.

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
    }

    // Convert database DATETIME text into LocalDateTime without timezone shift
    private LocalDateTime parseDbDateTime(String value) {

        return LocalDateTime.parse(value.replace(" ", "T"));
    }

// Generate gate report
public String getGateReport() throws SQLException {

    StringBuilder report = new StringBuilder();

    int totalGates = 0;
    int available = 0;
    int occupied = 0;
    int reserved = 0;
    int maintenance = 0;

    String statusSql = "SELECT gate_status, COUNT(*) AS total "
            + "FROM gates GROUP BY gate_status";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(statusSql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            String status = rs.getString("gate_status");
            int count = rs.getInt("total");

            totalGates += count;

            switch (status) {
                case "AVAILABLE" -> available = count;
                case "OCCUPIED" -> occupied = count;
                case "RESERVED" -> reserved = count;
                case "MAINTENANCE" -> maintenance = count;
            }
        }
    }

    report.append("\n========== GATE REPORT ==========\n");
    report.append("Total Gates: ").append(totalGates).append("\n");
    report.append("Available Gates: ").append(available).append("\n");
    report.append("Occupied Gates: ").append(occupied).append("\n");
    report.append("Reserved Gates: ").append(reserved).append("\n");
    report.append("Maintenance Gates: ").append(maintenance).append("\n");

    report.append("\n------ Gate Usage Summary ------\n");

    String usageSql = "SELECT g.gate_code, COUNT(ga.gate_assignment_id) "
            + "AS total_bookings "
            + "FROM gates g "
            + "LEFT JOIN gate_assignments ga "
            + "ON g.gate_id = ga.gate_id "
            + "AND ga.assignment_status != 'CANCELLED' "
            + "GROUP BY g.gate_id, g.gate_code "
            + "ORDER BY g.gate_code";

    try (Connection con = DatabaseConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(usageSql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {

            report.append("Gate ")
                    .append(rs.getString("gate_code"))
                    .append(" | Total Bookings: ")
                    .append(rs.getInt("total_bookings"))
                    .append("\n");
        }
    }

    report.append("===============================\n");

    return report.toString();
}

    // Convert database row to Gate object
    private Gate mapGate(ResultSet rs) throws SQLException {

        Gate gate = new Gate(
                rs.getInt("gate_id"),
                rs.getString("gate_code"),
                rs.getString("gate_size")
        );

        gate.setStatus(
                GateStatus.valueOf(rs.getString("gate_status"))
        );

        return gate;
    }
}
