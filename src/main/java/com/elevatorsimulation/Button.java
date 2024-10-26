package com.elevatorsimulation;

import java.util.Objects;

/**
 * Button is a class for representing an elevator button that is used to direct
 * the elevator to a specified floor. The class represents both floor buttons
 * inside the elevator and buttons external to the elevator that are used to
 * summon an elevator to a floor for pickup.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class Button {
    public final int floor;
    public final Direction direction;

    /**
     * Class constructor specifying the floor and the direction (NONE is allowed)
     * that goes with the button.
     *
     * @param floor     the floor that corresponds to the button
     * @param direction the direction that corresponds to the button.
     *                  Setting to NONE will represent an internal button. Anything
     *                  other than NONE will represent an external button.
     */
    public Button(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
    }

    @Override
    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Button))
            return false;
        Button button = (Button) o;
        return (floor == button.floor) &&
                (direction == button.direction);
    }

    @Override
    public int hashCode() {
        return Objects.hash(floor, direction);
    }

    /**
     * Returns a human readable string that represents the informative contents of
     * this button. The representation is subject to change, but the
     * following may be regarded as typical:
     * 
     * "Button{floor=1, direction=NONE}"
     *
     * @return a string representation of the object
     */
    @Override
    public String toString() {
        return "Button{" +
                "floor=" + floor +
                ", direction=" + direction +
                '}';
    }
}
