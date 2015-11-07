package xbot.common.subsystems;

import org.apache.log4j.Logger;

import xbot.common.command.BaseSubsystem;
import xbot.common.controls.sensors.XGyro;
import xbot.common.controls.sensors.XTimer;
import xbot.common.math.ContiguousDouble;
import xbot.common.math.PIDManager;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.PropertyManager;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class RotationalSubsystem extends BaseSubsystem
{
    private static Logger log = Logger.getLogger(DriveSubsystem.class);
    
    private DriveSubsystem drive;
    private PoseSubsystem pose;
    private XTimer assistTimer;

    private PIDManager headingPID;

    private final String assistDeadZonePropname = "RotationAssistDeadzone";
    private final String assistTimeoutPropname = "RotationAssistTimeout";
    
    private DoubleProperty assistDeadZone; // = 0.15;
    private DoubleProperty assistTimeout; // = 0.3;
    private Double headingAssistTarget = null;
    
    @Inject
    public RotationalSubsystem(PoseSubsystem pose, DriveSubsystem drive, PropertyManager propMan)
    {
        log.info("Creating RotationalSubsystem");
        
        headingPID = new PIDManager("Heading", propMan, 0.06d, 0d, 0d);
        assistTimer = new XTimer();
        this.drive = drive;
        this.pose = pose;
        
        assistDeadZone = propMan.createPersistentProperty(assistDeadZonePropname, 0.15);
        assistTimeout = propMan.createPersistentProperty(assistTimeoutPropname, 0.3);
    }

    /**
     * Resets all timers and history.
     * This should be called every time a new command takes over this subsystem or the purpose changes.
     * 
     * TODO: Auto-reset instead
     */
    public void resetState()
    {
        log.info("Resetting assistTimer, headingPID, and headingAssistTarget");
        assistTimer.reset();
        headingPID.reset();
        headingAssistTarget = null;
    }

    /**
     * Drives the rotation of the robot using a rotational speed.
     * Attempts to maintain yaw value while in the dead zone.
     * @param rotationalPower
     */
    public void humanAssistRotation(double rotationalPower)
    {
        if (Math.abs(rotationalPower) <= assistDeadZone.get())
        {
            if (!assistTimer.isStarted())
            {
                log.info("Waiting a bit before engaging heading maintainance");
                assistTimer.reset();
                assistTimer.start();
                
                setRotationalPower(0);
            }
            else if (assistTimer.isStarted() && assistTimer.getTime() <= assistTimeout.get())
            {
                setRotationalPower(0);
            }
            else
            {
                if(headingAssistTarget == null) {
                    headingAssistTarget = pose.getCalibratedGyroYaw().getValue();
                }
                
                maintainHeading(headingAssistTarget);
            }
        }
        else
        {
            if (assistTimer.isStarted()) {
                assistTimer.reset();
            }
            
            headingAssistTarget = null;
            setRotationalPower(rotationalPower);
        }
    }
    
    /**
     * Sets the target that will be used when the rotational assistance system is
     * engaged (maintaining). This value will be cleared when the rotational stick
     * is moved.
     * @param newTarget
     */
    public void setHumanAssistTargetHeading(double newTarget) {        
        headingAssistTarget = newTarget;
    }

    /**
     * Attempt to maintain the specified heading (should be called repeatedly)
     * @param targetHeading
     */
    public void maintainHeading(double targetHeading)
    {
        ContiguousDouble currYaw = pose.getCalibratedGyroYaw();
        ContiguousDouble heading = new ContiguousDouble(targetHeading,
                currYaw.getLowerBound(), currYaw.getUpperBound());

        double error = -currYaw.difference(heading);
        double pidResult = headingPID.calculate(0, error);

        setRotationalPower(pidResult);
    }

    public void setRotationalPower(double power)
    {
        drive.setRotationalPower(power);
    }
    
}
