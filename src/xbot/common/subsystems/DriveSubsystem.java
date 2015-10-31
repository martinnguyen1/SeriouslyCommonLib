package xbot.common.subsystems;

import org.apache.log4j.Logger;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.actuators.XSpeedController;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.MathUtils;
import xbot.common.math.XYPair;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class DriveSubsystem extends BaseSubsystem
{
    private static Logger log = Logger.getLogger(DriveSubsystem.class);
    
    public XSpeedController leftFrontDrive; //Motor 0
    public XSpeedController leftRearDrive; //Motor 1
    public XSpeedController rightFrontDrive; //Motor 2
    public XSpeedController rightRearDrive; //Motor 3
    
    private DoubleProperty leftFrontSpeedProp;
    private DoubleProperty leftRearSpeedProp;
    private DoubleProperty rightFrontSpeedProp;
    private DoubleProperty rightRearSpeedProp;
    
    private double rotationalPower = 0;
    private XYPair translationalVector = new XYPair(0, 0);
    private WPIFactory deviceFactory;
    
    @Inject
    public DriveSubsystem(WPIFactory deviceFactory, PropertyManager propManager)
    {
        log.info("Creating DriveSubsystem");
        this.deviceFactory = deviceFactory;
        SetupMotors();

        this.leftFrontSpeedProp = new DoubleProperty("Left front power", 0, propManager);
        this.leftRearSpeedProp = new DoubleProperty("Left rear power", 0, propManager);
        this.rightFrontSpeedProp = new DoubleProperty("Right front power", 0, propManager);
        this.rightRearSpeedProp = new DoubleProperty("Right rear power", 0, propManager);
    }
    
    // Override me if you have different motor settings!
    private void SetupMotors()
    {
        leftFrontDrive = deviceFactory.getSpeedController(0);
        leftRearDrive = deviceFactory.getSpeedController(1);
        rightFrontDrive = deviceFactory.getSpeedController(2);
        rightRearDrive = deviceFactory.getSpeedController(3);
    }
    
    // Override me if you have different motor settings!
    private void SetMotorPower(double leftFront, double leftRear, double rightFront, double rightRear)
    {
        // Update the dashboards with new speeds
        leftFrontSpeedProp.set(leftFront);
        leftRearSpeedProp.set(leftRear);
        rightFrontSpeedProp.set(rightFront);
        rightRearSpeedProp.set(rightRear);
        
        // Update the motors with new speeds
        leftFrontDrive.set(leftFront);
        leftRearDrive.set(leftRear);
        rightFrontDrive.set(rightFront);
        rightRearDrive.set(rightRear);        
    }
    
    public void robotRelativeTankDrive(double leftPower, double rightPower)
    {
        SetMotorPower(leftPower, leftPower, rightPower, rightPower);
    }
    
    /**
     * Runs the motors using the specified robot-relative vector and angular power.
     * Best to use one of the wrapper methods instead of this one explicitly.
     * @param translationVector Robot-relative translational vector
     * @param angularPower Angular rotation power
     */
    public void robotRelativeMecanumDrive(XYPair translationVector, double angularPower)
    {        
        double transX = MathUtils.constrainDoubleToRobotScale(translationVector.x);
        double transY = MathUtils.constrainDoubleToRobotScale(translationVector.y);
        double cappedAngularPower = MathUtils.constrainDoubleToRobotScale(angularPower);
        
        //Calculate wheel power values
        double[] wheelPowers = {
                transX + transY - cappedAngularPower, // left front
                -transX + transY - cappedAngularPower, // left rear
                -transX + transY + cappedAngularPower, // right front
                transX + transY + cappedAngularPower // right rear
        };
        
        leftFrontSpeedProp.set(wheelPowers[0]);
        leftRearSpeedProp.set(wheelPowers[1]);
        rightFrontSpeedProp.set(wheelPowers[2]);
        rightRearSpeedProp.set(wheelPowers[3]);
        
        SetMotorPower(wheelPowers[0], wheelPowers[1], wheelPowers[2], wheelPowers[3]);
    }
    
    /**
     * Sets the rotational power being sent to the mecanum drive
     * @param power
     */
    public void setRotationalPower(double power)
    {
        this.rotationalPower = power;
        updateDriveOutput();
    }
    
    /**
     * Sets the robot-relative vector that is being fed to the mecanum drive
     * @param newVector
     */
    public void setTranslatonalVector(XYPair newVector)
    {
        this.translationalVector = newVector;
        updateDriveOutput();
    }
    
    private void updateDriveOutput()
    {
        robotRelativeMecanumDrive(this.translationalVector, this.rotationalPower);        
    }    
}
