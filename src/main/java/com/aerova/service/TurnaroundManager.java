package com.aerova.service;

import com.aerova.model.GroundService;
import com.aerova.model.ServiceStatus;

import java.util.ArrayList;

public class TurnaroundManager {

    private final ServiceManager serviceManager;

    public TurnaroundManager(ServiceManager serviceManager) {
        this.serviceManager = serviceManager;
    }

    public boolean areAllServicesCompleted(String flightId) {

        ArrayList<GroundService> services =
                serviceManager.getServicesByFlight(flightId);

        if (services.isEmpty()) {
            return false;
        }

        for (GroundService service : services) {
            if (service.getStatus() != ServiceStatus.COMPLETED) {
                return false;
            }
        }

        return true;
    }

    public ArrayList<GroundService> getPendingServicesForFlight(String flightId) {

        ArrayList<GroundService> incompleteServices = new ArrayList<>();

        ArrayList<GroundService> services =
                serviceManager.getServicesByFlight(flightId);

        for (GroundService service : services) {
            if (service.getStatus() != ServiceStatus.COMPLETED) {
                incompleteServices.add(service);
            }
        }

        return incompleteServices;
    }

    public boolean markAircraftReady(String flightId) {

        if (areAllServicesCompleted(flightId)) {
            System.out.println("Aircraft is ready for departure!");
            return true;
        }

        System.out.println("Aircraft is NOT ready for departure.");
        return false;
    }
}
