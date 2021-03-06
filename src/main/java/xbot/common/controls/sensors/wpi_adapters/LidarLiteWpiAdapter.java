package xbot.common.controls.sensors.wpi_adapters;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import edu.wpi.first.wpilibj.I2C;
import edu.wpi.first.wpilibj.I2C.Port;
import xbot.common.controls.sensors.XLidarLite;
import xbot.common.properties.XPropertyManager;
import edu.wpi.first.wpilibj.Timer;

public class LidarLiteWpiAdapter extends XLidarLite{

    private I2C i2c;
    
    @Inject
    public LidarLiteWpiAdapter(@Assisted("port") Port port, XPropertyManager propMan) {
        super(port, propMan);

      i2c = new I2C(port, lidar_address);
    }

    @Override
    // Update distance variable
    public void update() {
        i2c.write(lidar_config_register, 0x04); // Initiate measurement
        Timer.delay(0.04); // Delay for measurement to be taken
        i2c.read(lidar_distance_register, 2, distance); // Read in measurement
        Timer.delay(0.01); // Delay to prevent over polling
    }

}
