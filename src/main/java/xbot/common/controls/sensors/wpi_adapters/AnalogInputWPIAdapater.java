package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import xbot.common.controls.sensors.XAnalogInput;

public class AnalogInputWPIAdapater extends XAnalogInput {
    AnalogInput input;

    @Inject
    public AnalogInputWPIAdapater(@Assisted("channel") int channel) {
        input = new AnalogInput(channel);
        LiveWindow.addSensor("Analog input", channel, this.getInternalDevice());
    }

    public int getValue() {
        return input.getValue();
    }

    public double getVoltage() {
        return input.getVoltage();
    }

    public double getAverageVoltage() {
        return input.getAverageVoltage();
    }

    public void setAverageBits(int bits) {
        input.setAverageBits(bits);
    }

    public AnalogInput getInternalDevice() {
        return input;
    }

    @Override
    public int getChannel() {
        return input.getChannel();
    }

    @Override
    public boolean getAsDigital(double threshold) {
        return getVoltage() >= threshold;
    }
}
