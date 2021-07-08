package tms.intersection;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import tms.route.Route;
import tms.route.TrafficSignal;

import java.util.ArrayList;

public class IntersectionLightsTest {
    private ArrayList<Route> routes;
    private Intersection intersectionTest;
    private Intersection intersectionTest2;
    private Route r;
    private Route r2;
    private IntersectionLights lights;

    @Before
    public void setUp() throws Exception {

        routes = new ArrayList<>();
        intersectionTest = new Intersection("X");
        intersectionTest2 = new Intersection("Z");
        r = new Route("W", intersectionTest, 60);
        r.addTrafficLight();
        routes.add(r);
        r2 = new Route("Y", intersectionTest2, 50);
        r2.addTrafficLight();
        routes.add(r2);
        lights = new IntersectionLights(routes, 4,
                8);
    }

    @Test
    public void checkFirstLightGreenTest() {

        Assert.assertEquals(TrafficSignal.GREEN, routes.get(0).getTrafficLight()
                .getSignal());
    }

    @Test
    public void getYellowTimeTest() {

        Assert.assertEquals(4, lights.getYellowTime());
    }

    @Test
    public void setDurationTest() {

        lights.setDuration(10);
        Assert.assertEquals(10, Integer.parseInt(lights.toString().
                split(":")[0]));
    }

    @Test
    public void setDurationTestTwo() {

        lights.setDuration(3);
        Assert.assertEquals(8, Integer.parseInt(lights.toString().
                split(":")[0]));

    }

    @Test
    public void oneSecondTest() {

        for (int i = 0; i < Integer.parseInt(lights.toString().
                split(":")[0]) - lights.getYellowTime(); i++) {
            lights.oneSecond();
        }
        Assert.assertEquals(TrafficSignal.YELLOW, routes.get(0).getTrafficLight
                ().getSignal());

    }

    @Test
    public void oneSecondTestTwo() {

        for (int i = 0; i < Integer.parseInt(lights.toString().
                split(":")[0]) - lights.getYellowTime(); i++) {
            lights.oneSecond();
        }

        if (routes.get(0).getTrafficLight().getSignal() == TrafficSignal.YELLOW) {
            for (int x = 0; x < lights.getYellowTime(); x++) {
                lights.oneSecond();
            }
        }
        Assert.assertEquals(TrafficSignal.RED, routes.get(0).getTrafficLight
                ().getSignal());
    }

    @Test
    public void oneSecondTestThree() {

        for (int i = 0; i < Integer.parseInt(lights.toString().
                split(":")[0]) - lights.getYellowTime(); i++) {
            lights.oneSecond();
        }

        if (routes.get(0).getTrafficLight().getSignal() == TrafficSignal.YELLOW) {
            for (int x = 0; x < lights.getYellowTime(); x++) {
                lights.oneSecond();
            }
        }
        lights.oneSecond();
        Assert.assertEquals(TrafficSignal.GREEN, routes.get(1).getTrafficLight()
                .getSignal());
    }

    @Test
    public void toStringTest() {

        Assert.assertEquals("8:X,Z", lights.toString());
    }
}

