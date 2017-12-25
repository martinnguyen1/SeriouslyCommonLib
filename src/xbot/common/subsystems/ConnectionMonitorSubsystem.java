package xbot.common.subsystems;

import com.google.inject.Singleton;
import edu.wpi.first.wpilibj.Timer;
import xbot.common.command.BaseSubsystem;
import xbot.common.logic.Latch;
import xbot.common.properties.DoubleProperty;
import xbot.common.properties.XPropertyManager;

import java.util.Observable;
import java.util.Observer;

@Singleton
public class ConnectionMonitorSubsystem extends BaseSubsystem implements Observer {

    protected final DoubleProperty timeOut;
    private final Latch connectionLatch = new Latch(true, Latch.EdgeType.Both);

    private double lastPacketReceivedTimestamp = Timer.getFPGATimestamp();
    private double previousDisconnectionTimestamp = -1;

    public ConnectionMonitorSubsystem(XPropertyManager propertyManager) {
        log.info("Creating");
        timeOut = propertyManager.createPersistentProperty("ConnectionMonitorTimeOut Seconds", 1.0);
        connectionLatch.addObserver(this);
    }

    public synchronized void setLastPacketReceivedTimestamp(double currentTimestamp) {
        connectionLatch.setValue((currentTimestamp - lastPacketReceivedTimestamp) < timeOut.get());
        this.lastPacketReceivedTimestamp = currentTimestamp;
    }

    public double getPreviousDisconnectionTimestamp() {
        return previousDisconnectionTimestamp;
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg == Latch.EdgeType.FallingEdge) {
            log.warn("The Driver Station has been disconnected for greater than " + timeOut.get() + " Second(s)");
            previousDisconnectionTimestamp = lastPacketReceivedTimestamp;
        }
    }
}
