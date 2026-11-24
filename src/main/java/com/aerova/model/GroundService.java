
package com.aerova.model;

public class GroundService {

    private String serviceId;
    private String flightId;
    private ServiceType serviceType;
    private ServiceStatus status;
    private String assignedCrewId;

    // Constructor
    public GroundService(String serviceId, String flightId,
                        ServiceType serviceType) {

        this.serviceId = serviceId;
        this.flightId = flightId;
        this.serviceType = serviceType;
        this.status = ServiceStatus.PENDING;
        this.assignedCrewId = null;
    }

    // Getters
    public String getServiceId() {
        return serviceId;
    }

    public String getFlightId() {
        return flightId;
    }

    public ServiceType getServiceType() {
        return serviceType;
    }

    public ServiceStatus getStatus() {
        return status;
    }

    public String getAssignedCrewId() {
        return assignedCrewId;
    }

    // Setters
    public void setStatus(ServiceStatus status) {
        this.status = status;
    }

    public void setAssignedCrewId(String assignedCrewId) {
        this.assignedCrewId = assignedCrewId;
    }

    // Display service details
    public void displayService() {
        System.out.println("------------------------------------");
        System.out.println("Service ID    : " + serviceId);
        System.out.println("Flight ID     : " + flightId);
        System.out.println("Service Type  : " + serviceType);
        System.out.println("Status        : " + status);
        System.out.println("Assigned Crew : " + assignedCrewId);
        System.out.println("------------------------------------");
    }
}

