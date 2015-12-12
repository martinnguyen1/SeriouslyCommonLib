package xbot.common.subsystems;

import static org.junit.Assert.assertEquals;

import java.util.Timer;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockEncoder;
import edu.wpi.first.wpilibj.MockTimer;
import xbot.common.math.XYPair;

public class RotationalTests extends DriveTests {

    RotationalSubsystem rot;
    PoseSubsystem pose;
    
    @Before
    public void setUp() {
        super.setUp();
        
        rot = injector.getInstance(RotationalSubsystem.class);
        pose = injector.getInstance(PoseSubsystem.class);
    }
    
    @Test
    public void testMaintainHeading() {
        rot.maintainHeading(90);
        checkMotorPowers(0, 0, 0, 0);
        
        // change angle
        
        // want to turn left a huge amount
        rot.maintainHeading(269);
        checkMotorPowers(-1, 1, -1, 1);
    }
    
    @Test
    public void testHumanAssistHeading() {
        rot.setHumanAssistTargetHeading(90);
        rot.humanAssistRotation(0);
        checkMotorPowers(0, 0, 0, 0);
        
        // move the gyro as if the robot is turnin gleft - the robot should try and hold original heading
        mockRobotIO.setGyroHeading(90);
        MockTimer t = injector.getInstance(MockTimer.class);
        t.advanceTimeInSecondsBy(100);
        
        rot.humanAssistRotation(0);
        checkMotorPowers(1, -1, 1, -1);
    }
}
