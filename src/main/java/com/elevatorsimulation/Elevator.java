package com.elevatorsimulation;

import java.util.logging.*;
import java.util.HashSet;

/**
 * This class simulates an elevator. The Elevator can be kicked off in a thread
 * and it will then loop continuously waiting for requests and processing
 * existing requests.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class Elevator implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Elevator.class.getName());
    private volatile boolean running = true;

    private final int timeToWaitBeforeCheckingForRequestsInMs = 500;
    private final int timeToTravelBetweenFloorsInMs = 500;
    private final int timeToWaitForPassengersInMs = 500;
    private final int id;
    private int currentFloor;
    private boolean isMoving;
    private HashSet<Button> pressedButtons;
    private InternalScheduler internalScheduler;
    private Doors doors;
    private StatsCollector statsCollector;

    /**
     * Class constructor specifying an id number and the starting floor for
     * the elevator.
     * 
     * @param id           an identifier to distinguish the elevator if there are
     *                     multiple ones
     * @param currentFloor the floor the elevator will start on
     */
    public Elevator(int id, int currentFloor) {
        this.id = id;
        this.currentFloor = currentFloor;
        isMoving = false;
        internalScheduler = new LookWithDirectionInternalScheduler(this);
        pressedButtons = new HashSet<Button>();
        doors = new Doors();
        statsCollector = new StatsCollector();
    }

    /**
     * Returns the identifier for this elevator, so that multiple elevators can be
     * distinguished.
     *
     * @return the identifier of the elevator
     */
    public final int getId() {
        return id;
    }

    /**
     * Returns the current floor that the elevator is on.
     *
     * @return the current floor location of the elevator
     */
    public final synchronized int getCurrentFloor() {
        return currentFloor;
    }

    /**
     * Returns a human readable string that represents the informative statistics of
     * all requests that have been processed.
     *
     * @return a string providing a summary of the statistics for the processed
     *         requests
     */
    public String getStats() {
        return statsCollector.toString();
    }

    /**
     * Returns a human readable string that represents the informative contents of
     * this elevator. The representation is subject to change, but the
     * following may be regarded as typical:
     * 
     * "Elevator{id=1, currentFloor=1, isMoving=false,
     * doors=Doors{doorState=CLOSED}, timeToWaitBeforeCheckingForRequestsInMs=500,
     * timeToTravelBetweenFloorsInMs=500, timeToWaitForPassengersInMs=500}"
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Elevator{" +
                "id=" + id +
                ", currentFloor=" + currentFloor +
                ", isMoving=" + isMoving +
                ", doors=" + doors +
                ", timeToWaitBeforeCheckingForRequestsInMs=" + timeToWaitBeforeCheckingForRequestsInMs +
                ", timeToTravelBetweenFloorsInMs=" + timeToTravelBetweenFloorsInMs +
                ", timeToWaitForPassengersInMs=" + timeToWaitForPassengersInMs +
                '}';
    }

    /**
     * Defines the code that will be executed when a thread starts. Continuously
     * loops while checking to see if the scheduler has any requests. If requests
     * exists, then the next request is processed and the elevator will move towards
     * the desired floor. The elevator will sleep while waiting for new request,
     * before waking up and checking for new requests. If the elevator is told to
     * terminate, then the elevator will finish processing any remaining requests
     * and then will break out of the loop.
     */
    @Override
    public void run() {
        while (running || internalScheduler.hasRequests()) {
            if (internalScheduler.hasRequests()) {
                processNextRequest();
            } else {
                try {
                    Thread.sleep(timeToWaitBeforeCheckingForRequestsInMs);
                } catch (InterruptedException e) {
                    LOGGER.info("Elevator waiting for requests loop interrupted!");
                }
            }
        }
    }

    /**
     * Signals the elevator to finish processing any remaining requests and then
     * stop after that.
     */
    public void terminate() {
        running = false;
    }

    /**
     * Processes the pressing of an elevator button by making a new request for the
     * specified floor and direction and handing it off to the scheduler. A
     * direction of NONE represents an internal button press. Anything other than
     * NONE for direction represents an external button press. If the
     * button has already been pressed, then nothing will happen.
     *
     * @param button the button that was pressed which contains information for the
     *               requested floor number and the requested direction
     */
    public void pressElevatorButton(Button button) {
        System.out.println("[BUTTON_PRESS] Pressing button for floor: "
                + button.floor + ", direction: " + button.direction);

        if (buttonAlreadyPressed(button)) {
            System.out.println("[BUTTON_PRESS] Button for floor: "
                    + button.floor + ", direction: " + button.direction
                    + " is already pressed. Ignoring request.");
            return;
        }

        markButtonAsPressed(button);

        Request request = new Request(button.floor, button.direction);
        internalScheduler.addRequest(request);
    }

    private synchronized boolean buttonAlreadyPressed(Button button) {
        return pressedButtons.contains(button);
    }

    private synchronized void markButtonAsPressed(Button button) {
        pressedButtons.add(button);
    }

    private synchronized void clearButtonPress(int floorNumber, Direction direction) {
        Button button = new Button(floorNumber, direction);
        pressedButtons.remove(button);
    }

    private Direction calculateMovementDirection(int destinationFloor) {
        if (destinationFloor > currentFloor) {
            return Direction.UP;
        } else if (destinationFloor < currentFloor) {
            return Direction.DOWN;
        } else {
            return Direction.NONE;
        }
    }

    /**
     * Processes the top priority request as determined by the scheduler. The
     * elevator will ensure the doors are closed first and then it will then
     * move towards the destination floor of the request.
     * <p>
     * While moving, the elevator check at every floor to see if there is a new
     * higher priority request. If there is a new priority request, the destination
     * will be updated as the elevator continues to move. This cycle will continue
     * until the elevator arrives at its destination.
     */
    private void processNextRequest() {
        Request currentRequest = internalScheduler.getCurrentRequest();
        if (currentRequest == null) {
            LOGGER.warning("Expected a request to exist, but instead got null.");
            return;
        }

        int destinationFloor = currentRequest.getFloorNumber();

        System.out.println("[DESTINATION_CHANGE] Moving elevator in direction "
                + calculateMovementDirection(destinationFloor)
                + " to floor " + destinationFloor);

        while (!doors.areClosed()) {
            doors.close();
        }

        while (currentFloor != destinationFloor) {
            isMoving = true;
            travelOneFloorTowardsDestination(destinationFloor);

            // Check for a potential new job
            Request newCurrentRequest = internalScheduler.getCurrentRequest();
            if (newCurrentRequest != null && currentRequest != newCurrentRequest) {
                currentRequest = newCurrentRequest;
                destinationFloor = currentRequest.getFloorNumber();

                System.out.println(
                        "[DESTINATION_CHANGE] Updating elevator to move in direction "
                                + calculateMovementDirection(destinationFloor)
                                + " to floor " + destinationFloor);
            }
        }
        isMoving = false;

        arrivedAtDestination(currentRequest);
    }

    private void arrivedAtDestination(Request request) {
        if (request == null) {
            LOGGER.warning("Expected a request to exist, but instead got null.");
            return;
        }
        System.out.println("[ARRIVED] Destination reached. Floor: " + request.getFloorNumber());

        internalScheduler.removeRequest(request);

        statsCollector.addCompletedRequest(request);

        clearButtonPress(request.getFloorNumber(), request.getDesiredDirection());

        doors.open();
        waitForPassengers();
        while (!doors.areClosed()) {
            doors.close();
        }
    }

    private void waitForPassengers() {
        try {
            // Simulate the time to wait for passengers to load
            Thread.sleep(timeToWaitForPassengersInMs);
        } catch (InterruptedException e) {
            LOGGER.info("Waiting for passengers interrupted!");
        }
    }

    private void travelOneFloorTowardsDestination(int destinationFloor) {
        if (destinationFloor == currentFloor) {
            LOGGER.info("Destination floor (" + destinationFloor + ") and current floor ("
                    + currentFloor + ") are the same. Elevator will not move.");
            return;
        }

        try {
            // Simulate the time it takes to move between floors
            Thread.sleep(timeToTravelBetweenFloorsInMs);
        } catch (InterruptedException e) {
            LOGGER.info("Travel Interrupted!");
        }

        if (currentFloor < destinationFloor) {
            currentFloor++;
        } else {
            currentFloor--;
        }

        System.out.println("[MOVING] Current floor is now: " + currentFloor +
                ", destination floor is: " + destinationFloor);
    }
}
