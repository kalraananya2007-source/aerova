package com.aerova.gate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.aerova.util.DatabaseConnection;

public class GateRepository {

    // Save a new gate in the database
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

    // Find a gate using its ID
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

    // Display all gates
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
                    "RESERVED status is not supported by the gates table.");
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

    // Convert database record into Gate object
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