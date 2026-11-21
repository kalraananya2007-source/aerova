
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightPriority;
import com.aerova.model.FlightStatus;

import java.util.ArrayList;
import java.util.Comparator;

public class FlightPriorityManager {

    public FlightPriority getPriority(Flight flight) {

        if (flight.isEmergency()) {
            return FlightPriority.EMERGENCY;
        }

        if (flight.getStatus() == FlightStatus.DELAYED) {
            return FlightPriority.DELAYED;
        }

        return FlightPriority.NORMAL;
    }

    public ArrayList<Flight> sortByPriority(ArrayList<Flight> flights) {

        ArrayList<Flight> sortedFlights = new ArrayList<>(flights);

        sortedFlights.sort(
            Comparator.comparingInt(flight -> {
                FlightPriority priority = getPriority(flight);

                if (priority == FlightPriority.EMERGENCY) {
                    return 1;
                } else if (priority == FlightPriority.DELAYED) {
                    return 2;
                } else {
                    return 3;
                }
            })
        );

        return sortedFlights;
    }
}
