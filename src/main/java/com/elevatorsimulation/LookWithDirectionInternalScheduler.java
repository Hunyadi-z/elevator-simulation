package com.elevatorsimulation;

import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * LookWithDirectionInternalScheduler is a class that implements the
 * InternalScheduler interface. It is a scheduling algorithm used to determine
 * which floor the elevator should move to next. The algorithm is based on the
 * LOOK algorithm which is a hard disk scheduling algorithm used to determine
 * the order of read and write requests. At a high level, the algorithm will
 * satisfy all requests in one direction of travel first, and then process all
 * requests in the other direction, repeating this pattern forever. The
 * algorithm has been modified to add an additional check for a desired
 * direction, where if the desired direction is up for example, then it will
 * only be processed when the elevator is going up. Two queues are maintained
 * and are ordered in the direction of travel, so for going up the floors are in
 * ascending order and for going down the floors are in descending order.
 * Requests that cannot be satisfied for the current direction (would require
 * backtracking) are placed in pending queues, that are then copied over when
 * the direction of travel once again returns to the desired direction.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public class LookWithDirectionInternalScheduler implements InternalScheduler {

    private final PriorityQueue<Request> upJobs;
    private final PriorityQueue<Request> downJobs;

    private final PriorityQueue<Request> pendingUpJobs;
    private final PriorityQueue<Request> pendingDownJobs;

    private final Elevator elevator;
    private Direction schedulingDirection;

    /**
     * Class constructor specifying the elevator that is being scheduled.
     *
     * @param elevator the elevator that is being scheduled
     */
    public LookWithDirectionInternalScheduler(Elevator elevator) {
        upJobs = new PriorityQueue<Request>(
                Comparator.comparingInt(Request::getFloorNumber));
        downJobs = new PriorityQueue<Request>(
                Comparator.comparingInt(Request::getFloorNumber).reversed());

        pendingUpJobs = new PriorityQueue<Request>(
                Comparator.comparingInt(Request::getFloorNumber));
        pendingDownJobs = new PriorityQueue<Request>(
                Comparator.comparingInt(Request::getFloorNumber).reversed());

        this.elevator = elevator;
        schedulingDirection = Direction.NONE;
    }

    /**
     * Returns the current scheduling direction of the scheduler. This specifies
     * which queue is being pulled from currently.
     *
     * @return the current scheduling direction
     */
    public synchronized final Direction getSchedulingDirection() {
        return schedulingDirection;
    }

    /**
     * Adds a request to the scheduler. This is intended for the elevator to take
     * button presses and then tell the scheduler there are new requests. A
     * determination is made for which queue the request will go into. The
     * scheduling direction will be determined in this method if there is no current
     * direction.
     *
     * @param request a new request to be scheduled
     */
    public synchronized void addRequest(Request request) {
        switch (request.getDesiredDirection()) {
            case UP:
                if (request.getFloorNumber() >= elevator.getCurrentFloor() ||
                        schedulingDirection != Direction.UP) {
                    upJobs.add(request);
                } else {
                    pendingUpJobs.add(request);
                }

                if (schedulingDirection == Direction.NONE) {
                    schedulingDirection = Direction.UP;
                }
                break;

            case DOWN:
                if (request.getFloorNumber() <= elevator.getCurrentFloor() ||
                        schedulingDirection != Direction.DOWN) {
                    downJobs.add(request);
                } else {
                    pendingDownJobs.add(request);
                }

                if (schedulingDirection == Direction.NONE) {
                    schedulingDirection = Direction.DOWN;
                }
                break;

            default:
                if (request.getFloorNumber() >= elevator.getCurrentFloor()) {
                    upJobs.add(request);
                    if (schedulingDirection == Direction.NONE) {
                        schedulingDirection = Direction.UP;
                    }
                } else {
                    downJobs.add(request);
                    if (schedulingDirection == Direction.NONE) {
                        schedulingDirection = Direction.DOWN;
                    }
                }
                break;
        }
    }

    /**
     * Removes the specified request from the scheduler. This is intended to happen
     * after a request has been satisfied. The elevator provides the specific
     * request that was just satisfied. If there are no more requests in the current
     * direction, then a direction switch will occur and pending requests will get
     * added to the main queues at this time. If there are no requests at all then
     * the scheduling direction will be set to NONE.
     * 
     * @param request the request that was satified that will be removed from the
     *                scheduler
     */
    public synchronized void removeRequest(Request request) {
        if (schedulingDirection == Direction.UP) {
            upJobs.remove(request);

            if (upJobs.isEmpty()) {
                // Copy pending over
                upJobs.addAll(pendingUpJobs);
                pendingUpJobs.clear();

                if (!downJobs.isEmpty()) {
                    schedulingDirection = Direction.DOWN;
                } else if (upJobs.isEmpty()) {
                    schedulingDirection = Direction.NONE;
                }
            }
        } else {
            downJobs.remove(request);

            if (downJobs.isEmpty()) {
                // Copy pending over
                downJobs.addAll(pendingDownJobs);
                pendingDownJobs.clear();

                if (!upJobs.isEmpty()) {
                    schedulingDirection = Direction.UP;
                } else if (downJobs.isEmpty()) {
                    schedulingDirection = Direction.NONE;
                }
            }
        }
    }

    /**
     * Retrieves the current highest priority request in the eyes of the scheduler.
     * This is the request that the elevator will satisfy next. The scheduling
     * direction will determine which queue to pull from.
     *
     * @returns the current highest priority request
     */
    public synchronized Request getCurrentRequest() {
        if (schedulingDirection == Direction.UP) {
            return upJobs.peek();
        } else {
            return downJobs.peek();
        }
    }

    /**
     * Returns whether the scheduler has any requests in its queues. There is no
     * time when the pending queues could have requests while the primary queues do
     * not, so only the primary queues are checked for requests.
     *
     * @returns whether the scheduler has any requests that still need to be
     *          processed
     */
    public synchronized boolean hasRequests() {
        return !upJobs.isEmpty() || !downJobs.isEmpty();
    }
}
