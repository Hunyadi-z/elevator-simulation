package com.elevatorsimulation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

/**
 * ButtonPresser is a class for simulating a person pressing buttons for an
 * elevator. A ButtonPresser object reads in ButtonPressEvents from the
 * specified csv file. Currently the csv file is:
 * "src/main/resources/buttonPresser.csv"
 * <p>
 * ButtonPresser implements the Runnable interface so it can be executed with a
 * Thread. Once started with a Thread, the object will loop through all
 * ButtonPressEvents that were read in and execute them sequentially, sleeping
 * in between events using the delay time for each ButtonPressEvent.
 * <p>
 * A ButtonPresser object acts on a single Elevator. Multiple ButtonPresser
 * objects are required to simulate multiple elevators.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class ButtonPresser implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(ButtonPresser.class.getName());
    private final List<ButtonPressEvent> buttonPressEvents;
    private final Elevator myElevator;

    private class ButtonPressEvent {
        private final int floor;
        private final Direction direction;
        private final long delayBeforePressing; // milliseconds

        /**
         * Class constructor specifying a requested floor, a requested direction (if
         * any), and how long to wait after the previous event to execute the button
         * press.
         *
         * @param id                  an identifier to distinguish the elevator if there
         *                            are multiple ones
         * @param floor               the requested floor for the button press
         * @param direction           the requested direction for the button press.
         *                            Setting to NONE will represent an internal button
         *                            press. Anything other than NONE will represent an
         *                            external button press.
         * @param delayBeforePressing the amount of time (in milliseconds) to wait after
         *                            the previous event before executing the button
         *                            press
         */
        public ButtonPressEvent(int floor, Direction direction, long delayBeforePressing) {
            this.floor = floor;
            this.direction = direction;
            this.delayBeforePressing = delayBeforePressing;
        }

        /**
         * Returns the requested floor for the button press.
         *
         * @return the floor number that goes with the button press
         */
        public final int getFloor() {
            return floor;
        }

        /**
         * Returns the requested direction for the button press.
         *
         * @return the direction that goes with the button press. Returns NONE if no
         *         direction was provided.
         */
        public final Direction getDirection() {
            return direction;
        }

        /**
         * Returns the delay time (in milliseconds) to wait after the previous event
         * before the event executes.
         *
         * @return the time to wait (in milliseconds) after the previous event before
         *         executing the button press
         */
        public final Long getDelayBeforeProcessing() {
            return delayBeforePressing;
        }

        /**
         * Returns a human readable string that represents the informative contents of
         * this button press event. The representation is subject to change, but the
         * following may be regarded as typical:
         * 
         * "ButtonPressEvent{floor=1, direction=NONE, delayBeforePressing=500}"
         *
         * @return a string representation of the object
         */
        @Override
        public String toString() {
            return "ButtonPressEvent{" +
                    "floor=" + floor +
                    ", direction=" + direction +
                    ", delayBeforePressing=" + delayBeforePressing +
                    '}';
        }
    }

    /**
     * Class constructor specifying the elevator that will have button press events
     * executed on it.
     *
     * @param elevator the elevator that will have button presses simulated on it
     */
    public ButtonPresser(Elevator elevator) {
        myElevator = elevator;
        buttonPressEvents = new ArrayList<>();
        readInEvents();
    }

    /**
     * Defines the code that will be executed when a thread starts. Loops through
     * all ButtonPressEvents that were read in from the csv file and sequentially
     * executes them using the delay time to sleep between events.
     */
    @Override
    public void run() {
        try {
            for (ButtonPressEvent buttonPress : buttonPressEvents) {
                Thread.sleep(buttonPress.getDelayBeforeProcessing());

                if (buttonPress.getDirection() == Direction.NONE) {
                    myElevator.pressInternalButton(buttonPress.getFloor());
                } else {
                    myElevator.pressExternalButton(buttonPress.getFloor(), buttonPress.getDirection());
                }
            }
        } catch (InterruptedException e) {
            LOGGER.info("Interrupted ButtonPresser");
        }
    }

    private void readInEvents() {
        String csvFile = "src/main/resources/buttonPresser.csv";

        LOGGER.info("Reading in file '" + csvFile + "'...");

        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");

                if (values.length == 3) {
                    ButtonPressEvent myEvent = new ButtonPressEvent(Integer.parseInt(values[0].trim()),
                            Direction.valueOf(values[1].trim()), Long.parseLong(values[2].trim()));
                    buttonPressEvents.add(myEvent);
                } else {
                    LOGGER.severe("Wrong number of fields found! Expected 3 but got " +
                            values.length);
                }
            }
        } catch (FileNotFoundException e) {
            LOGGER.severe("Could not open file '" + csvFile + "'! ");
            e.printStackTrace();
        } catch (IOException e) {
            LOGGER.severe("Error reading '" + csvFile + "'!");
            e.printStackTrace();
        }
        LOGGER.info("Done reading file.");
    }
}
