package edu.wpi.first.wpilibj;

import java.util.HashMap;

import org.apache.log4j.Logger;

import xbot.common.controls.XPowerDistributionPanel;

public class MockPowerDistributionPanel implements XPowerDistributionPanel {
	private HashMap<Integer, Double> outputCurrents;
	
	private static Logger log = Logger.getLogger(MockPowerDistributionPanel.class);
	
	public MockPowerDistributionPanel() {
		log.info("Creating PDP");
        outputCurrents = new HashMap<>();
	}

	public void setCurrent(int channel, double current) {
	    outputCurrents.put(channel, current);
	}
	
    @Override
    public double getCurrent(int channel) {
        return outputCurrents.getOrDefault(channel, 0d);
    }

}
