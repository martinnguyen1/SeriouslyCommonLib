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
    
    protected XYPair fieldPosition;
    protected XYPair fieldVelocity;
    protected XYPair robotTilt;
    
    private double previousTotalLongitudinalDistance_Left;
    private double previousTotalLongitudinalDistance_Right;
    private double previousTotalTransverseDistance;
    
    protected double gyroYawOffset = 0;
    protected double FORWARD_ANGLE = 90;
    
    @Inject
    public PoseSubsystem(WPIFactory deviceFactory)
    {
        this.deviceFactory = deviceFactory;
        setupSensors();
        calibrate(FORWARD_ANGLE);
        
        fieldPosition = new XYPair();
        fieldVelocity = new XYPair();
        robotTilt = new XYPair();
    }
    
    /**
     * Allocates the sensors that this subsystem needs to discover position, rotation, velocity, and tilt.
     * If you use different sensors, override this method.
     */
    public void setupSensors()
    {
        gyro = deviceFactory.getGyro();

        leftLongitudinalEncoder = deviceFactory.getEncoder(0, 1);
        rightLongitudinalEncoder = deviceFactory.getEncoder(2, 3);
        middleTransverseEncoder = deviceFactory.getEncoder(4, 5);
    }
    
    /**
     * Updates all base sensor data related to robot pose. This method needs to be called frequently 
     * and ideally periodically.
     * 
     * It needs to set the following protected variables (all field-relative):
     *     robotPosition
     *     robotVelocity
     *     robotTilt
     */
    public void updateSensorValues()
    {
        double vDist1 = leftLongitudinalEncoder.getDistance() - previousTotalLongitudinalDistance_Left;
        double vDist2 = rightLongitudinalEncoder.getDistance() - previousTotalLongitudinalDistance_Right;
        double hDist = middleTransverseEncoder.getDistance() - previousTotalTransverseDistance;
        
        XYPair distSinceLastTick = new XYPair(hDist, (vDist1 + vDist2) / 2);
        
        //Rotate vector from robot- to field-relative
        distSinceLastTick.rotate(getCalibratedGyroYaw().getValue() - FORWARD_ANGLE);
        
        fieldPosition.add(distSinceLastTick);
        
        updatePrevTotals();
        
        double vVel1 = leftLongitudinalEncoder.getRate();
        double vVel2 = rightLongitudinalEncoder.getRate();
        double hVel = middleTransverseEncoder.getRate();
        
        fieldVelocity.y = (vVel1 + vVel2) / 2;        
        fieldVelocity.x = hVel;
        
        fieldVelocity = fieldVelocity.rotate(getCalibratedGyroYaw().getValue() - FORWARD_ANGLE);
        
        // tilt detection
        double pitch = gyro.getPitch();
        double roll = gyro.getRoll();
        
        robotTilt = new XYPair(roll, pitch);
        
        // now that we have all these values, update those key variables
    }
    
    private void updatePrevTotals()
    {
        previousTotalLongitudinalDistance_Left = leftLongitudinalEncoder.getDistance();
        previousTotalLongitudinalDistance_Right = rightLongitudinalEncoder.getDistance();
        previousTotalTransverseDistance = middleTransverseEncoder.getDistance();
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

    /**
     * Returns robot tilt, with roll as X and pitch as Y.
     * @return
     */
    public XYPair getTilt() {
        updateSensorValues();
        return robotTilt;
    }

    /**
     * Returns the maximum safe tilt value - experimentally derived. The idea being that if tilt >
     * max tilt, you may want to take immediate corrective action.
     * @return
     */
    public XYPair getMaxTilt() {
        return new XYPair(1,1);
    }

    /**
     * Returns the field-oriented position of the robot.
     * @return
     */
    public XYPair getPosition() {
        updateSensorValues();
        return fieldPosition;
    }

    public XYPair getVelocityFieldRelative() {
        updateSensorValues();
        return fieldVelocity;
    }

    public XYPair getVelocityRobotRelative() {
        updateSensorValues();
        // need to rotate velocity back towards robot
        XYPair robotVelocity = fieldVelocity.rotate(FORWARD_ANGLE - getCalibratedGyroYaw().getValue());
        return robotVelocity;
    }

    /**
     * Compares the robot's current velocity to the given tolerance - if it is below, we consider the robot
     * to be "stopped."
     * @param velTolerance
     * @return
     */
    public boolean isStopped(double velTolerance) {
        updateSensorValues();
        if (fieldVelocity.getMagnitude() < velTolerance)
        {
            return true;
        }
        return false;
    }
    
    
}
