
package com.aerova.taxiway;

import java.util.*;

public class TaxiwayNetwork {

    private final Map<String, TaxiwayNode> nodes;
    private final Map<String, TaxiwaySegment> segments;
    private final Map<String, List<TaxiwaySegment>> adjacencyList;

    public TaxiwayNetwork() {
        nodes = new LinkedHashMap<>();
        segments = new LinkedHashMap<>();
        adjacencyList = new LinkedHashMap<>();
    }

    // Add a node
    public void addNode(TaxiwayNode node) {

        if (node == null) {
            throw new IllegalArgumentException("Node cannot be null.");
        }

        String nodeId = node.getNodeId();

        if (nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException(
                    "Node already exists: " + nodeId);
        }

        nodes.put(nodeId, node);
        adjacencyList.put(nodeId, new ArrayList<>());
    }

    // Add a taxiway segment
    public void addSegment(TaxiwaySegment segment) {

        if (segment == null) {
            throw new IllegalArgumentException(
                    "Segment cannot be null.");
        }

        String segmentId = segment.getSegmentId();

        if (segments.containsKey(segmentId)) {
            throw new IllegalArgumentException(
                    "Segment already exists: " + segmentId);
        }

        String startId = segment.getStartNode().getNodeId();
        String endId = segment.getEndNode().getNodeId();

        if (!nodes.containsKey(startId) ||
                !nodes.containsKey(endId)) {

            throw new IllegalArgumentException(
                    "Both nodes must exist in the network.");
        }

        segments.put(segmentId, segment);

        adjacencyList.get(startId).add(segment);
        adjacencyList.get(endId).add(segment);
    }

    // Load complete taxiway network from MySQL
    public void loadNetworkFromDatabase() throws java.sql.SQLException {

        TaxiwayNodeDAO nodeDAO = new TaxiwayNodeDAO();
        TaxiwaySegmentDAO segmentDAO = new TaxiwaySegmentDAO();

        List<TaxiwayNode> databaseNodes = nodeDAO.getAllNodes();
        List<TaxiwaySegment> databaseSegments = segmentDAO.getAllSegments();

        // Prevent duplicate entries when loading
        nodes.clear();
        segments.clear();
        adjacencyList.clear();

        // Load nodes first
        for (TaxiwayNode node : databaseNodes) {
            addNode(node);
        }

        // Load segments after nodes
        for (TaxiwaySegment segment : databaseSegments) {
            addSegment(segment);
        }

        System.out.println("Taxiway network loaded successfully!");
        System.out.println("Total nodes: " + getNodeCount());
        System.out.println("Total segments: " + getSegmentCount());
    }

    // Get node by ID
    public TaxiwayNode getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    // Get segment by ID
    public TaxiwaySegment getSegment(String segmentId) {
        return segments.get(segmentId);
    }

    // Get all nodes
    public Collection<TaxiwayNode> getAllNodes() {
        return Collections.unmodifiableCollection(nodes.values());
    }

    // Get all segments
    public Collection<TaxiwaySegment> getAllSegments() {
        return Collections.unmodifiableCollection(segments.values());
    }

    // Get neighbouring nodes
    public List<TaxiwayNode> getNeighbours(String nodeId) {

        if (!nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException(
                    "Node not found: " + nodeId);
        }

        List<TaxiwayNode> neighbours = new ArrayList<>();

        for (TaxiwaySegment segment : adjacencyList.get(nodeId)) {

            String neighbourId;

            if (segment.getStartNode().getNodeId().equals(nodeId)) {
                neighbourId = segment.getEndNode().getNodeId();
            } else {
                neighbourId = segment.getStartNode().getNodeId();
            }

            neighbours.add(nodes.get(neighbourId));
        }

        return Collections.unmodifiableList(neighbours);
    }

    // Get segments connected to a node
    public List<TaxiwaySegment> getConnectedSegments(String nodeId) {

        if (!nodes.containsKey(nodeId)) {
            throw new IllegalArgumentException(
                    "Node not found: " + nodeId);
        }

        return Collections.unmodifiableList(
                adjacencyList.get(nodeId));
    }

    // Get total node count
    public int getNodeCount() {
        return nodes.size();
    }

    // Get total segment count
    public int getSegmentCount() {
        return segments.size();
    }
}
