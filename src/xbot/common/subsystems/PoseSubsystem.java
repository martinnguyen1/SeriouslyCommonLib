package xbot.common.subsystems;

import org.apache.log4j.Logger;

import xbot.common.controls.sensors.XGyro;
import xbot.common.injection.wpi_factories.WPIFactory;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.XYPair;

import com.google.inject.Inject;

public class PoseSubsystem {

    private static Logger log = Logger.getLogger(PoseSubsystem.class);
    
    private WPIFactory deviceFactory;
    
    public XGyro gyro;
    private double gyroYawOffset = 0;
    
    public double FORWARD_ANGLE = 90;
    
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
