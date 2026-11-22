
package com.aerova;

import com.aerova.model.CrewSkill;
import com.aerova.model.GroundCrew;
import com.aerova.model.GroundService;
import com.aerova.model.ServiceType;
import com.aerova.service.CrewManager;
import com.aerova.service.ServiceManager;

public class ServiceManagerTest {

    public static void main(String[] args) {

        CrewManager crewManager = new CrewManager();
        ServiceManager serviceManager = new ServiceManager(crewManager);

        // Register crew
        crewManager.addCrew(
                new GroundCrew("CR101", "Rahul", CrewSkill.BAGGAGE)
        );

        crewManager.addCrew(
                new GroundCrew("CR102", "Aman", CrewSkill.CLEANING)
        );

        crewManager.addCrew(
                new GroundCrew("CR103", "Karan", CrewSkill.REFUELING)
        );

        // Create services for flight FL201
        GroundService baggage = serviceManager.createService(
                "FL201", ServiceType.BAGGAGE
        );

        GroundService cleaning = serviceManager.createService(
                "FL201", ServiceType.CLEANING
        );

        GroundService refueling = serviceManager.createService(
                "FL201", ServiceType.REFUELING
        );

        System.out.println("CREATED SERVICES");

        baggage.displayService();
        cleaning.displayService();
        refueling.displayService();

        // Assign crew
        serviceManager.assignCrewToService("SV101", "CR101");
        serviceManager.assignCrewToService("SV102", "CR102");
        serviceManager.assignCrewToService("SV103", "CR103");

        // Start services
        serviceManager.startService("SV101");
        serviceManager.startService("SV102");
        serviceManager.startService("SV103");

        System.out.println("\nSERVICES IN PROGRESS");

        for (GroundService service :
                serviceManager.getServicesByFlight("FL201")) {
            service.displayService();
        }

        // Complete services
        serviceManager.completeService("SV101");
        serviceManager.completeService("SV102");
        serviceManager.completeService("SV103");

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

