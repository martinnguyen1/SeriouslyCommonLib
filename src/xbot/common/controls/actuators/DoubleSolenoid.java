package xbot.common.controls.actuators;

/**
 * Represents a DoubleSolenoid (a solenoid with 2 control points that remembers state)
 */
public class DoubleSolenoid implements XSolenoid {
    XSolenoid trueSolenoid;
    XSolenoid falseSolenoid;
    
    boolean isInverted = false;
    
    boolean lastSet = false;
    
    public DoubleSolenoid(XSolenoid trueSolenoid, XSolenoid falseSolenoid) {
        this.trueSolenoid = trueSolenoid;
        this.falseSolenoid = falseSolenoid;
    }

    @Override
    public int getChannel() {
        return trueSolenoid.getChannel();
    }
    
    public int getTrueChannel() {
        return trueSolenoid.getChannel();
    }
    
    public int getFalseChannel() {
        return falseSolenoid.getChannel();
    }

    @Override
    public void set(boolean on) {
        lastSet = on;
        
        boolean value = !isInverted ? on : !on;
        
        this.trueSolenoid.set(value);
        this.falseSolenoid.set(!value);
        
    }

    @Override
    public boolean get() {
        return lastSet;
    }

    @Override
    public void setInverted(boolean isInverted) {
        this.isInverted = isInverted;
    }
    
    
        
}
