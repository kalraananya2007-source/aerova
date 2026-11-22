package com.aerova.gate;

public class Gate {

    private int gateId;
    private String gateCode;
    private String gateSize;
    private GateStatus status;
    private int assignedFlightId;

    public Gate(int gateId, String gateCode, String gateSize) {
        this.gateId = gateId;
        this.gateCode = gateCode;
        this.gateSize = gateSize;
        this.status = GateStatus.AVAILABLE;
        this.assignedFlightId = -1;
    }

    public int getGateId() {
        return gateId;
    }

    public String getGateCode() {
        return gateCode;
    }

    public String getGateSize() {
        return gateSize;
    }

    public GateStatus getStatus() {
        return status;
    }

    public int getAssignedFlightId() {
        return assignedFlightId;
    }

    public boolean isAvailable() {
        return status == GateStatus.AVAILABLE;
    }

    public void setStatus(GateStatus status) {
        this.status = status;
    }

    public void setAssignedFlightId(int assignedFlightId) {
        this.assignedFlightId = assignedFlightId;
    }
}