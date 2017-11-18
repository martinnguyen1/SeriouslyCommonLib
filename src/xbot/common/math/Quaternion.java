package xbot.common.math;

public class Quaternion {
    public final float w, x, y, z;
    
    public Quaternion(float w, float x, float y, float z) {
        this.w = w;
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public Quaternion() {
        this(1, 0, 0, 0);
    }
}
