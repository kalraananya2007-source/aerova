
package com.aerova.model;

public class GroundCrew {

    private String crewId;
    private String name;
    private CrewSkill skill;
    private CrewStatus status;
    private String assignedServiceId;

    // Constructor
    public GroundCrew(String crewId, String name, CrewSkill skill) {
        this.crewId = crewId;
        this.name = name;
        this.skill = skill;
        this.status = CrewStatus.AVAILABLE;
        this.assignedServiceId = null;
    }

    // Getters
    public String getCrewId() {
        return crewId;
    }

    public String getName() {
        return name;
    }

    public CrewSkill getSkill() {
        return skill;
    }

    public CrewStatus getStatus() {
        return status;
    }

    public String getAssignedServiceId() {
        return assignedServiceId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setSkill(CrewSkill skill) {
        this.skill = skill;
    }

    public void setStatus(CrewStatus status) {
        this.status = status;
    }

    public void setAssignedServiceId(String assignedServiceId) {
        this.assignedServiceId = assignedServiceId;
    }

    // Check availability
    public boolean isAvailable() {
        return status == CrewStatus.AVAILABLE;
    }

    // Display crew details
    public void displayCrew() {
        System.out.println("------------------------------------");
        System.out.println("Crew ID       : " + crewId);
        System.out.println("Name          : " + name);
        System.out.println("Skill         : " + skill);
        System.out.println("Status        : " + status);
        System.out.println("Assigned Task : " + assignedServiceId);
        System.out.println("------------------------------------");
    }
}
