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

    // Update gate status
    public void updateGateStatus(int gateId, GateStatus status)
            throws SQLException {

        if (status == GateStatus.RESERVED) {
            throw new SQLException(
                    "RESERVED is not supported by the database.");
        }

        String sql = "UPDATE gates SET gate_status = ? "
                + "WHERE gate_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setInt(2, gateId);

            ps.executeUpdate();
        }
    }

    // Save a flight's gate assignment
    public int saveGateAssignment(int flightId,
                                 int gateId,
                                 LocalDateTime assignedFrom,
                                 LocalDateTime assignedUntil)
            throws SQLException {

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

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return -1;
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