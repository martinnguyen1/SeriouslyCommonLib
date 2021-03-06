package xbot.common.logic;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Observable;
import java.util.Observer;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class LatchTest {

    private LatchTestObserver latchTestObserver;

    @Before
    public void setUp() {
        latchTestObserver = new LatchTestObserver();
    }

    @After
    public void tearDown() {
        latchTestObserver = null;
    }

    @Test
    public void testRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
    }

    @Test
    public void testFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
    }

    @Test
    public void testRiseFallRiseObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseRiseFallRiseFallObservingRisingEdge() {
        Latch latch = new Latch(false, Latch.EdgeType.RisingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        latch.setValue(false);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testFallRiseRiseFallRiseFallObservingFallingEdge() {
        Latch latch = new Latch(true, Latch.EdgeType.FallingEdge);
        latch.addObserver(latchTestObserver);
        verifyEdgeType(null);
        latch.setValue(true);
        verifyEdgeType(null);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(2);
    }

    @Test
    public void testRiseFallRiseObservingBothEdges() {
        Latch latch = new Latch(true, Latch.EdgeType.Both);
        latch.addObserver(latchTestObserver);
        verifyTimesUpdateWasCalled(0);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(1);
        latch.setValue(true);
        verifyEdgeType(Latch.EdgeType.RisingEdge);
        verifyTimesUpdateWasCalled(2);
        latch.setValue(false);
        verifyEdgeType(Latch.EdgeType.FallingEdge);
        verifyTimesUpdateWasCalled(3);
    }

    private void verifyTimesUpdateWasCalled(int expected) {
        assertEquals(expected, latchTestObserver.getNumTimesUpdated());
    }

    private void verifyEdgeType(Latch.EdgeType expected) {
        assertEquals(expected, latchTestObserver.getLastUpdateEdgeType());
    }
}

class LatchTestObserver implements Observer {

    private Latch.EdgeType lastUpdateEdgeType = null;
    private int numTimesUpdated = 0;

    public Latch.EdgeType getLastUpdateEdgeType() {
        return lastUpdateEdgeType;
    }

    public int getNumTimesUpdated() {
        return numTimesUpdated;
    }

    @Override
    public void update(Observable o, Object arg) {
        assertTrue("Latch is the only class that should be observed", o instanceof Latch);
        assertTrue("Latch must provide an argument of type EdgeType", arg instanceof Latch.EdgeType);
        numTimesUpdated++;
        lastUpdateEdgeType = (Latch.EdgeType) arg;
    }
}
