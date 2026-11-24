
package com.aerova.taxiway;

public class AircraftSimulation implements Runnable {

    private final int flightId;
    private final AircraftMovementManager movementManager;

    public AircraftSimulation(int flightId,
                              AircraftMovementManager movementManager) {
        this.flightId = flightId;
        this.movementManager = movementManager;
    }

    @Override
    public void run() {
        try {
            System.out.println("Simulation started for Flight " + flightId);

            movementManager.startMovement(flightId);

            while (movementManager.getMovementStatus(flightId)
                    == MovementStatus.MOVING) {

                Thread.sleep(2000);

                movementManager.moveToNextNode(flightId);
            }

            System.out.println("Simulation finished for Flight " + flightId);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Simulation interrupted for Flight " + flightId);

        } catch (RuntimeException e) {
            System.out.println("Simulation error: " + e.getMessage());
        }
    }
}
