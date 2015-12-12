package xbot.common.subsystems;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockEncoder;
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
    
    @Test
    public void checkGoForward() {
        setDistances(10, 10, 0);
        checkDistances(0, 10);
        
        setDistances(-10, -10, 0);
        checkDistances(0, -10);
    }
    
    @Test
    public void checkStrafe() {
        setDistances(0, 0, 10);
        checkDistances(10, 0);
        
        setDistances(0, 0, -10);
        checkDistances(-10, 0);
    }
    
    @Test
    public void checkRotateThenGoForward() {
        pose.calibrate(0);
        setDistances(10,10,0);
        checkDistances(10, 0);
    }
    
    @Test
    public void checkRollandPitch() {
        setTilt(10, 15);
        checkTilt(10, 15);
        
        setTilt(-10, -15);
        checkTilt(-10, -15);
    }    
    
    @Test
    public void checkVelocity() {
        setSpeeds(10, 10, 0);
        checkRobotSpeeds(0, 10);
        checkFieldSpeeds(0, 10);
        
        setSpeeds(10, 0, 0);
        checkRobotSpeeds(0, 5);
        checkFieldSpeeds(0, 5);
    }
    
    @Test
    public void turnRightAndCheckVelocity() {
        pose.calibrate(0);
        setSpeeds(10, 10, 0);
        checkRobotSpeeds(0, 10);
        checkFieldSpeeds(10, 0);
    }
    
    @Test
    public void checkStopped(){
        setSpeeds(10, 10, 0);
        assertEquals(false, pose.isStopped(5));
        assertEquals(false, pose.isStopped(10));
        assertEquals(true, pose.isStopped(11));
        
        setSpeeds(-10, -10, 0);
        assertEquals(false, pose.isStopped(5));
        assertEquals(false, pose.isStopped(10));
        assertEquals(true, pose.isStopped(11));
    }
    
    
    private void setDistances(double left, double right, double middle)
    {
        ((MockEncoder)pose.leftLongitudinalEncoder).setDistance(left);
        ((MockEncoder)pose.rightLongitudinalEncoder).setDistance(right);
        ((MockEncoder)pose.middleTransverseEncoder).setDistance(middle);
    }
    
    private void checkDistances(double x, double y)
    {
        assertEquals(x, pose.getPosition().x, 0.001);
        assertEquals(y, pose.getPosition().y, 0.001);
    }
    
    private void setSpeeds(double left, double right, double middle)
    {
        ((MockEncoder)pose.leftLongitudinalEncoder).setRate(left);
        ((MockEncoder)pose.rightLongitudinalEncoder).setRate(right);
        ((MockEncoder)pose.middleTransverseEncoder).setRate(middle);
    }
    
    private void checkFieldSpeeds(double x, double y)
    {
        assertEquals(x, pose.getVelocityFieldRelative().x, 0.001);
        assertEquals(y, pose.getVelocityFieldRelative().y, 0.001);
    }
    
    private void checkRobotSpeeds(double x, double y)
    {
        assertEquals(x, pose.getVelocityRobotRelative().x, 0.001);
        assertEquals(y, pose.getVelocityRobotRelative().y, 0.001);
    }
    
    private void setTilt(double roll, double pitch) {
        this.mockRobotIO.setGyroRoll(roll);
        this.mockRobotIO.setGyroPitch(pitch);
        
    }
    
    private void checkTilt(double roll, double pitch) {
        assertEquals(roll, pose.getTilt().x, 0.001);
        assertEquals(pitch, pose.getTilt().y, 0.001);
    }
}
