package com.elevatorsimulation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class LookWithDirectionInternalSchedulerTest {
    private LookWithDirectionInternalScheduler scheduler;
    private Elevator elevator;

    @Test
    void testHasRequests() {
        elevator = new Elevator(1, 0);
        scheduler = new LookWithDirectionInternalScheduler(elevator);

        assertEquals(false, scheduler.hasRequests());

        Request myRequest = new Request(1);
        scheduler.addRequest(myRequest);
        assertEquals(true, scheduler.hasRequests());
    }

    public static Stream<Arguments> testSchedulingDirection() {
        return Stream.of(
                Arguments.of(0, 4, Direction.NONE, Direction.UP),
                Arguments.of(0, -2, Direction.UP, Direction.UP),
                Arguments.of(0, 0, Direction.UP, Direction.UP),
                Arguments.of(5, 10, Direction.NONE, Direction.UP),
                Arguments.of(5, 10, Direction.UP, Direction.UP),
                Arguments.of(0, -2, Direction.NONE, Direction.DOWN),
                Arguments.of(0, 3, Direction.DOWN, Direction.DOWN),
                Arguments.of(0, 0, Direction.DOWN, Direction.DOWN),
                Arguments.of(10, 5, Direction.NONE, Direction.DOWN),
                Arguments.of(10, 5, Direction.DOWN, Direction.DOWN),
                Arguments.of(0, 0, Direction.NONE, Direction.UP));
    }

    @ParameterizedTest
    @MethodSource
    void testSchedulingDirection(int startingFloor, int requestedFloor, Direction direction,
            Direction expectedDirection) {
        elevator = new Elevator(1, startingFloor);
        scheduler = new LookWithDirectionInternalScheduler(elevator);

        assertEquals(Direction.NONE, scheduler.getSchedulingDirection());

        Request myRequest = new Request(requestedFloor, direction);
        scheduler.addRequest(myRequest);
        assertEquals(expectedDirection, scheduler.getSchedulingDirection());

        myRequest = scheduler.getCurrentRequest();
        scheduler.removeRequest(myRequest);
        assertEquals(Direction.NONE, scheduler.getSchedulingDirection());
    }

    public static Stream<Arguments> testFloorScheduling() {
        return Stream.of(
                Arguments.of(10, new int[] { 5, 7, 3, 1, 2, 9 },
                        new Direction[] { Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                                Direction.NONE, Direction.NONE },
                        new int[] { 9, 7, 5, 3, 2, 1 }), // Going Down
                Arguments.of(0, new int[] { 5, 7, 3, 1, 2, 9 },
                        new Direction[] { Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                                Direction.NONE, Direction.NONE },
                        new int[] { 1, 2, 3, 5, 7, 9 }), // Going Up
                Arguments.of(5, new int[] { 6, 7, 3, 1, -2, 9 },
                        new Direction[] { Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                                Direction.NONE, Direction.NONE },
                        new int[] { 6, 7, 9, 3, 1, -2 }), // Going Up then Down
                Arguments.of(5, new int[] { 3, 5, 7, -1, 2, 9 },
                        new Direction[] { Direction.NONE, Direction.NONE, Direction.NONE, Direction.NONE,
                                Direction.NONE, Direction.NONE },
                        new int[] { 3, 2, -1, 5, 7, 9 }), // Going Down then Up
                Arguments.of(10, new int[] { 5, 7, 3, 1, 2, 9 },
                        new Direction[] { Direction.DOWN, Direction.DOWN, Direction.DOWN, Direction.DOWN,
                                Direction.DOWN, Direction.DOWN },
                        new int[] { 9, 7, 5, 3, 2, 1 }), // Going Down with Direction
                Arguments.of(0, new int[] { 5, 7, 3, 1, 2, 9 },
                        new Direction[] { Direction.UP, Direction.UP, Direction.UP, Direction.UP,
                                Direction.UP, Direction.UP },
                        new int[] { 1, 2, 3, 5, 7, 9 })); // Going Up with Direction
    }

    @ParameterizedTest
    @MethodSource
    void testFloorScheduling(int startingFloor, int[] floorsToVisit, Direction[] requestedDirections,
            int[] expectedOrder) {
        elevator = new Elevator(1, startingFloor);
        scheduler = new LookWithDirectionInternalScheduler(elevator);

        for (int i = 0; i < floorsToVisit.length; i++) {
            Request request = new Request(floorsToVisit[i], requestedDirections[i]);
            scheduler.addRequest(request);
        }

        for (int expectedFloor : expectedOrder) {
            Request request = scheduler.getCurrentRequest();
            assertEquals(expectedFloor, request.getFloorNumber());
            scheduler.removeRequest(request);
        }
    }

    @AfterEach
    void tearDown() {
        scheduler = null;
        elevator = null;
    }

}
