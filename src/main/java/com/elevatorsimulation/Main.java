package com.elevatorsimulation;

/**
 * The main class of the application. This is the class that gets executed in
 * order to run.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class Main {
    /**
     * The main method of the application. Creates an Elevator and a ButtonPresser
     * and runs them in separate threads. The method then waits for the
     * ButtonPresser to finish, at which time all the button presses will have
     * occurred. After that, the Elevator is given a signal to terminate which will
     * direct it to finish whatever is remaining in its queue. The method waits for
     * the elevator to finish all its remaining requests and then the scorecard
     * containing the elevator statistics is printed out.
     *
     * @param args any command line arguments (none currently)
     */
    public static void main(String[] args) {
        AsciiArt.printTitle();

        Elevator myElevator = new Elevator(1, 0);
        ButtonPresser myButtonPresser = new ButtonPresser(myElevator);
        Thread elevatorThread = new Thread(myElevator);
        Thread buttonPresserThread = new Thread(myButtonPresser);

        System.out.println("\nStarting Elevator Simulation...\n");

        elevatorThread.start();
        buttonPresserThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Shutting down...");
                myElevator.terminate();

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {

                }

                AsciiArt.printScorecard();
                System.out.println("Elevator " + myElevator.getId() + ":");
                System.out.println(myElevator.getStats() + "\n");
            }
        });

        try {
            buttonPresserThread.join();
        } catch (InterruptedException e) {
            System.out.println("ButtonPresserThread.join() interrupted.");
        }

        myElevator.terminate();

        try {
            elevatorThread.join();
        } catch (InterruptedException e) {
            System.out.println("ElevatorThread.join() interrupted.");
        }
    }
}