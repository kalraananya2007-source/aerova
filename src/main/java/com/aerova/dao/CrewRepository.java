

package com.aerova.dao;

import com.aerova.model.GroundCrew;
import com.aerova.model.CrewSkill;
import com.aerova.model.CrewStatus;
import com.aerova.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CrewRepository {

    // Save crew member in MySQL
    public boolean saveCrew(GroundCrew crew) {

        String sql = "INSERT INTO ground_crew " +
                "(crew_id, crew_name, crew_role, crew_status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, crew.getCrewId());
            ps.setString(2, crew.getName());
            ps.setString(3, crew.getSkill().name());
            ps.setString(4, crew.getStatus().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving crew: " + e.getMessage());
            return false;
        }
    }

    // Find crew using ID
    public GroundCrew findCrewById(String crewId) {

        String sql = "SELECT * FROM ground_crew WHERE crew_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, crewId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapCrew(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding crew: " + e.getMessage());
        }

        return null;
    }

    // Fetch all crew members
    public List<GroundCrew> getAllCrew() {

        List<GroundCrew> crewList = new ArrayList<>();

        String sql = "SELECT * FROM ground_crew";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                crewList.add(mapCrew(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching crew: " + e.getMessage());
        }

        return crewList;
    }

    // Update crew status
    public boolean updateCrewStatus(String crewId, String status) {

        String sql = "UPDATE ground_crew SET crew_status = ? WHERE crew_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status);
            ps.setString(2, crewId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating crew: " + e.getMessage());
            return false;
        }
    }

    // Delete crew member
    public boolean deleteCrew(String crewId) {

        String sql = "DELETE FROM ground_crew WHERE crew_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, crewId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error deleting crew: " + e.getMessage());
            return false;
        }
    }

    // Convert database row into Java object
    private GroundCrew mapCrew(ResultSet rs) throws SQLException {

        GroundCrew crew = new GroundCrew(
                rs.getString("crew_id"),
                rs.getString("crew_name"),
                CrewSkill.valueOf(rs.getString("crew_role"))
        );

        crew.setStatus(
                CrewStatus.valueOf(rs.getString("crew_status"))
        );

        return crew;
    }
}
