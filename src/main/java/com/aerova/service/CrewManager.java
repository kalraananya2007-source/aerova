
package com.aerova.service;

import com.aerova.model.CrewSkill;
import com.aerova.model.CrewStatus;
import com.aerova.model.GroundCrew;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CrewManager {

    private final Map<String, GroundCrew> crewMap;

    public CrewManager() {
        this.crewMap = new LinkedHashMap<>();
    }

    // Register a new crew member
    public void addCrew(GroundCrew crew) {

        Objects.requireNonNull(crew, "Crew cannot be null.");

        if (crew.getCrewId() == null || crew.getCrewId().isBlank()) {
            throw new IllegalArgumentException(
                    "Crew ID cannot be empty."
            );
        }

        if (crew.getName() == null || crew.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Crew name cannot be empty."
            );
        }

        Objects.requireNonNull(
                crew.getSkill(),
                "Crew skill cannot be null."
        );

        if (crewMap.containsKey(crew.getCrewId())) {
            throw new IllegalArgumentException(
                    "Crew ID already exists: " + crew.getCrewId()
            );
        }

        crewMap.put(crew.getCrewId(), crew);
    }

    // Get all crew members
    public ArrayList<GroundCrew> getAllCrew() {
        return new ArrayList<>(crewMap.values());
    }

    // Search crew by ID
    public GroundCrew getCrewById(String crewId) {

        if (crewId == null || crewId.isBlank()) {
            throw new IllegalArgumentException(
                    "Crew ID cannot be empty."
            );
        }

        return crewMap.get(crewId.trim());
    }

    // Get only available crew members
    public ArrayList<GroundCrew> getAvailableCrew() {

        ArrayList<GroundCrew> result = new ArrayList<>();

        for (GroundCrew crew : crewMap.values()) {
            if (crew.isAvailable()) {
                result.add(crew);
            }
        }

        return result;
    }

    // Find available crew members with a particular skill
    public ArrayList<GroundCrew> getAvailableCrewBySkill(CrewSkill skill) {

        Objects.requireNonNull(skill, "Crew skill cannot be null.");

        ArrayList<GroundCrew> result = new ArrayList<>();

        for (GroundCrew crew : crewMap.values()) {
            if (crew.isAvailable() && crew.getSkill() == skill) {
                result.add(crew);
            }
        }

        return result;
    }

    // Assign crew member to a service
    public void assignCrewToService(String crewId, String serviceId) {

        GroundCrew crew = getCrewById(crewId);

        if (crew == null) {
            throw new IllegalArgumentException(
                    "Crew member not found: " + crewId
            );
        }

        if (serviceId == null || serviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "Service ID cannot be empty."
            );
        }

        if (!crew.isAvailable()) {
            throw new IllegalStateException(
                    "Crew member is not available."
            );
        }

        crew.setStatus(CrewStatus.ASSIGNED);
        crew.setAssignedServiceId(serviceId.trim());
    }

    // Release crew member after service
    public void releaseCrew(String crewId) {

        GroundCrew crew = getCrewById(crewId);

        if (crew == null) {
            throw new IllegalArgumentException(
                    "Crew member not found: " + crewId
            );
        }

        if (crew.getStatus() != CrewStatus.ASSIGNED) {
            throw new IllegalStateException(
                    "Crew member is not currently assigned."
            );
        }

        crew.setStatus(CrewStatus.AVAILABLE);
        crew.setAssignedServiceId(null);
    }
}

