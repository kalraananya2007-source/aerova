
package com.aerova;

import com.aerova.model.CrewSkill;
import com.aerova.model.CrewStatus;
import com.aerova.model.GroundCrew;

public class GroundCrewTest {

    public static void main(String[] args) {

        GroundCrew crew1 = new GroundCrew(
                "CR101", "Rahul", CrewSkill.BAGGAGE
        );

        GroundCrew crew2 = new GroundCrew(
                "CR102", "Aman", CrewSkill.REFUELING
        );

        crew1.displayCrew();
        crew2.displayCrew();

        System.out.println("Is Rahul available? " + crew1.isAvailable());

        crew1.setStatus(CrewStatus.ASSIGNED);
        crew1.setAssignedServiceId("SV101");

        System.out.println("\nAfter assignment:");

        crew1.displayCrew();

        System.out.println("Is Rahul available? " + crew1.isAvailable());
    }
}

