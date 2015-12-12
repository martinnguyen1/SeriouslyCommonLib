package xbot.common.subsystems;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockEncoder;
import xbot.common.math.XYPair;

public class TranslationalTests extends DriveTests {

    TranslationalSubsystem trans;
    PoseSubsystem pose;
    
    @Before
    public void setUp() {
        super.setUp();
        
        trans = injector.getInstance(TranslationalSubsystem.class);
        pose = injector.getInstance(PoseSubsystem.class);
    }
    
    @Test
    public void testRobotTranslation()
    {
        XYPair vector = new XYPair(0, 0);
        trans.translatePowerRobotRelative(vector);
        checkMotorPowers(0, 0, 0, 0);
        
        vector.x = 1;
        trans.translatePowerRobotRelative(vector);
        checkMotorPowers(1, -1, -1, 1);
    }
    
    @Test
    public void testRobotTranslationWhileTilting()
    {
        XYPair vector = new XYPair(0, 0);
        trans.translatePowerRobotRelative(vector);
        checkMotorPowers(0, 0, 0, 0);
        
        //tilt backwards - this should cause the robot to try and drive backwards to correct.
        mockRobotIO.setGyroPitch(10);
        vector.y = 1;
        trans.translatePowerRobotRelative(vector);
        checkMotorPowers(-1, -1, -1, -1);
        
        //tilt left - this should cause the robot to try and drive left to correct
        mockRobotIO.setGyroPitch(0);
        mockRobotIO.setGyroRoll(10);
        vector.y = 1;
        trans.translatePowerRobotRelative(vector);
        checkMotorPowers(-1, 1, 1, -1);
    }
    
    @Test
    public void testTranslateFieldRelative() {
        XYPair vector = new XYPair(0, 0);
        trans.translatePowerFieldRelative(vector);
        checkMotorPowers(0, 0, 0, 0);
        
        // initially pointing at robot heading, so forwards is forwards
        vector.y = 1;
        trans.translatePowerFieldRelative(vector);
        checkMotorPowers(1, 1, 1, 1);
        
        // rotate the robot 90 degrees positive. The robot is now facing "left", so it must translate right
        // in order to go forwards
        mockRobotIO.setGyroHeading(90);
        vector.y = 1;
        trans.translatePowerFieldRelative(vector);
        checkMotorPowers(1, -1, -1, 1);
    }
    
    @Test
    public void testTranslateFieldRelativeVelocity() {
        XYPair vector = new XYPair(0, 0);
        trans.translateVelocityFieldRelative(vector);
        checkMotorPowers(0, 0, 0, 0);
        
        vector.x = 1000; // PID is hard to get strict numbers for - so I'll just use
        // very large values so they cap at 1 and -1.
        trans.translateVelocityFieldRelative(vector);
        checkMotorPowers(1, -1, -1, 1);
        
        // need to reset PID between runs, so we have a fresh slate each sub-test
        trans.resetState();
        mockRobotIO.setGyroHeading(90);
        vector.x = 0;
        vector.y = 10000;
        trans.translateVelocityFieldRelative(vector);
        checkMotorPowers(1, -1, -1, 1);
    }
    
    @Test
    public void testTranslateToPosition() {
        XYPair location = new XYPair(0, 100);
        trans.goToFieldPositionAbsolute(location);
        checkMotorPowers(.5, .5, .5, .5);
        
        setDistances(90, 90, 0);
        trans.goToFieldPositionAbsolute(location);
        assertEquals(false, trans.isAtPositionTarget(0, 1));
        assertEquals(false, trans.isAtPositionTarget(10, 1));
        assertEquals(true, trans.isAtPositionTarget(11, 1));
    }
    
    private void setDistances(double left, double right, double middle)
    {
        ((MockEncoder)pose.leftLongitudinalEncoder).setDistance(left);
        ((MockEncoder)pose.rightLongitudinalEncoder).setDistance(right);
        ((MockEncoder)pose.middleTransverseEncoder).setDistance(middle);
    }
}
