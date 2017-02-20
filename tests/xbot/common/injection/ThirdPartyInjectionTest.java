package xbot.common.injection;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ThirdPartyInjectionTest extends BaseWPITest {

    @Test
    public void getThirdParty() {
        ThirdPartyAdapterAbstractClassFactory logic = 
                injector.getInstance(ThirdPartyAdapterAbstractClassFactory.class);
        
        ThirdPartyAdapterAbstractClass tpaac = logic.create(5);
        
        assertTrue(tpaac.getIndex() == 5);
    }
}
