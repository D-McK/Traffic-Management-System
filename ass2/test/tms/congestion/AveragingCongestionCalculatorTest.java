package tms.congestion;

import org.junit.Test;
import org.junit.Assert;
import tms.sensors.DemoPressurePad;
import tms.sensors.DemoSpeedCamera;
import tms.sensors.DemoVehicleCount;
import tms.sensors.Sensor;

import java.util.ArrayList;


public class AveragingCongestionCalculatorTest {

    @Test
    public void calculateCongestionNoSensorsTest() {

        CongestionCalculator cc = new AveragingCongestionCalculator
                (new ArrayList<Sensor>());
        Assert.assertEquals(0, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionOneSensorTest() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoPressurePad(new int[]{30, 40, 30}, 100  ));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(30, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionOneSensorTestTwo() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoPressurePad(new int[]{36, 40, 30}, 40  ));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(90, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionTwoSensorsTest() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoPressurePad(new int[]{30, 40, 30}, 100  ));
        s.add(new DemoSpeedCamera(new int[]{60, 40}, 100));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(35, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionTwoSensorsTestTwo() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoSpeedCamera(new int[]{60, 40}, 100));
        s.add(new DemoVehicleCount(new int[]{60, 30}, 100));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(40, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionTwoSensorsTestThree() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoPressurePad(new int[]{60, 40}, 100));
        s.add(new DemoVehicleCount(new int[]{60, 30}, 100));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(50, cc.calculateCongestion());
    }

    @Test
    public void calculateCongestionThreeSensorsTest() {

        ArrayList<Sensor> s = new ArrayList<>();
        s.add(new DemoSpeedCamera(new int[]{60, 40}, 100));
        s.add(new DemoVehicleCount(new int[]{60, 30}, 100));
        s.add(new DemoPressurePad(new int[]{80, 50}, 100));

        CongestionCalculator cc = new AveragingCongestionCalculator(s);

        Assert.assertEquals(53, cc.calculateCongestion());
    }
}
