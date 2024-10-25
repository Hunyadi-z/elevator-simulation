package com.elevatorsimulation;

import java.time.Instant;

/**
 * Request is a class used to represent a button press for scheduling an
 * elevator. Button presses on the elevator create requests that the elevator
 * scheduler will work to satisfy. The request represents the intent that a user
 * has when pressing a button, such as a floor they wish to go to, or
 * summoning an elevator so they can go up or down. The Request class contains
 * the information for floor requests and whether an up or down button was
 * pressed. It also has a timestamp for creation to track how long a request has
 * been around before it is satisfied.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class Request {

    private final int floorNumber;
    private final Direction desiredDirection;
    private final Instant timeCreated;

    /**
     * Class constructor specifying a requested floor. This represents an internal
     * button press that would have no direction, just a floor.
     *
     * @param floorNumber the requested floor to go to
     */
    public Request(int floorNumber) {
        this.floorNumber = floorNumber;
        desiredDirection = Direction.NONE;
        timeCreated = Instant.now();
    }

    /**
     * Class constructor specifying a requested floor and a requested direction.
     * This represents an external button press that would have an originating floor
     * and a desired direction.
     *
     * @param floorNumber      the originating floor for the external button press
     * @param desiredDirection the requested direction for the external button press
     */
    public Request(int floorNumber, Direction desiredDirection) {
        this.floorNumber = floorNumber;
        this.desiredDirection = desiredDirection;
        timeCreated = Instant.now();
    }

    /**
     * Returns the requested floor.
     *
     * @return the floor number that goes with the request
     */
    public final int getFloorNumber() {
        return floorNumber;
    }

    /**
     * Returns the desired direction for the request. NONE is a valid option and is
     * used for interal button presses, whereas an external button press should have
     * UP or DOWN.
     *
     * @return the direction that goes with the button press. Returns NONE if no
     *         direction was provided.
     */
    public final Direction getDesiredDirection() {
        return desiredDirection;
    }

    /**
     * Returns the time the request was created. The time is used to calculate
     * statistics for the life of a request.
     *
     * @return the time that the request was created
     */
    public final Instant getTimeCreated() {
        return timeCreated;
    }

    /**
     * Returns a human readable string that represents the informative contents of
     * this request. The representation is subject to change, but the
     * following may be regarded as typical:
     * 
     * "Request{floorNumber=1, desiredDirection=NONE, timeCreated=500}"
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Request{" +
                "floorNumber=" + floorNumber +
                ", desiredDirection=" + desiredDirection +
                ", timeCreated=" + timeCreated +
                '}';
    }
}