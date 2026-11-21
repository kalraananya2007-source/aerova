
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightType;
import com.aerova.model.FlightStatus;

import java.util.ArrayList;

public class FlightAnalytics {

    public int getTotalFlights(ArrayList<Flight> flights) {
        return flights.size();
    }

    public int getArrivalCount(ArrayList<Flight> flights) {
        int count = 0;

        for (Flight flight : flights) {
            if (flight.getFlightType() == FlightType.ARRIVAL) {
                count++;
            }
        }

        return count;
    }

    public int getDepartureCount(ArrayList<Flight> flights) {
        int count = 0;

        for (Flight flight : flights) {
            if (flight.getFlightType() == FlightType.DEPARTURE) {
                count++;
            }
        }

        return count;
    }

    public int getDelayedFlightCount(ArrayList<Flight> flights) {
        int count = 0;

        for (Flight flight : flights) {
            if (flight.getStatus() == FlightStatus.DELAYED) {
                count++;
            }
        }

        return count;
    }

    public int getEmergencyFlightCount(ArrayList<Flight> flights) {
        int count = 0;

        for (Flight flight : flights) {
            if (flight.isEmergency()) {
                count++;
            }
        }

        return count;
    }
}
