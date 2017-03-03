package xbot.common.math;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class FieldPointTest {

    @Test
    public void lineWith1SlopeAnd0Intercept() {
        FieldPoint l = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(45));
        
        assertEquals(1, l.getSlope(), 0.001);
        assertEquals(0, l.getIntercept(), 0.001);
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(-1, 1));
        assertEquals(Math.sqrt(2), distance, 0.001);
    }
    
    @Test
    public void zeroSlope() {
        FieldPoint l = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(0));
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(0, 1));
        
        assertEquals(1, distance, 0.001);
    }
    
    @Test
    public void negativeSlope() {
        FieldPoint l = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(135));
        
        assertEquals(-1, l.getSlope(), 0.001);
        assertEquals(0, l.getIntercept(), 0.001);
        
        double distance = l.getDistanceToLineFromPoint(new XYPair(1, 1));
        assertEquals(Math.sqrt(2), distance, 0.001);
    }
    
    @Test
    public void testSlopeToDegrees() {
        FieldPoint fp = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(45));
        assertEquals(45, fp.getHeading().getValue(), 0.001);
        
        FieldPoint fp90 = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(90));
        assertEquals(90, fp90.getHeading().getValue(), 0.001);
        
        FieldPoint fp135 = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(135));
        assertEquals(135, fp135.getHeading().getValue(), 0.001);
    }
    
    @Test
    public void testSimpleYDisplacement() {
        FieldPoint fp = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(45));
        FieldPoint currentPosition = new FieldPoint(new XYPair(-1, 1), new ContiguousHeading(135));
        double y = fp.getPointRelativeYDisplacementFromLine(currentPosition);
        
        assertEquals(-Math.sqrt(2), y, 0.001);
        
        fp = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(90));
        currentPosition = new FieldPoint(new XYPair(10, 0), new ContiguousHeading(0));
        y = fp.getPointRelativeYDisplacementFromLine(currentPosition);
        
        assertEquals(-10, y, 0.001);
        
        currentPosition = new FieldPoint(new XYPair(10, 0), new ContiguousHeading(180));
        y = fp.getPointRelativeYDisplacementFromLine(currentPosition);
        
        assertEquals(10, y, 0.001);
    }
    
    @Test
    public void testDegenerateYDisplacement() {
        FieldPoint fp = new FieldPoint(new XYPair(0, 0), new ContiguousHeading(90));
        FieldPoint currentPosition = new FieldPoint(new XYPair(10, 0), new ContiguousHeading(90));
        double y = fp.getPointRelativeYDisplacementFromLine(currentPosition);
        
        assertEquals(0, y, 0.001);
    }
}
