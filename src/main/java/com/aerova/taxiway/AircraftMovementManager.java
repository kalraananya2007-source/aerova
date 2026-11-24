
package com.aerova.taxiway;

import com.aerova.model.Flight;

import java.sql.SQLException;
import java.util.*;

public class AircraftMovementManager {

    private final RouteFinder routeFinder;
    private final MovementRepository movementRepository;

    private final Map<Integer, List<TaxiwayNode>> flightRoutes;
    private final Map<Integer, Integer> currentPositions;
    private final Map<Integer, MovementStatus> movementStatuses;
    private final Map<Integer, Integer> movementIds;

    public AircraftMovementManager(RouteFinder routeFinder) {

        if (routeFinder == null) {
            throw new IllegalArgumentException(
                    "RouteFinder cannot be null.");
        }

        this.routeFinder = routeFinder;
        this.movementRepository = new MovementRepository();

        flightRoutes = new HashMap<>();
        currentPositions = new HashMap<>();
        movementStatuses = new HashMap<>();
        movementIds = new HashMap<>();
    }

    // Plan movement and save it in the database
    public void planMovement(
            Flight flight,
            String sourceId,
            String destinationId) {

        if (flight == null) {
            throw new IllegalArgumentException(
                    "Flight cannot be null.");
        }

        int flightId = flight.getFlightId();

        if (flightRoutes.containsKey(flightId)) {
            throw new IllegalStateException(
                    "Movement already exists for flight: " + flightId);
        }

        List<TaxiwayNode> route =
                routeFinder.findShortestRoute(sourceId, destinationId);

        if (route.isEmpty()) {
            throw new IllegalStateException(
                    "No available taxiway route found.");
        }

        try {
            int movementId =
                    movementRepository.saveMovement(flightId, route);

            flightRoutes.put(flightId, new ArrayList<>(route));
            currentPositions.put(flightId, 0);
            movementStatuses.put(flightId, MovementStatus.WAITING);
            movementIds.put(flightId, movementId);

            System.out.println(
                    "Movement planned for flight: " + flightId);
            System.out.println(
                    "Movement database ID: " + movementId);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not save movement in database.", e);
        }
    }

    // Start aircraft movement
    public void startMovement(int flightId) {

        checkFlightExists(flightId);

        MovementStatus status = movementStatuses.get(flightId);

        if (status != MovementStatus.WAITING &&
                status != MovementStatus.STOPPED) {

            throw new IllegalStateException(
                    "Flight cannot start from status: " + status);
        }

        try {
            TaxiwayNode currentNode = getCurrentNode(flightId);
            int movementId = movementIds.get(flightId);

            movementRepository.updateMovement(
                    movementId, MovementStatus.MOVING,
                    currentNode.getNodeId());

            movementRepository.saveLog(
                    movementId, "STARTED",
                    currentNode.getNodeId(),
                    "Aircraft movement started.");

            movementStatuses.put(flightId, MovementStatus.MOVING);

            System.out.println(
                    "Flight " + flightId + " started moving.");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not start movement in database.", e);
        }
    }

    // Move aircraft to the next taxiway node
    public TaxiwayNode moveToNextNode(int flightId) {

        checkFlightExists(flightId);

        if (movementStatuses.get(flightId) != MovementStatus.MOVING) {
            throw new IllegalStateException(
                    "Flight is not currently moving.");
        }

        List<TaxiwayNode> route = flightRoutes.get(flightId);
        int position = currentPositions.get(flightId);

        if (position >= route.size() - 1) {
            return route.get(position);
        }

        int nextPosition = position + 1;
        TaxiwayNode currentNode = route.get(nextPosition);

        MovementStatus newStatus =
                (nextPosition == route.size() - 1)
                        ? MovementStatus.COMPLETED
                        : MovementStatus.MOVING;

        try {
            int movementId = movementIds.get(flightId);

            movementRepository.updateMovement(
                    movementId, newStatus, currentNode.getNodeId());

            movementRepository.saveLog(
                    movementId, "NODE_REACHED",
                    currentNode.getNodeId(),
                    "Aircraft reached " + currentNode.getNodeName());

            if (newStatus == MovementStatus.COMPLETED) {
                movementRepository.saveLog(
                        movementId, "COMPLETED",
                        currentNode.getNodeId(),
                        "Aircraft reached its destination.");
            }

            currentPositions.put(flightId, nextPosition);
            movementStatuses.put(flightId, newStatus);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not update aircraft movement in database.", e);
        }

        System.out.println(
                "Flight " + flightId +
                        " reached " + currentNode.getNodeName());

        if (movementStatuses.get(flightId) == MovementStatus.COMPLETED) {
            System.out.println(
                    "Flight " + flightId +
                            " reached its destination.");
        }

        return currentNode;
    }

    // Stop aircraft movement
    public void stopMovement(int flightId) {

        checkFlightExists(flightId);

        if (movementStatuses.get(flightId) != MovementStatus.MOVING) {
            throw new IllegalStateException(
                    "Only moving flights can be stopped.");
        }

        try {
            TaxiwayNode currentNode = getCurrentNode(flightId);
            int movementId = movementIds.get(flightId);

            movementRepository.updateMovement(
                    movementId, MovementStatus.STOPPED,
                    currentNode.getNodeId());

            movementRepository.saveLog(
                    movementId, "STOPPED",
                    currentNode.getNodeId(),
                    "Aircraft movement stopped.");

            movementStatuses.put(flightId, MovementStatus.STOPPED);

            System.out.println(
                    "Flight " + flightId + " stopped.");

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Could not stop movement in database.", e);
        }
    }

    // Get current aircraft location
    public TaxiwayNode getCurrentNode(int flightId) {

        checkFlightExists(flightId);

        int position = currentPositions.get(flightId);

        return flightRoutes.get(flightId).get(position);
    }

    // Get movement status
    public MovementStatus getMovementStatus(int flightId) {

        checkFlightExists(flightId);

        return movementStatuses.get(flightId);
    }

    // Get planned route
    public List<TaxiwayNode> getFlightRoute(int flightId) {

        checkFlightExists(flightId);

        return Collections.unmodifiableList(
                flightRoutes.get(flightId));
    }

    // Validate flight movement
    private void checkFlightExists(int flightId) {

        if (!flightRoutes.containsKey(flightId)) {
            throw new IllegalArgumentException(
                    "No movement found for flight: " + flightId);
        }
    }
}
