package com.aerova.gate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.aerova.util.DatabaseConnection;

public class GateConflictChecker {

    // Check whether a gate has another flight during the given time
    public boolean hasConflict(int gateId,
                              LocalDateTime assignedFrom,
                              LocalDateTime assignedUntil)
            throws SQLException {

        if (!assignedFrom.isBefore(assignedUntil)) {
            throw new IllegalArgumentException(
                    "Assignment start time must be before end time.");
        }

        String sql = "SELECT COUNT(*) FROM gate_assignments "
                + "WHERE gate_id = ? "
                + "AND assigned_from < ? "
                + "AND assigned_until > ? "
                + "AND assignment_status IN ('SCHEDULED', 'ACTIVE')";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, gateId);
            ps.setTimestamp(2, Timestamp.valueOf(assignedUntil));
            ps.setTimestamp(3, Timestamp.valueOf(assignedFrom));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }
}