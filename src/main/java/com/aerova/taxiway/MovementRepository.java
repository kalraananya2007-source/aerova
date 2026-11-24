
package com.aerova.taxiway;

import com.aerova.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

public class MovementRepository {

    // Save movement and its complete route in the database
    public int saveMovement(
            int flightId,
            List<TaxiwayNode> route) throws SQLException {

        if (route == null || route.isEmpty()) {
            throw new IllegalArgumentException("Route cannot be empty.");
        }

        String movementSql =
                "INSERT INTO aircraft_movements " +
                        "(flight_id, source_node_id, destination_node_id, " +
                        "current_node_id, movement_status) " +
                        "VALUES (?, ?, ?, ?, 'WAITING')";

        String routeSql =
                "INSERT INTO movement_route_nodes " +
                        "(movement_id, sequence_no, node_id) VALUES (?, ?, ?)";

        String logSql =
                "INSERT INTO movement_logs " +
                        "(movement_id, event_type, node_id, log_message) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection()) {

            con.setAutoCommit(false);

            try {
                int movementId;

                // Insert movement record
                try (PreparedStatement ps = con.prepareStatement(
                        movementSql, Statement.RETURN_GENERATED_KEYS)) {

                    ps.setInt(1, flightId);
                    ps.setString(2, route.get(0).getNodeId());
                    ps.setString(3,
                            route.get(route.size() - 1).getNodeId());
                    ps.setString(4, route.get(0).getNodeId());

                    ps.executeUpdate();

                    try (ResultSet rs = ps.getGeneratedKeys()) {
                        if (!rs.next()) {
                            throw new SQLException(
                                    "Movement ID was not generated.");
                        }

                        movementId = rs.getInt(1);
                    }
                }

                // Insert all route nodes
                try (PreparedStatement ps =
                             con.prepareStatement(routeSql)) {

                    for (int i = 0; i < route.size(); i++) {
                        ps.setInt(1, movementId);
                        ps.setInt(2, i);
                        ps.setString(3, route.get(i).getNodeId());
                        ps.addBatch();
                    }

                    ps.executeBatch();
                }

                // Save initial log
                try (PreparedStatement ps =
                             con.prepareStatement(logSql)) {

                    ps.setInt(1, movementId);
                    ps.setString(2, "PLANNED");
                    ps.setString(3, route.get(0).getNodeId());
                    ps.setString(4, "Aircraft movement planned.");

                    ps.executeUpdate();
                }

                con.commit();

                return movementId;

            } catch (SQLException | RuntimeException e) {
                con.rollback();
                throw e;
            } finally {
                con.setAutoCommit(true);
            }
        }
    }

    // Update movement status and current location
    public void updateMovement(
            int movementId,
            MovementStatus status,
            String currentNodeId) throws SQLException {

        String sql =
                "UPDATE aircraft_movements SET " +
                        "movement_status = ?, current_node_id = ?, " +
                        "started_at = CASE WHEN ? = 'MOVING' " +
                        "THEN COALESCE(started_at, CURRENT_TIMESTAMP) " +
                        "ELSE started_at END, " +
                        "completed_at = CASE WHEN ? = 'COMPLETED' " +
                        "THEN CURRENT_TIMESTAMP ELSE completed_at END " +
                        "WHERE movement_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());

            if (currentNodeId == null) {
                ps.setNull(2, Types.VARCHAR);
            } else {
                ps.setString(2, currentNodeId);
            }

            ps.setString(3, status.name());
            ps.setString(4, status.name());
            ps.setInt(5, movementId);

            ps.executeUpdate();
        }
    }

    // Save an event in movement history
    public void saveLog(
            int movementId,
            String eventType,
            String nodeId,
            String message) throws SQLException {

        String sql =
                "INSERT INTO movement_logs " +
                        "(movement_id, event_type, node_id, log_message) " +
                        "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, movementId);
            ps.setString(2, eventType);

            if (nodeId == null) {
                ps.setNull(3, Types.VARCHAR);
            } else {
                ps.setString(3, nodeId);
            }

            ps.setString(4, message);

            ps.executeUpdate();
        }
    }
}
