package com.elevatorsimulation;

/**
 * InternalScheduler is an interface used by the Elevator class for determining
 * which floor the elevator should move to next. This scheduler is intended for
 * internally what a single elevator would be responsible for scheduling, as
 * opposed to an external controller that would be in charge of handing out
 * requests to multiple elevators that would then get scheduled internally for
 * each elevator.
 *
 * @author Michael Zigment
 * @version 1.0
 */
public interface InternalScheduler {
    /**
     * Adds a request to the scheduler. This is intended for the elevator to take
     * button presses and then tell the scheduler there are new requests.
     *
     * @param request a new request to be scheduled
     */
    public void addRequest(Request request);

    /**
     * Removes the specified request from the scheduler. This is intended to happen
     * after a request has been satisfied. The elevator provides the specific
     * request that was just satisfied.
     * 
     * @param request the request that was satified that will be removed from the
     *                scheduler
     */
    public void removeRequest(Request request);

    /**
     * Retrieves the current highest priority request in the eyes of the scheduler.
     * This is the request that the elevator will satisfy next.
     *
     * @returns the current highest priority request
     */
    public Request getCurrentRequest();

    /**
     * Returns whether the scheduler has any requests in its queue.
     *
     * @returns whether the scheduler has any requests that still need to be
     *          processed
     */
    public boolean hasRequests();
}
