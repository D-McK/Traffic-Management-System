package tms.network;

import tms.intersection.Intersection;
import tms.route.Route;
import tms.sensors.Sensor;
import tms.util.DuplicateSensorException;
import tms.util.IntersectionNotFoundException;
import tms.util.InvalidOrderException;
import tms.util.RouteNotFoundException;

import java.util.*;

/**
 * Represents a network of intersections connected by routes.
 *
 * Networks need to keep track of the intersections that form the network.
 */

public class Network {
    /** The list of intersections that make up the network*/
    private ArrayList<Intersection> network;
    /** The default value of time for traffic lights to be yellow if not specified
     * by the network file*/
    private int yellowTime = 1;

    /**
     * Creates a new empty network with no intersections.
     */

    public Network() {

        network = new ArrayList<Intersection>();
    }

    /**
     * Returns the yellow time for all traffic lights in this network.
     * @return traffic light yellow time (in seconds)
     */

    public int getYellowTime() {

        return yellowTime;
    }

    /**
     * Sets the time that lights appear yellow between turning from green to red
     * (in seconds) for all new traffic lights added to this network.
     *
     * Existing traffic lights should not have their yellow time changed after
     * this method is called.
     *
     * The yellow time must be at least one (1) second. If the argument provided
     * is below 1, throw an exception and do not set the yellow time.
     * @param yellowTime new yellow time for all new traffic lights in network
     * @throws IllegalArgumentException if yellowTime < 1
     */

    public void setYellowTime(int yellowTime) {

        if (yellowTime < 1) {
            throw new IllegalArgumentException();
        } else {
            this.yellowTime = yellowTime;
        }
    }

    /**
     * Creates a new intersection with the given ID and adds it to this network.
     * @param id identifier of the intersection to be created
     * @throws IllegalArgumentException  if an intersection already exists with
     * the given ID, or if the given ID contains the colon character (:), or if
     * the id contains only whitespace (space, newline, tab, etc.) characters
     */

    public void createIntersection(String id) throws IllegalArgumentException {

        for (Intersection intersections : network) {
            if (intersections.getId().equals(id)) {
                throw new IllegalArgumentException();
            }
        }
        if (id.contains(":") || id.isBlank()) {
            throw new IllegalArgumentException();
        }
        network.add(new Intersection(id));
    }

    /**
     * Creates a connecting route between the two intersections with the given
     * IDs.
     *
     * The new route should start at 'from' and end at 'to', and have a default
     * speed of 'defaultSpeed'.
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param defaultSpeed speed limit of the route to create
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID of 'from' or 'to'
     * @throws IllegalStateException if a route already exists between the given
     * two intersections
     * @throws IllegalArgumentException if defaultSpeed is negative
     */

    public void connectIntersections(String from, String to, int defaultSpeed)
            throws IntersectionNotFoundException, IllegalStateException,
            IllegalArgumentException {

        Intersection intersectionFrom = findIntersection(from);
        Intersection intersectionTo = findIntersection(to);

        intersectionTo.addConnection(intersectionFrom, defaultSpeed);

    }

    /**
     * Adds traffic lights to the intersection with the given ID.
     *
     * The traffic lights will change every duration seconds and will cycle in
     * the order given by intersectionOrder, whereby each element in the list
     * represents the intersection from which each incoming route originates.
     * The yellow time will be the network's yellow time value
     * @param intersectionId ID of intersection to add traffic lights to
     * @param duration number of seconds between traffic light cycles
     * @param intersectionOrder list of origin intersection IDs, traffic lights
     *                         will go green in this order
     * @throws IntersectionNotFoundException if no intersection with the given
     * ID exists
     * @throws InvalidOrderException if the order specified is not a permutation
     * of the intersection's incoming routes; or if order is empty
     * @throws IllegalArgumentException if the given duration is less than the
     * network's yellow time plus one
     */

    public void addLights(String intersectionId, int duration, List<String>
            intersectionOrder) throws IntersectionNotFoundException,
            InvalidOrderException, IllegalArgumentException {

        List<Route> intersectionRoutes = new ArrayList<>();
        for (String originIntersection : intersectionOrder) {
            try {
                intersectionRoutes.add(getConnection(originIntersection,
                        intersectionId));
            } catch (RouteNotFoundException e) {
                throw new InvalidOrderException();
            }
        }
        findIntersection(intersectionId).addTrafficLights(intersectionRoutes,
                getYellowTime(), duration);
    }

    /**
     * Adds an electronic speed sign on the route between the two given
     * intersections.
     *
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param initialSpeed initial speed to be displayed on speed sign
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the two given
     * intersections
     * @throws IllegalArgumentException if the given speed is negative
     */

    public void addSpeedSign(String from, String to, int initialSpeed) throws
            IntersectionNotFoundException, RouteNotFoundException {

        getConnection(from, to).addSpeedSign(initialSpeed);
    }

    /**
     * Sets the speed limit on the route between the two given intersections
     * Speed limits can only be changed on routes with an electronic speed sign.
     * Calling this method on a route without an electronic speed sign results
     * in an exception
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @param newLimit new speed limit
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the two given
     * intersections
     */

    public void setSpeedLimit(String from, String to, int newLimit) throws
            IntersectionNotFoundException, RouteNotFoundException {

        getConnection(from, to).setSpeedLimit(newLimit);
    }

    /**
     * Sets the duration of each green-yellow cycle for the given intersection's
     * traffic lights.
     * @param intersectionId ID of target intersection
     * @param duration new duration of traffic lights
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'intersectionId'
     */

    public void changeLightDuration(String intersectionId, int duration) throws
            IntersectionNotFoundException {

        Intersection intersectionPresent = findIntersection(intersectionId);

        intersectionPresent.setLightDuration(duration);
    }

    /**
     * Returns the route that connects the two given intersections.
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @return Route that connects these intersections
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'to' or 'from'
     * @throws RouteNotFoundException if no route exists between the two given
     * intersections
     */

    public Route getConnection(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {

        Intersection intersectionFrom = findIntersection(from);
        Intersection intersectionTo = findIntersection(to);

        return intersectionTo.getConnection(intersectionFrom);
    }

    /**
     * Adds a sensor to the route between the two intersections with the given
     * IDs.
     * @param from ID of intersection at which the route originates
     * @param to ID of intersection at which the route ends
     * @param sensor sensor instance to add to the route
     * @throws DuplicateSensorException if a sensor already exists on the route
     * with the same type
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route exists between the given
     * to/from intersections
     */

    public void addSensor(String from, String to, Sensor sensor) throws
            DuplicateSensorException, IntersectionNotFoundException,
            RouteNotFoundException {

        getConnection(from, to).addSensor(sensor);
    }

    /**
     * Returns the congestion level on the route between the two given
     * intersections.
     * @param from ID of origin intersection
     * @param to ID of destination intersection
     * @return congestion level (integer between 0 and 100) of connecting route
     * @throws IntersectionNotFoundException if no intersection exists with an
     * ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no connecting route exists between the
     * given two intersections
     */

    public int getCongestion(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {

        return getConnection(from, to).getCongestion();
    }

    /**
     * Returns a new list containing all the intersections in this network.
     * @return list of all intersections in this network
     */

    public List<Intersection> getIntersections() {

        return new ArrayList<>(this.network);
    }

    /**
     * Attempts to find an Intersection instance in this network with the same
     * identifier as the given 'id' string.
     * @param id intersection identifier to search for
     * @return the intersection that was found (if one was found)
     * @throws IntersectionNotFoundException if no intersection could be found
     * with the given identifier
     */

    public Intersection findIntersection(String id) throws
            IntersectionNotFoundException {

        for (Intersection intersection : network) {
            if (intersection.getId().equals(id)) {
                return intersection;
            }
        }
        throw new IntersectionNotFoundException();
    }

    /**
     * Creates a new connecting route in the opposite direction to an existing
     * route including speed limits and speed signs present on the original
     * @param from ID of intersection that the existing route starts at
     * @param to ID of intersection that the existing route ends at
     * @throws IntersectionNotFoundException if no intersection exists with the
     * ID given by 'from' or 'to'
     * @throws RouteNotFoundException if no route currently exists between given
     * two intersections
     */

    public void makeTwoWay(String from, String to) throws
            IntersectionNotFoundException, RouteNotFoundException {

        Route currentRoute = getConnection(from, to);

        connectIntersections(to, from, currentRoute.getSpeed());

        if (currentRoute.hasSpeedSign()){
            getConnection(to, from).addSpeedSign(currentRoute.getSpeed());
        }
    }

    /**
     * Returns true if and only if this network is equal to the other given network.
     *
     * For two networks to be equal, they must have the same number of
     * intersections, and all intersections in the first network must be
     * contained in the second network, and vice versa.
     * @param o other object to compare equality
     * @return true if equal, false otherwise.
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Network network1 = (Network) o;
        return Objects.equals(network, network1.network);
    }

    /**
     * Returns the hash code of this network.
     *
     * Two networks that are equal must have the same hash code.
     * @return hash code of the network
     */

    @Override
    public int hashCode() {
        return Objects.hash(network);
    }

    /**
     * Returns the string representation of this network.
     *
     * The format of the string to return is identical to that described in
     * NetworkInitialiser.loadNetwork(String). All intersections in the network,
     * including all connecting routes with their respective sensors, are
     * included in the returned string.
     * @return string representation of this network
     */

    @Override
    public String toString() {

        StringBuilder builder = new StringBuilder();
        List<String> routesTemp = new ArrayList<>();
        List<String> intersectionsTemp = new ArrayList<>();
        int i = 0;

        builder.append(network.size()).append(System.lineSeparator());

        for (Intersection intersection : network) {
            for (Route route : intersection.getConnections()) {
                i++;
                routesTemp.add(route.toString());
            }
        }
        builder.append(i).append(System.lineSeparator());
        builder.append(getYellowTime()).append(System.lineSeparator());

        for (Intersection intersection : network) {
            intersectionsTemp.add(intersection.toString());
        }
        Collections.sort(intersectionsTemp);
        for (String string : intersectionsTemp) {
            builder.append(string).append(System.lineSeparator());
        }
        Collections.sort(routesTemp);
        for (String string : routesTemp) {
            builder.append(string).append(System.lineSeparator());
        }
        return String.valueOf(builder);
    }
}