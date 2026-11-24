
package com.aerova.taxiway;

import com.aerova.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaxiwayNodeDAO {

    // Save a taxiway node to the database
    public void saveNode(TaxiwayNode node) throws SQLException {

        String sql = "INSERT INTO taxiway_nodes (node_id, node_name) VALUES (?, ?)";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, node.getNodeId());
            statement.setString(2, node.getNodeName());

            statement.executeUpdate();

            System.out.println("Taxiway node saved: " + node);
        }
    }

    // Retrieve all taxiway nodes from the database
    public List<TaxiwayNode> getAllNodes() throws SQLException {

        List<TaxiwayNode> nodes = new ArrayList<>();

        String sql = "SELECT node_id, node_name FROM taxiway_nodes";

        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet result = statement.executeQuery(sql)) {

            while (result.next()) {

                String nodeId = result.getString("node_id");
                String nodeName = result.getString("node_name");

                nodes.add(new TaxiwayNode(nodeId, nodeName));
            }
        }

        return nodes;
    }
}
