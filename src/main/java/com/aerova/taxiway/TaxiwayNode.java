
package com.aerova.taxiway;

import java.util.Objects;

public class TaxiwayNode {

    private String nodeId;
    private String nodeName;

    public TaxiwayNode(String nodeId, String nodeName) {
        if (nodeId == null || nodeId.trim().isEmpty()) {
            throw new IllegalArgumentException("Node ID cannot be empty.");
        }

        if (nodeName == null || nodeName.trim().isEmpty()) {
            throw new IllegalArgumentException("Node name cannot be empty.");
        }

        this.nodeId = nodeId;
        this.nodeName = nodeName;
    }

    public String getNodeId() {
        return nodeId;
    }

    public String getNodeName() {
        return nodeName;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof TaxiwayNode)) {
            return false;
        }

        TaxiwayNode other = (TaxiwayNode) obj;
        return Objects.equals(nodeId, other.nodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeId);
    }

    @Override
    public String toString() {
        return nodeId + " - " + nodeName;
    }
}
