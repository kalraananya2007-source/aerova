
package com.aerova;

import com.aerova.model.GroundService;
import com.aerova.model.ServiceType;
import com.aerova.model.ServiceStatus;

public class GroundServiceTest {

    public static void main(String[] args) {

        GroundService service1 = new GroundService(
                "SV101", "FL201", ServiceType.BAGGAGE
        );

        GroundService service2 = new GroundService(
                "SV102", "FL201", ServiceType.CLEANING
        );

        GroundService service3 = new GroundService(
                "SV103", "FL201", ServiceType.REFUELING
        );

        System.out.println("CREATED SERVICES");

        service1.displayService();
        service2.displayService();
        service3.displayService();

        // Assign crew to baggage
        service1.setAssignedCrewId("CR101");

        // Start baggage service
        service1.setStatus(ServiceStatus.IN_PROGRESS);

        System.out.println("\nAFTER ASSIGNMENT AND START");

        service1.displayService();

        // Complete baggage service
        service1.setStatus(ServiceStatus.COMPLETED);

        System.out.println("\nAFTER COMPLETION");

        service1.displayService();
    }
}

