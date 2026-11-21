
package com.aerova.service;

import com.aerova.model.Flight;
import com.aerova.model.FlightPriority;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class FlightPriorityManager {

    // Determines the priority of a flight
    public FlightPriority getPriority(Flight flight) {

        if (flight == null) {
            throw new IllegalArgumentException("Flight cannot be null.");
        }

        if (flight.isEmergency()) {
            return FlightPriority.EMERGENCY;
        }

        if (flight.isDelayed()) {
            return FlightPriority.DELAYED;
        }

        return FlightPriority.NORMAL;
    }

    // Higher priority flights come first
    private final Comparator<Flight> priorityComparator =
            Comparator.comparingInt((Flight flight) -> {
                FlightPriority priority = getPriority(flight);

                if (priority == FlightPriority.EMERGENCY) {
                    return 1;
                } else if (priority == FlightPriority.DELAYED) {
                    return 2;
                } else {
                    return 3;
                }
            }).thenComparing(Flight::getScheduledTime);

    // Creates a priority queue
    public PriorityQueue<Flight> createPriorityQueue(
            ArrayList<Flight> flights) {

        PriorityQueue<Flight> queue =
                new PriorityQueue<>(priorityComparator);

        if (flights != null) {
            queue.addAll(flights);
        }

        return queue;
    }

    // Returns flights in priority order
    public ArrayList<Flight> sortByPriority(ArrayList<Flight> flights) {

        PriorityQueue<Flight> queue = createPriorityQueue(flights);
        ArrayList<Flight> sortedFlights = new ArrayList<>();

        while (!queue.isEmpty()) {
            sortedFlights.add(queue.poll());
        }

        return sortedFlights;
    }

    // Returns the next highest-priority flight
    public Flight getNextPriorityFlight(PriorityQueue<Flight> queue) {

        if (queue == null || queue.isEmpty()) {
            return null;
        }

        return queue.poll();
    }
}
