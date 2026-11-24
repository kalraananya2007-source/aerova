
package com.aerova.taxiway;

import com.aerova.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaxiwaySegmentDAO {

    // Save a taxiway segment
    public void saveSegment(TaxiwaySegment segment) throws SQLException {

        String sql = "INSERT INTO taxiway_segments " +
                "(segment_id, start_node_id, end_node_id, distance, is_available) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, segment.getSegmentId());
            statement.setString(2, segment.getStartNode().getNodeId());
            statement.setString(3, segment.getEndNode().getNodeId());
            statement.setDouble(4, segment.getDistance());
            statement.setBoolean(5, segment.isAvailable());

            statement.executeUpdate();

            System.out.println("Taxiway segment saved: " + segment);
        }
    }

    // Retrieve all taxiway segments
    public List<TaxiwaySegment> getAllSegments() throws SQLException {

        List<TaxiwaySegment> segments = new ArrayList<>();

        String sql = "SELECT s.segment_id, s.distance, s.is_available, " +
                "n1.node_id AS start_id, n1.node_name AS start_name, " +
                "n2.node_id AS end_id, n2.node_name AS end_name " +
                "FROM taxiway_segments s " +
                "JOIN taxiway_nodes n1 ON s.start_node_id = n1.node_id " +
                "JOIN taxiway_nodes n2 ON s.end_node_id = n2.node_id";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                TaxiwayNode startNode = new TaxiwayNode(
                        result.getString("start_id"),
                        result.getString("start_name")
                );

                TaxiwayNode endNode = new TaxiwayNode(
                        result.getString("end_id"),
                        result.getString("end_name")
                );

                TaxiwaySegment segment = new TaxiwaySegment(
                        result.getString("segment_id"),
                        startNode,
                        endNode,
                        result.getDouble("distance")
                );

                segment.setAvailable(result.getBoolean("is_available"));

                segments.add(segment);
            }
        }

        return segments;
    }
}
