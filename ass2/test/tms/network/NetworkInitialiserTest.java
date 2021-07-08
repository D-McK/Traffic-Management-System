package tms.network;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import tms.intersection.Intersection;
import tms.route.Route;
import tms.sensors.DemoPressurePad;
import tms.sensors.Sensor;
import tms.util.DuplicateSensorException;
import tms.util.IntersectionNotFoundException;
import tms.util.RouteNotFoundException;


public class NetworkInitialiserTest {

    private Network network;
    private Intersection trafficLightIntersection;
    private Sensor pp;

    @Before
    public void setUp() throws Exception {

        network = NetworkInitialiser.loadNetwork("networks/demo.txt");
        trafficLightIntersection = network.findIntersection("Y");
        pp = new DemoPressurePad(new int[]{5, 2, 4, 4, 1, 5, 2, 7, 3, 5, 6, 5,
                8, 5, 4, 2, 3, 3, 2, 5}, 5);
    }

    @Test
    public void checkIntersectionNumberTest() {

        Assert.assertEquals(4, network.getIntersections().size());
    }
    @Test
    public void checkRouteNumberTest() {

        int i = 0;
        for (Intersection intersection : network.getIntersections()) {
            for (Route route : intersection.getConnections()) {
                i++;
            }
        }
        Assert.assertEquals(5, i);
    }
    @Test
    public void checkYellowTimeTest() {

        Assert.assertEquals(1, network.getYellowTime());
    }
    @Test
    public void checkTrafficLightCreationTest() {

        Assert.assertTrue(trafficLightIntersection.hasTrafficLights());
    }
    @Test
    public void checkLightDuration() {

        Assert.assertEquals(3, Integer.parseInt(trafficLightIntersection.toString()
                .split(":")[1]));
    }
    @Test
    public void checkLightRoutes() {

        Assert.assertEquals("Z,X", trafficLightIntersection.toString()
                .split(":")[2]);
    }
    @Test(expected = DuplicateSensorException.class)
    public void checkRepeatedSensor() throws IntersectionNotFoundException,
            DuplicateSensorException, RouteNotFoundException {

        network.addSensor("Y", "X", pp);
    }
    @Test(expected = IntersectionNotFoundException.class)
    public void checkNoIntersectionRoute() throws IntersectionNotFoundException {

        network.connectIntersections("Y", "B", 60);
    }
}
