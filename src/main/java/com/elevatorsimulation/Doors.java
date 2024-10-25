package com.elevatorsimulation;

/**
 * Doors is a class for simulating the current state and operations of the doors
 * of an elevator. The doors can open and they can close.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class Doors {
    private enum DoorState {
        OPEN,
        CLOSED
    }

    private DoorState doorState;

    /**
     * Class constructor.
     */
    public Doors() {
        doorState = DoorState.CLOSED;
    }

    /**
     * Opens the elevator doors.
     */
    public void open() {
        doorState = DoorState.OPEN;
        System.out.println("[DOORS] Opening doors.");
    }

    /**
     * Closes the elevator doors.
     */
    public void close() {
        doorState = DoorState.CLOSED;
        System.out.println("[DOORS] Closing doors.");
    }

    /**
     * Returns whether the elevator doors are closed.
     *
     * @return whether the elevator doors are closed or not
     */
    public boolean areClosed() {
        return (doorState == DoorState.CLOSED);
    }

    /**
     * Returns a human readable string that represents the informative contents of
     * the elevator doors. The representation is subject to change, but the
     * following may be regarded as typical:
     * 
     * "Doors{doorState=CLOSED}"
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Doors{" +
                "doorState=" + doorState +
                '}';
    }
}
