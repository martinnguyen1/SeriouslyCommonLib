package xbot.common.subsystems;

import org.junit.Before;
import org.junit.Test;

import edu.wpi.first.wpilibj.MockSpeedController;
import static org.junit.Assert.assertEquals;
import xbot.common.injection.BaseWPITest;
import xbot.common.math.XYPair;

public class DriveTests extends BaseWPITest{

    DriveSubsystem drive;
    
    @Before
    public void setUp() {
        super.setUp();
        
        drive = injector.getInstance(DriveSubsystem.class);
    }
    
    @Test
    public void testTank() {
        double left = 0;
        double right = 0;
        drive.robotRelativeTankDrive(left, right);
        checkMotorPowers(left, right, left, right);
        
        left = 1;
        right = -1;
        drive.robotRelativeTankDrive(left, right);
        checkMotorPowers(left, right, left, right);
        
        left = 100;
        right = -100;
        drive.robotRelativeTankDrive(left, right);
        checkMotorPowers(1, -1, 1, -1);
        
        left = Double.POSITIVE_INFINITY;
        right = Double.NEGATIVE_INFINITY;
        drive.robotRelativeTankDrive(left, right);
        checkMotorPowers(1, -1, 1, -1);
        
        left = Double.NaN;
        right = Double.NaN;
        drive.robotRelativeTankDrive(left, right);
        checkMotorPowers(0, 0, 0, 0);
    }
    
    @Test
    public void testMecanum() {
        // forward
        XYPair direction = new XYPair(0, 0);
        double rotation = 0;
        
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(0, 0, 0, 0);
        
        direction.x = 1;
        direction.y = 0;
        rotation = 0;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, -1, -1, 1);
        
        direction.x = 0;
        direction.y = 1;
        rotation = 0;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, 1, 1, 1);
        
        direction.x = 0;
        direction.y = -1;
        rotation = 0;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(-1, -1, -1, -1);
        
        direction.x = 0;
        direction.y = 0;
        rotation = 1;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(-1, 1, -1, 1);
        
        direction.x = 0;
        direction.y = 0;
        rotation = -1;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, -1, 1, -1);
    }
    
    @Test
    public void checkMecanumCombination()
    {
        XYPair direction = new XYPair(0, 0);
        double rotation = 0;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(0, 0, 0, 0);
        
        direction.x = 1;
        direction.y = 1;
        rotation = 0;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, 0, 0, 1);
        
        direction.x = 1;
        direction.y = 1;
        rotation = 1;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, 1, -1, 1);
        
        direction.x = 100;
        direction.y = 100;
        rotation = 100;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(1, 1, -1, 1);
        
        direction.x = -100;
        direction.y = -100;
        rotation = -100;
        drive.setTranslatonalVector(direction);
        drive.setRotationalPower(rotation);
        checkMotorPowers(-1, -1, 1, -1);
    }
    
    protected void checkMotorPowers(double frontL, double frontR, double rearL, double rearR){
        assertEquals(frontL, ((MockSpeedController)drive.leftFrontDrive).get(), 0.001);
        assertEquals(frontR, ((MockSpeedController)drive.rightFrontDrive).get(), 0.001);
        assertEquals(rearL, ((MockSpeedController)drive.leftRearDrive).get(), 0.001);
        assertEquals(rearR, ((MockSpeedController)drive.rightRearDrive).get(), 0.001);
    }
}
