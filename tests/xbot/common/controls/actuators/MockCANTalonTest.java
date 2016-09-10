package xbot.common.controls.actuators;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import xbot.common.controls.MockRobotIO;
import xbot.common.injection.BaseWPITest;

public class MockCANTalonTest extends BaseWPITest {
    @Test
    public void testSpeedControl() {
        XCANTalon talon = new MockCANTalon(1, mockRobotIO);
        talon.setProfile(0);
        talon.setP(0.5);
        
        talon.setControlMode(TalonControlMode.Speed);
        talon.setFeedbackDevice(FeedbackDevice.QuadEncoder);
        
        talon.set(2);
        assertEquals(1 * MockRobotIO.BUS_VOLTAGE, talon.getOutputVoltage(), 1e-5);
        assertEquals(1, mockRobotIO.getPWM(-1), 1e-5);

        talon.set(-1);
        assertEquals(-0.5 * MockRobotIO.BUS_VOLTAGE, talon.getOutputVoltage(), 1e-5);
        assertEquals(-0.5, mockRobotIO.getPWM(-1), 1e-5);
    }
}