
package com.aerova;

import com.aerova.model.CrewSkill;
import com.aerova.model.GroundCrew;
import com.aerova.service.CrewManager;

public class CrewManagerTest {

    public static void main(String[] args) {

        CrewManager manager = new CrewManager();

        GroundCrew crew1 = new GroundCrew(
                "CR101", "Rahul", CrewSkill.BAGGAGE
        );

        GroundCrew crew2 = new GroundCrew(
                "CR102", "Aman", CrewSkill.REFUELING
        );

        GroundCrew crew3 = new GroundCrew(
                "CR103", "Karan", CrewSkill.BAGGAGE
        );

        // Register crew
        manager.addCrew(crew1);
        manager.addCrew(crew2);
        manager.addCrew(crew3);

        System.out.println("ALL CREW");
        for (GroundCrew crew : manager.getAllCrew()) {
            crew.displayCrew();
        }

        System.out.println("\nAVAILABLE BAGGAGE CREW");

        for (GroundCrew crew :
                manager.getAvailableCrewBySkill(CrewSkill.BAGGAGE)) {
            crew.displayCrew();
        }

        // Assign Rahul
        manager.assignCrewToService("CR101", "SV101");

        System.out.println("\nAFTER ASSIGNMENT");
        manager.getCrewById("CR101").displayCrew();

        // Release Rahul
        manager.releaseCrew("CR101");

        System.out.println("\nAFTER RELEASE");
        manager.getCrewById("CR101").displayCrew();

        System.out.println(
                "\nAvailable crew count: "
                + manager.getAvailableCrew().size()
        );
    }
}

