package tms.intersection;

import tms.route.Route;
import tms.route.TrafficSignal;
import tms.util.TimedItem;
import tms.util.TimedItemManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a set of traffic lights at an intersection.
 *
 * For simplicity, traffic lights only allow one incoming route to be green at
 * any given time, with incoming traffic allowed to exit via any outbound route.
 */

public class IntersectionLights implements TimedItem {
    /** The list of routes connecting to this intersection */
    private List<Route> connections;
    /** The duration of time the traffic light will remain yellow*/
    private int yellowTime;
    /** The duration of time a traffic light will be yellow + green */
    private int duration;
    /** The real time passing of seconds in the TMS*/
    private int secondsPassed;
    /** The real time passing of seconds in the TMS while yellow*/
    private int yellowSecondsPassed;
    /** The current route having its traffic light signal changed*/
    private int currentRoute;

    /**
     * Creates a new set of traffic lights at an intersection.
     *
     * The first route in the given list of incoming routes should have its
     * TrafficLight signal set to TrafficSignal.GREEN.
     * @param connections a list of incoming routes, the list cannot be empty
     * @param yellowTime time in seconds for which lights will appear yellow
     * @param duration time in seconds for which lights will appear yellow and
     *                 green
     */
    public IntersectionLights(List<Route> connections, int yellowTime,
                              int duration) {

        this.connections = connections;
        this.yellowTime = yellowTime;
        this.duration = duration;
        this.secondsPassed = 0;
        this.yellowSecondsPassed = 0;
        this.currentRoute = 0;
        TimedItemManager.getTimedItemManager().registerTimedItem(this);
        connections.get(currentRoute).setSignal(TrafficSignal.GREEN);
    }

    /**
     * Returns the time in seconds for which a traffic light will appear yellow
     * when transitioning from green to red
     * @return yellow time in seconds for this set of traffic lights
     */

    public int getYellowTime() {

        return yellowTime;
    }

    /**
     * Sets a new duration of each green-yellow cycle.
     *
     * The current progress of the lights cycle should be reset, such that on
     * the next call to oneSecond(), only one second of the new duration has
     * been elapsed for the incoming route that currently has a green light.
     * @param duration the new light signal duration
     */

    public void setDuration(int duration) {

        if (duration > getYellowTime()) {

            this.duration = duration;
            secondsPassed = 0;
        }
    }

    /**
     * Simulates one second passing and updates the state of this set of traffic
     * lights.
     *
     * If enough time has passed such that a full green-yellow duration has
     * elapsed, or such that the current green light should now be yellow,
     * the appropriate light signals should be changed:
     *
     *     When a traffic light signal has been green for 'duration - yellowTime
     *     ' seconds, it should be changed from green to yellow.
     *     When a traffic light signal has been yellow for 'yellowTime' seconds,
     *     it should be changed from yellow to red, and the next incoming route
     *     in the order passed to IntersectionLights(List, int, int) should be
     *     given a green light. If the end of the list of routes has been reached,
     *     simply wrap around to the start of the list and repeat.
     *
     * If no routes are connected to the intersection, the duration shall not
     * elapse and the call should simply return without changing anything.
     */

    @Override
    public void oneSecond() {

        if (secondsPassed == 0) {
            connections.get(currentRoute).setSignal(TrafficSignal.GREEN);
        }
        secondsPassed++;

        if (connections.get(currentRoute).getTrafficLight().getSignal() ==
                TrafficSignal.YELLOW) {
            yellowSecondsPassed++;

            if (yellowSecondsPassed == getYellowTime()) {
                connections.get(currentRoute).setSignal
                        (TrafficSignal.RED);
                if ((connections.size() - (currentRoute + 1)) > 0) {
                    currentRoute++;
                }
                else {
                    currentRoute = 0;
                }
                secondsPassed = 0;
                yellowSecondsPassed = 0;
            }
        }

        if (secondsPassed == (duration - getYellowTime())) {
            connections.get(currentRoute).setSignal
                    (TrafficSignal.YELLOW);
        }
    }

    /**
     * Returns the string representation of this set of IntersectionLights.
     * The format to return is "duration:list,of,intersection,ids"
     * @return formatted string representation
     */

    @Override
    public String toString() {

        ArrayList<String> intersections = new ArrayList<>();

        for (Route route : connections) {

            intersections.add(route.getFrom().getId());
        }

        String finalList = intersections.toString().replace("[", "")
                .replace("]", "").replaceAll(" ", "");

        return duration + ":" + finalList;
    }
}
