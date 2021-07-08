package tms.congestion;

import tms.sensors.Sensor;
import java.util.List;

/**
 * An implementation of a congestion calculator that calculates the average
 * congestion value from all of its sensors.
 */

public class AveragingCongestionCalculator implements CongestionCalculator {
    /** The list of sensors to compile data from to return congestion level*/
    private List<Sensor> congestionCalculator;

    /**
     * Creates a new averaging congestion calculator for a given list of sensors
     * on a route.
     * @param sensors list of sensors to use in congestion calculation
     */

    public AveragingCongestionCalculator(List<Sensor> sensors){

        congestionCalculator = sensors;
    }

    /**
     * Calculates the average congestion level, as returned by
     * Sensor.getCongestion(), of all the sensors stored by this calculator.
     *
     * If there are no sensors stored, return 0.
     * @return the average congestion
     */

    public int calculateCongestion(){

        double total = 0;
        int average;

        if (congestionCalculator.size() == 0) {
            return 0;
        }
        for (Sensor c : congestionCalculator) {

            total += c.getCongestion();
        }
        average = (int) total / congestionCalculator.size();
        return average;
    }
}