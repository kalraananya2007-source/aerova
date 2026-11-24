

package com.aerova;

import com.aerova.model.GroundCrew;
import com.aerova.model.GroundService;
import com.aerova.model.CrewSkill;
import com.aerova.model.ServiceStatus;
import com.aerova.model.ServiceType;
import com.aerova.service.CrewManager;
import com.aerova.service.ServiceManager;
import com.aerova.service.TurnaroundManager;

import java.util.ArrayList;

public class TurnaroundManagerTest {

    public static void main(String[] args) {

        CrewManager crewManager = new CrewManager();
        ServiceManager serviceManager = new ServiceManager(crewManager);
        TurnaroundManager turnaroundManager =
                new TurnaroundManager(serviceManager);

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

        // Create ground services
        GroundService s1 = serviceManager.createService(
                "FL201", ServiceType.BAGGAGE
        );

        GroundService s2 = serviceManager.createService(
                "FL201", ServiceType.CLEANING
        );

        GroundService s3 = serviceManager.createService(
                "FL201", ServiceType.REFUELING
        );

        System.out.println("=== TURNAROUND CHECK 1 ===");

        // Complete only two services
        s1.setStatus(ServiceStatus.COMPLETED);
        s2.setStatus(ServiceStatus.COMPLETED);
        s3.setStatus(ServiceStatus.IN_PROGRESS);

        System.out.println("All services completed: "
                + turnaroundManager.areAllServicesCompleted("FL201"));

        System.out.println("Incomplete services:");
        ArrayList<GroundService> pending =
                turnaroundManager.getPendingServicesForFlight("FL201");

        for (GroundService service : pending) {
            service.displayService();
        }

        turnaroundManager.markAircraftReady("FL201");

        System.out.println("\n=== TURNAROUND CHECK 2 ===");

        // Complete the final service
        s3.setStatus(ServiceStatus.COMPLETED);

        System.out.println("All services completed: "
                + turnaroundManager.areAllServicesCompleted("FL201"));

        turnaroundManager.markAircraftReady("FL201");
    }
}