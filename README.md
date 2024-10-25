# Elevator Simulation

## Description

This is an elevator simulation that can be used to test out different scheduling algorithms for how an elevator chooses the order in which to travel to different floors. This application simulates floor buttons internal to an elevator as well as up/down buttons external to an elevator that a person would use to summon an elevator. Currently only one elevator is simulated at a time.

## Assumptions

- Only a single elevator will be simulated, not multiple simultaneous elevators.
- External buttons will consist of both an UP and a DOWN button on each floor.
- There is no express elevator or special priority for any floor.
- Only buttons for movement will be simulated. No other buttons will be provided (Open Door/Close Doors, Call For Emergency, etc.)
- Door does not check for obstacles.
- No GUI will be provided.
- The desired output information is the length of time it takes to service requests.
- The focus is on the scheduling algorithm of an individual elevator, rather than traffic flow of passengers throughout a building.
- An elevator does not need to know passenger information, an elevator only needs to know if the weight limit has been exceeded.

## Installation and Requirements

This program requires that Java is installed. See [here](https://www.java.com/en/download/help/download_options.html) for installation instructions.

This program requires that Maven is installed. See [here](https://maven.apache.org/download.cgi) for installation instructions.

1. Clone the repo using the following command:

   ```
   git clone https://github.com/Hunyadi-z/elevator-simulation.git
   ```

2. In the repository directory run this Maven command to build the package:

   ```
   mvn package
   ```

3. Execute the program with:

   ```
   java -cp target/elevatorsimulation-1.0-SNAPSHOT.jar com.elevatorsimulation.Main
   ```

   Alternatively, the program can be compiled and run with:

   ```
   mvn compile
   mvn exec:java -Dexec.mainClass="com.elevatorsimulation.Main"
   ```

## Usage

The button presses

Control-C should stop the program and still show the statistics for the requests up to that point in time.

## Scheduling Algorithm

The algorithm that this simulation is currently using is based on the [LOOK algorithm](https://en.wikipedia.org/wiki/LOOK_algorithm) which is a hard disk scheduling algorithm. In summary, the algorithm will satisfy all requests in one direction of travel first, and then process all requests in the other direction, repeating this pattern forever. The algorithm has been modified to add an additional check for a desired direction, where if the desired direction is up for example, then it will only be processed when the elevator is going up. Two queues are maintained and are ordered in the direction of travel, so for going up the floors are in ascending order and for going down the floors are in descending order. Requests that cannot be satisfied for the current direction (would require backtracking) are placed in pending queues, that are then copied over when the direction of travel once again returns to the desired direction.

## Future Features

- [ ] Add the ability to run with multiple elevators by adding an external controller to handle the tasking of external button presses.
- [ ] Make it so an elevator can have a home floor that it returns to when idle.
- [ ] Add priority to certain floors (eg. penthouse).
- [ ] Read in elevator configuration parameters from a file. Fields such as starting floor, time it takes to travel between floors, how long to wait for passengers to load.
- [ ] Add maximum weight checks, but also a means of simulating weight in the elevator.
- [ ] Simulate failure of the doors.
- [ ] Add additional buttons that are not floor related, such as open/close doors, call for emergency.
