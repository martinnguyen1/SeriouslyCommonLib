package xbot.common.subsystems;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XEncoder;
import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.XYPair;

import com.google.inject.Inject;

public class PoseSubsystem {

    private static Logger log = Logger.getLogger(PoseSubsystem.class);
    
    protected WPIFactory deviceFactory;
    
    public XGyro gyro;
    public XEncoder leftLongitudinalEncoder;
    public XEncoder rightLongitudinalEncoder;
    public XEncoder middleTransverseEncoder;
    
    
    protected double robotXPosition;
    protected double robotYPosition;
    
    protected double robotXVelocity;
    protected double robotYVelocity;
    
    protected double robotTransverseTilt;
    protected double robotLongitundinalTilt;
    
    
    private double previousTotalLongitudinalDistanceX_Left;
    private double previousTotalLongitudinalDistanceX_Right;
    private double previousTotalLongitudinalDistanceY;
    
    
    protected double gyroYawOffset = 0;
    protected double FORWARD_ANGLE = 90;
    
    @Inject
    public PoseSubsystem(WPIFactory deviceFactory)
    {
        this.deviceFactory = deviceFactory;
        setupSensors();
        calibrate(FORWARD_ANGLE);
    }
    
    public void setupSensors()
    {
        gyro = deviceFactory.getGyro();
    }
    
    /**
     * Updates all base sensor data related to robot pose. This method needs to be called frequently 
     * and ideally periodically.
     * 
     * It needs to set the following protected variables:
     *     robotXPosition
     *     robotYPosition
     *     robotXVelocity
     *     robotYVelocity
     *     robotTransverseTilt
     *     robotLongitundinalTilt
     */
    public void updateSensorValues()
    {
        
    }
    
    public ContiguousDouble getUncalibratedGyroYaw()
    {
        return gyro.getYaw();
    }
    
    public ContiguousDouble getCalibratedGyroYaw()
    {
        ContiguousDouble yaw = gyro.getYaw();
        yaw.shiftValue(gyroYawOffset);
        return yaw;
    }
    
    public void calibrate()
    {
        log.info("Calibrating to " + FORWARD_ANGLE);
        calibrate(FORWARD_ANGLE);
    }
    
    public void calibrate(double newHeading)
    {
        ContiguousDouble currentRawHeading = getUncalibratedGyroYaw();
        
        ContiguousDouble newHeadingContiguous = new ContiguousDouble(
                currentRawHeading.getLowerBound(), 
                currentRawHeading.getUpperBound());
        newHeadingContiguous.setValue(newHeading);
        
        gyroYawOffset = gyro.getYaw().difference(newHeadingContiguous);
        log.info("Calibrating yaw offset to: " + gyroYawOffset);
    }

    public XYPair getTilt() {
        // TODO Auto-generated method stub
        return new XYPair(0,0);
    }

    public XYPair getMaxTilt() {
        // TODO Auto-generated method stub
        return new XYPair(1,1);
    }

    public XYPair getPosition() {
        // TODO Auto-generated method stub
        return new XYPair(0,0);
    }

    public XYPair getVelocityFieldRelative() {
        // TODO Auto-generated method stub
        return new XYPair(0,0);
    }

    public XYPair getVelocityRobotRelative() {
        // TODO Auto-generated method stub
        return new XYPair(0,0);
    }

    public boolean isStopped(double velTolerance) {
        // TODO Auto-generated method stub
        return false;
    }
    
    
}
