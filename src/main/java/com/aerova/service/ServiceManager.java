
package com.aerova.service;

import com.aerova.model.CrewSkill;
import com.aerova.model.CrewStatus;
import com.aerova.model.GroundCrew;
import com.aerova.model.GroundService;
import com.aerova.model.ServiceStatus;
import com.aerova.model.ServiceType;

import java.util.ArrayList;
import java.util.Objects;

public class ServiceManager {

    private final ArrayList<GroundService> services;
    private final CrewManager crewManager;
    private int serviceCounter;

    public ServiceManager(CrewManager crewManager) {
        this.services = new ArrayList<>();
        this.crewManager = Objects.requireNonNull(
                crewManager, "CrewManager cannot be null."
        );
        this.serviceCounter = 100;
    }

    // Create a new ground service
    public GroundService createService(String flightId, ServiceType type) {

        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException(
                    "Flight ID cannot be empty."
            );
        }

        Objects.requireNonNull(type, "Service type cannot be null.");

        serviceCounter++;

        String serviceId = "SV" + serviceCounter;

        GroundService service = new GroundService(
                serviceId, flightId.trim(), type
        );

        services.add(service);

        return service;
    }

    // Get all services
    public ArrayList<GroundService> getAllServices() {
        return new ArrayList<>(services);
    }

    // Search service using service ID
    public GroundService getServiceById(String serviceId) {

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Service ID cannot be empty."
            );
        }

        for (GroundService service : services) {
            if (service.getServiceId().equalsIgnoreCase(serviceId.trim())) {
                return service;
            }
        }

        return null;
    }

    // Get services belonging to a particular flight
    public ArrayList<GroundService> getServicesByFlight(String flightId) {

        if (flightId == null || flightId.isBlank()) {
            throw new IllegalArgumentException(
                    "Flight ID cannot be empty."
            );
        }

        ArrayList<GroundService> result = new ArrayList<>();

        for (GroundService service : services) {
            if (service.getFlightId().equalsIgnoreCase(flightId.trim())) {
                result.add(service);
            }
        }

        return result;
    }

    // Find the required crew skill for a service
    private CrewSkill getRequiredSkill(ServiceType type) {

        switch (type) {
            case BAGGAGE:
                return CrewSkill.BAGGAGE;

            case CLEANING:
                return CrewSkill.CLEANING;

            case REFUELING:
                return CrewSkill.REFUELING;

            case MAINTENANCE:
                return CrewSkill.MAINTENANCE;

            default:
                throw new IllegalArgumentException(
                        "Unsupported service type."
                );
        }
    }

    // Assign crew to a service
    public void assignCrewToService(String serviceId, String crewId) {

        GroundService service = getServiceById(serviceId);

        if (service == null) {
            throw new IllegalArgumentException(
                    "Service not found: " + serviceId
            );
        }

        GroundCrew crew = crewManager.getCrewById(crewId);

        if (crew == null) {
            throw new IllegalArgumentException(
                    "Crew member not found: " + crewId
            );
        }

        if (service.getStatus() != ServiceStatus.PENDING) {
            throw new IllegalStateException(
                    "Crew can only be assigned to pending services."
            );
        }

        if (!crew.isAvailable()) {
            throw new IllegalStateException(
                    "Crew member is not available."
            );
        }

        CrewSkill requiredSkill = getRequiredSkill(service.getServiceType());

        if (crew.getSkill() != requiredSkill) {
            throw new IllegalArgumentException(
                    "Crew skill does not match the service requirement."
            );
        }

        crewManager.assignCrewToService(
                crew.getCrewId(), service.getServiceId()
        );

        service.setAssignedCrewId(crew.getCrewId());
    }

    // Start a service
    public void startService(String serviceId) {

        GroundService service = getServiceById(serviceId);

        if (service == null) {
            throw new IllegalArgumentException(
                    "Service not found: " + serviceId
            );
        }

        if (service.getAssignedCrewId() == null) {
            throw new IllegalStateException(
                    "A crew member must be assigned before starting."
            );
        }

        if (service.getStatus() != ServiceStatus.PENDING) {
            throw new IllegalStateException(
                    "Only pending services can be started."
            );
        }

        service.setStatus(ServiceStatus.IN_PROGRESS);
    }

    // Complete a service and release its crew
    public void completeService(String serviceId) {

        GroundService service = getServiceById(serviceId);

        if (service == null) {
            throw new IllegalArgumentException(
                    "Service not found: " + serviceId
            );
        }

        if (service.getStatus() != ServiceStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Only in-progress services can be completed."
            );
        }

        String crewId = service.getAssignedCrewId();

        service.setStatus(ServiceStatus.COMPLETED);

        if (crewId != null) {
            crewManager.releaseCrew(crewId);
        }
    }

    // Get all pending services
    public ArrayList<GroundService> getPendingServices() {

        ArrayList<GroundService> result = new ArrayList<>();

        for (GroundService service : services) {
            if (service.getStatus() == ServiceStatus.PENDING) {
                result.add(service);
            }
        }

        return result;
    }

    // Get all completed services
    public ArrayList<GroundService> getCompletedServices() {

        ArrayList<GroundService> result = new ArrayList<>();

        for (GroundService service : services) {
            if (service.getStatus() == ServiceStatus.COMPLETED) {
                result.add(service);
            }
        }

        return result;
    }
}

