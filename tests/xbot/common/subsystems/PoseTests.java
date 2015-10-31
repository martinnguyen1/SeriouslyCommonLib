package xbot.common.subsystems;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

import xbot.common.injection.BaseWPITest;

public class PoseTests extends BaseWPITest {
    
    PoseSubsystem pose;
    
    @Before
    public void setUp() {
        super.setUp();
        
        pose = injector.getInstance(PoseSubsystem.class);
    }
    
    @Test
    public void checkInitiallyPointingForward(){
       assertEquals(pose.FORWARD_ANGLE, pose.getCalibratedGyroYaw().getValue(), 0.001);
    }
    
    @Test
    public void checkCalibrateToNewAngle(){
        pose.calibrate(15);
        assertEquals(15, pose.getCalibratedGyroYaw().getValue(), 0.001);
    }
    
    @Test
    public void checkCalibrateToForward()
    {
        pose.calibrate(15);
        assertEquals(15, pose.getCalibratedGyroYaw().getValue(), 0.001);
        pose.calibrate();
        assertEquals(pose.FORWARD_ANGLE, pose.getCalibratedGyroYaw().getValue(), 0.001);
    }
    
    @Test
    public void checkRotateSome()
    {
        mockRobotIO.setGyroHeading(180);
        assertEquals(pose.FORWARD_ANGLE+180, pose.getCalibratedGyroYaw().getValue(), 0.001);
    }
    
    @Test
    public void checkRotateThenCalibrateThenRotate() {
        mockRobotIO.setGyroHeading(90);
        assertEquals(pose.FORWARD_ANGLE+90, pose.getCalibratedGyroYaw().getValue(), 0.001);
        
        pose.calibrate();
        assertEquals(pose.FORWARD_ANGLE, pose.getCalibratedGyroYaw().getValue(), 0.001);
        
        mockRobotIO.setGyroHeading(180);
        assertEquals(pose.FORWARD_ANGLE+180-90, pose.getCalibratedGyroYaw().getValue(), 0.001);
    }

}
