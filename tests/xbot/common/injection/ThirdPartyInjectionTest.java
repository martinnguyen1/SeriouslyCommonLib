package xbot.common.injection;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ThirdPartyInjectionTest extends BaseWPITest {

    @Test
    public void getThirdParty() {
        AdvancedFactory advf = injector.getInstance(AdvancedFactory.class);
        
        XbotLogicClass xlc = advf.createLogicClass(5);
        
        assertTrue(xlc.tpaac.getIndex() == 5);
    }
}
