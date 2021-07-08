package tms.network;

import tms.sensors.DemoPressurePad;
import tms.sensors.DemoSpeedCamera;
import tms.sensors.DemoVehicleCount;
import tms.util.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class responsible for loading and initialising a saved network from a
 * file.
 */

public class NetworkInitialiser {

    /** Delimiter used to separate individual pieces of data on a single line */
    public static final String LINE_INFO_SEPARATOR = ":";
    /** Delimiter used to separate individual elements in a variable-length list
     *  on a single line*/
    public static final String LINE_LIST_SEPARATOR = ",";

    /**
     * Default Constructor of the NetworkInitialiser Class
     */

    public NetworkInitialiser() {
    }

    /**
     * Loads a saved Network from the file with the given filename
     * @param filename name of the file from which to load a network
     * @return the Network loaded from file
     * @throws IOException any IOExceptions encountered when reading the file
     * are bubbled up
     * @throws InvalidNetworkException if the file format of the given file is
     * invalid
     */

    public static Network loadNetwork(String filename) throws IOException,
            InvalidNetworkException {

        Network network = new Network();
        List<String> networkFile;

        try {

            BufferedReader in = new BufferedReader(new FileReader(filename));

            networkFile = in.lines().collect(Collectors.toList());
            networkFile.removeIf(string -> string.startsWith(";"));
            if (networkFile.contains("")) {
                throw new InvalidNetworkException();
            }
            checkNewLines(networkFile);
            network.setYellowTime(Integer.parseInt(networkFile.get(2)));
            createIntersections(networkFile, network);
            createRoutes(networkFile, network);
            createTrafficLights(networkFile, network);

        } catch (FileNotFoundException e) {
            throw new InvalidNetworkException();
        } catch (NumberFormatException e) {
            throw new IOException();
        }
        return network;
    }

    /**
     * A helper method to turn an array of strings into an array of integers
     * @param string The string series of values to turn into an int array
     * @return the converted integer array
     */

    private static int[] stringToIntArray(String string) {

        String[] stringArray = string.split(LINE_LIST_SEPARATOR);
        int size = stringArray.length;
        int[] intArray = new int[size];

        for (int i = 0; i < size; i++) {
            intArray[i] = Integer.parseInt(stringArray[i]);
        }
        return intArray;
    }

    /**
     * Creates the intersections within the network using information passed from
     * the network file
     * @param networkFile A list of Strings wherein each entry represents a line
     *                    of the network file
     * @param network The initialised network this TMS is using
     * @throws InvalidNetworkException if the intersection ID in the file does
     * not exist when attempting to add it to the current network
     */

    private static void createIntersections(List<String> networkFile, Network
            network) throws InvalidNetworkException {

        int numberOfIntersections = Integer.parseInt(networkFile.get(0));
        int intersection = 3;

        for (int i = 0; i < numberOfIntersections; i++) {

            try {
                String[] intersectionInformation = networkFile.get(intersection).split
                        (LINE_INFO_SEPARATOR);
                network.createIntersection(intersectionInformation[0]);
                intersection++;
            } catch (IllegalArgumentException e) {
                throw new InvalidNetworkException();
            }
        }
    }

    /**
     * Creates the routes within the network using the information relevant to
     * route creation from the network file
     * @param networkFile A list of Strings wherein each entry represents a line
     *                    of the network file
     * @param network The initialised network this TMS is using
     * @throws InvalidNetworkException if a sensor within the file is not one of
     * the three outlined OR if there is a duplicate sensor on the same route OR
     * if the route attempting to be added does not exist when trying to add
     * a sensor to it
     */

    private static void createRoutes(List<String> networkFile, Network network)
            throws InvalidNetworkException {

        int numberOfRoutes = Integer.parseInt(networkFile.get(1));
        int route = 3 + Integer.parseInt(networkFile.get(0));

        for (int i = 0; i < numberOfRoutes; i++) {

            String[] routeInformation = networkFile.get(route).split
                    (LINE_INFO_SEPARATOR);
            try {
                network.connectIntersections(routeInformation[0],
                        routeInformation[1], Integer.parseInt
                                (routeInformation[2]));
                boolean invalidSensor = true;

                for (int x = 0; x < Integer.parseInt(routeInformation[3]);
                     x++) {
                    int y = 1;

                    String[] sensorInformation = networkFile.get(route + y).split
                            (LINE_INFO_SEPARATOR);
                    int[] array = stringToIntArray(sensorInformation[2]);
                    if (sensorInformation[0].equals("PP")) {
                        network.addSensor(routeInformation[0],
                                routeInformation[1], new DemoPressurePad
                                        (array, Integer.parseInt
                                                (sensorInformation[1])));
                        route++;
                        invalidSensor = false;
                    }
                    if (sensorInformation[0].equals("VC")) {
                        network.addSensor(routeInformation[0],
                                routeInformation[1], new DemoVehicleCount
                                        (array, Integer.parseInt
                                                (sensorInformation[1])));
                        route++;
                        invalidSensor = false;
                    }
                    if (sensorInformation[0].equals("SC")) {
                        network.addSensor(routeInformation[0],
                                routeInformation[1], new DemoSpeedCamera
                                        (array, Integer.parseInt
                                                (sensorInformation[1])));
                        route++;
                        invalidSensor = false;
                    }
                    y++;
                    if (invalidSensor) throw new InvalidNetworkException();
                }

            } catch (IntersectionNotFoundException | DuplicateSensorException |
                    RouteNotFoundException e) {
                throw new InvalidNetworkException();
            }
            route++;
        }
    }

    /**
     * Creates the traffic lights within the network using the information
     * relevant to the intersections that have them in the network file
     * @param networkFile A list of Strings wherein each entry represents a line
     *                    of the network file
     * @param network The initialised network this TMS is using
     * @throws InvalidNetworkException if the intersection of origin or of
     * destination does not exist when attempting to add a traffic light to it
     */

    private static void createTrafficLights(List<String> networkFile, Network network)
            throws InvalidNetworkException{

        int numberOfIntersections = Integer.parseInt(networkFile.get(0));
        int intersection = 3;

        try {
            for (int i = 0; i < numberOfIntersections; i++) {
                String[] trafficLightInfo = networkFile.get(intersection)
                        .split(LINE_INFO_SEPARATOR);

                if (trafficLightInfo.length > 1) {
                    String[] routesArray = trafficLightInfo[2].split
                            (LINE_LIST_SEPARATOR);
                    ArrayList<String> routes = new ArrayList<>
                            (Arrays.asList(routesArray));
                    ArrayList<String> updatedRoutes = new ArrayList<>();
                    for (String route : routes) {
                        route += ":" + trafficLightInfo[0];
                        updatedRoutes.add(route);
                    }
                    List<String> intersectionIds = new ArrayList<>();
                    routes = updatedRoutes;
                    for (String route : routes) {
                        String[] intersections = route.split(LINE_INFO_SEPARATOR);
                        intersectionIds.add(intersections[0]);
                    }
                    network.addLights(trafficLightInfo[0], Integer.parseInt
                            (trafficLightInfo[1]), intersectionIds);
                }
                intersection++;
            }

        } catch (IntersectionNotFoundException | InvalidOrderException e) {
            throw new InvalidNetworkException();
        }
    }

    /**
     * Checks the end of the network file to ensure there are NOT two or more
     * new empty lines ("\n")
     * @param networkFile A list of Strings wherein each entry represents a line
     *                    of the network file
     * @throws InvalidNetworkException if there are two or more empty lines at
     * the end of the network file
     */

    private static void checkNewLines(List<String> networkFile) throws
            InvalidNetworkException {

        if (networkFile.get(networkFile.size() - 1).equals("\n") &&
                networkFile.get(networkFile.size() - 2).equals("\n")){
            throw new InvalidNetworkException();
        }
    }
}