
package com.aerova.dao;

import com.aerova.model.GroundService;
import com.aerova.model.ServiceStatus;
import com.aerova.model.ServiceType;
import com.aerova.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRepository {

    // Save a new service in MySQL
    public boolean saveService(GroundService service) {

        String sql = "INSERT INTO ground_services " +
                "(service_id, flight_id, service_type, service_status) " +
                "VALUES (?, ?, ?, ?)";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, service.getServiceId());
            ps.setInt(2, Integer.parseInt(service.getFlightId()));
            ps.setString(3, service.getServiceType().name());
            ps.setString(4, service.getStatus().name());

            return ps.executeUpdate() > 0;

        } catch (SQLException | NumberFormatException e) {
            System.out.println("Error saving service: " + e.getMessage());
            return false;
        }
    }

    // Find service using ID
    public GroundService findServiceById(String serviceId) {

        String sql = "SELECT * FROM ground_services WHERE service_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, serviceId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapService(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding service: " + e.getMessage());
        }

        return null;
    }

    // Fetch all services
    public List<GroundService> getAllServices() {

        List<GroundService> services = new ArrayList<>();

        String sql = "SELECT * FROM ground_services";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                services.add(mapService(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error fetching services: " + e.getMessage());
        }

        return services;
    }

    // Update service status
    public boolean updateServiceStatus(
            String serviceId, ServiceStatus status) {

        String sql = "UPDATE ground_services " +
                "SET service_status = ? WHERE service_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, status.name());
            ps.setString(2, serviceId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error updating service status: "
                    + e.getMessage());
            return false;
        }
    }

    // Save assigned crew ID against a service
    public boolean assignCrewToService(
            String serviceId, String crewId) {

        String sql = "UPDATE ground_services " +
                "SET assigned_crew_id = ? WHERE service_id = ?";

        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, crewId);
            ps.setString(2, serviceId);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error assigning crew to service: "
                    + e.getMessage());
            return false;
        }
    }

    // Convert database row into Java object
    private GroundService mapService(ResultSet rs) throws SQLException {

        GroundService service = new GroundService(
                rs.getString("service_id"),
                String.valueOf(rs.getInt("flight_id")),
                ServiceType.valueOf(rs.getString("service_type"))
        );

        service.setStatus(
                ServiceStatus.valueOf(rs.getString("service_status"))
        );

        service.setAssignedCrewId(
                rs.getString("assigned_crew_id")
        );

        return service;
    }
}

