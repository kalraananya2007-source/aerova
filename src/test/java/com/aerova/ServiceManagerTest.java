
package com.aerova;

import com.aerova.model.GroundService;
import com.aerova.model.ServiceType;
import com.aerova.service.CrewManager;
import com.aerova.service.ServiceManager;

public class ServiceManagerTest {

    public static void main(String[] args) {

        CrewManager crewManager = new CrewManager();
        ServiceManager serviceManager = new ServiceManager(crewManager);

        // Create services for existing flight ID 1
        GroundService baggage = serviceManager.createService(
                "1", ServiceType.BAGGAGE
        );

        GroundService cleaning = serviceManager.createService(
                "1", ServiceType.CLEANING
        );

        System.out.println("CREATED SERVICES");
        baggage.displayService();
        cleaning.displayService();

        // Assign existing crew
        serviceManager.assignCrewToService(
                baggage.getServiceId(), "CR101"
        );

        serviceManager.assignCrewToService(
                cleaning.getServiceId(), "CR102"
        );

        // Start services
        serviceManager.startService(baggage.getServiceId());
        serviceManager.startService(cleaning.getServiceId());

        System.out.println("\nSERVICES IN PROGRESS");

        for (GroundService service :
                serviceManager.getServicesByFlight("1")) {
            service.displayService();
        }

        // Complete services
        serviceManager.completeService(baggage.getServiceId());
        serviceManager.completeService(cleaning.getServiceId());

        System.out.println("\nCOMPLETED SERVICES");

        for (GroundService service :
                serviceManager.getCompletedServices()) {
            service.displayService();
        }

        System.out.println(
                "\nAvailable crew after completion: "
                + crewManager.getAvailableCrew().size()
        );
    }
}
