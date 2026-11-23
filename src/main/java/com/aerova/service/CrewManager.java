
package com.aerova.service;

import com.aerova.dao.CrewRepository;
import com.aerova.model.CrewSkill;
import com.aerova.model.CrewStatus;
import com.aerova.model.GroundCrew;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class CrewManager {

    private final Map<String, GroundCrew> crewMap;
    private final CrewRepository crewRepository;

    public CrewManager() {
        this.crewMap = new LinkedHashMap<>();
        this.crewRepository = new CrewRepository();

        // Load existing crew members from MySQL
        for (GroundCrew crew : crewRepository.getAllCrew()) {
            crewMap.put(crew.getCrewId(), crew);
        }
    }

    // Register a new crew member
    public void addCrew(GroundCrew crew) {

        Objects.requireNonNull(crew, "Crew cannot be null.");

        if (crew.getCrewId() == null || crew.getCrewId().isBlank()) {
            throw new IllegalArgumentException("Crew ID cannot be empty.");
        }

        if (crew.getName() == null || crew.getName().isBlank()) {
            throw new IllegalArgumentException("Crew name cannot be empty.");
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

        // Save in MySQL first
        if (!crewRepository.saveCrew(crew)) {
            throw new IllegalStateException(
                    "Could not save crew to database."
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

        // Update MySQL first
        if (!crewRepository.updateCrewStatus(
                crewId, CrewStatus.ASSIGNED.name())) {
            throw new IllegalStateException(
                    "Could not update crew status in database."
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

        // Update MySQL first
        if (!crewRepository.updateCrewStatus(
                crewId, CrewStatus.AVAILABLE.name())) {
            throw new IllegalStateException(
                    "Could not release crew in database."
            );
        }

        crew.setStatus(CrewStatus.AVAILABLE);
        crew.setAssignedServiceId(null);
    }
}

