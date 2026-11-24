
package com.aerova.taxiway;

import java.util.*;

public class RouteFinder {

    private final TaxiwayNetwork network;

    public RouteFinder(TaxiwayNetwork network) {

        if (network == null) {
            throw new IllegalArgumentException(
                    "Taxiway network cannot be null.");
        }

        this.network = network;
    }

    // Find shortest route using Dijkstra's algorithm
    public List<TaxiwayNode> findShortestRoute(
            String sourceId, String destinationId) {

        TaxiwayNode source = network.getNode(sourceId);
        TaxiwayNode destination = network.getNode(destinationId);

        if (source == null || destination == null) {
            throw new IllegalArgumentException(
                    "Source or destination node does not exist.");
        }

        if (sourceId.equals(destinationId)) {
            return Collections.singletonList(source);
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> previous = new HashMap<>();

        PriorityQueue<String> queue =
                new PriorityQueue<>(
                        Comparator.comparingDouble(distances::get));

        // Initialize distances
        for (TaxiwayNode node : network.getAllNodes()) {
            distances.put(node.getNodeId(), Double.MAX_VALUE);
        }

        distances.put(sourceId, 0.0);
        queue.add(sourceId);

        while (!queue.isEmpty()) {

            String currentId = queue.poll();

            if (currentId.equals(destinationId)) {
                break;
            }

            double currentDistance = distances.get(currentId);

            for (TaxiwaySegment segment :
                    network.getConnectedSegments(currentId)) {

                // Ignore unavailable taxiways
                if (!segment.isAvailable()) {
                    continue;
                }

                String neighbourId;

                if (segment.getStartNode().getNodeId()
                        .equals(currentId)) {

                    neighbourId =
                            segment.getEndNode().getNodeId();

                } else {

                    neighbourId =
                            segment.getStartNode().getNodeId();
                }

                double newDistance =
                        currentDistance + segment.getDistance();

                if (newDistance < distances.get(neighbourId)) {

                    distances.put(neighbourId, newDistance);
                    previous.put(neighbourId, currentId);

                    queue.remove(neighbourId);
                    queue.add(neighbourId);
                }
            }
        }

        // Check whether destination is reachable
        if (distances.get(destinationId) == Double.MAX_VALUE) {
            return Collections.emptyList();
        }

        // Reconstruct shortest route
        LinkedList<TaxiwayNode> path = new LinkedList<>();

        String currentId = destinationId;

        while (currentId != null) {
            path.addFirst(network.getNode(currentId));
            currentId = previous.get(currentId);
        }

        return path;
    }

    // Calculate total distance of a route
    public double calculateRouteDistance(
            List<TaxiwayNode> route) {

        if (route == null || route.size() < 2) {
            return 0;
        }

        double totalDistance = 0;

        for (int i = 0; i < route.size() - 1; i++) {

            String currentId = route.get(i).getNodeId();
            String nextId = route.get(i + 1).getNodeId();

            boolean found = false;

            for (TaxiwaySegment segment :
                    network.getConnectedSegments(currentId)) {

                String startId =
                        segment.getStartNode().getNodeId();

                String endId =
                        segment.getEndNode().getNodeId();

                boolean matches =
                        (startId.equals(currentId)
                                && endId.equals(nextId))
                                ||
                                (startId.equals(nextId)
                                        && endId.equals(currentId));

                if (matches) {
                    totalDistance += segment.getDistance();
                    found = true;
                    break;
                }
            }

            if (!found) {
                throw new IllegalArgumentException(
                        "Route contains disconnected nodes.");
            }
        }

        return totalDistance;
    }
}
