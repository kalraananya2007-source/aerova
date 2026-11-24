
package com.aerova.taxiway;

public class TaxiwaySegment {

    private String segmentId;
    private TaxiwayNode startNode;
    private TaxiwayNode endNode;
    private double distance;
    private boolean available;

    public TaxiwaySegment(String segmentId,
                          TaxiwayNode startNode,
                          TaxiwayNode endNode,
                          double distance) {

        if (segmentId == null || segmentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Segment ID cannot be empty.");
        }

        if (startNode == null || endNode == null) {
            throw new IllegalArgumentException("Nodes cannot be null.");
        }

        if (startNode.equals(endNode)) {
            throw new IllegalArgumentException("Start and end nodes must be different.");
        }

        if (distance <= 0 || Double.isNaN(distance) || Double.isInfinite(distance)) {
            throw new IllegalArgumentException("Distance must be positive and finite.");
        }

        this.segmentId = segmentId;
        this.startNode = startNode;
        this.endNode = endNode;
        this.distance = distance;
        this.available = true;
    }

    public String getSegmentId() {
        return segmentId;
    }

    public TaxiwayNode getStartNode() {
        return startNode;
    }

    public TaxiwayNode getEndNode() {
        return endNode;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return segmentId + ": " + startNode.getNodeId()
                + " -> " + endNode.getNodeId()
                + " (" + distance + " metres)";
    }
}
