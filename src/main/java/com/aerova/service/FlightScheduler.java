
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightType;
import com.aerova.model.FlightStatus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;

public class FlightScheduler {

    private final FlightManager flightManager;

    public FlightScheduler() {
        this.flightManager = new FlightManager();
    }

    // Get all scheduled arrival flights
    public ArrayList<Flight> getScheduledArrivals() throws SQLException {

        ArrayList<Flight> allFlights = flightManager.getAllFlights();

        ArrayList<Flight> arrivals = new ArrayList<>();

        for (Flight flight : allFlights) {

            if (flight.getFlightType() == FlightType.ARRIVAL
                    && flight.getStatus() == FlightStatus.SCHEDULED) {

                arrivals.add(flight);
            }
        }

        // Sort flights according to scheduled time
        arrivals.sort(Comparator.comparing(Flight::getScheduledTime));

        return arrivals;
    }
}

