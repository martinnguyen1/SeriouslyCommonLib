package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import xbot.common.controls.sensors.XDigitalInput;

public class DigitalInputWPIAdapter extends XDigitalInput {

    protected DigitalInput adapter;

    /**
     * Create an instance of a Digital Input class. Creates a digital input given a channel.
     *
     * @param channel
     *            the DIO channel for the digital input 0-9 are on-board, 10-25 are on the MXP
     */
    @Inject
    public DigitalInputWPIAdapter(@Assisted("channel") int channel) {
        adapter = new DigitalInput(channel);
    }

    /**
     * Get the value from a digital input channel. Retrieve the value of a single digital input channel from the FPGA.
     *
     * @return the status of the digital input
     */
    public boolean get() {
        return adapter.get();
    }

    /**
     * Get the channel of the digital input
     *
     * @return The GPIO channel number that this object represents.
     */
    public int getChannel() {
        return adapter.getChannel();
    }
}
