
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightType;
import com.aerova.model.FlightStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FlightAnalytics {

    // Total number of flights
    public int getTotalFlights(ArrayList<Flight> flights) {
        return flights == null ? 0 : flights.size();
    }

    // Count arrival flights
    public int getArrivalCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(flight -> flight.getFlightType() == FlightType.ARRIVAL)
                .count();
    }

    // Count departure flights
    public int getDepartureCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(flight -> flight.getFlightType() == FlightType.DEPARTURE)
                .count();
    }

    // Count delayed flights
    public int getDelayedFlightCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(Flight::isDelayed)
                .count();
    }

    // Count cancelled flights
    public int getCancelledFlightCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(flight -> flight.getStatus() == FlightStatus.CANCELLED)
                .count();
    }

    // Count departed flights
    public int getDepartedFlightCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(flight -> flight.getStatus() == FlightStatus.DEPARTED)
                .count();
    }

    // Count emergency flights
    public int getEmergencyFlightCount(ArrayList<Flight> flights) {
        if (flights == null) return 0;

        return (int) flights.stream()
                .filter(Flight::isEmergency)
                .count();
    }

    // Airline-wise flight count using HashMap
    public HashMap<String, Integer> getAirlineWiseCount(
            ArrayList<Flight> flights) {

        HashMap<String, Integer> airlineCounts = new HashMap<>();

        if (flights == null) return airlineCounts;

        for (Flight flight : flights) {
            String airline = flight.getAirline();

            if (airline != null && !airline.isBlank()) {
                airlineCounts.merge(airline, 1, Integer::sum);
            }
        }

        return airlineCounts;
    }

    // Calculate average delay in minutes
    public double getAverageDelay(ArrayList<Flight> flights) {
        if (flights == null || flights.isEmpty()) return 0.0;

        return flights.stream()
                .filter(Flight::isDelayed)
                .mapToInt(Flight::getDelayMinutes)
                .average()
                .orElse(0.0);
    }
}
