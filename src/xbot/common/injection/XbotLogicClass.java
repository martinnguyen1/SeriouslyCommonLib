package xbot.common.injection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class XbotLogicClass {

    public ThirdPartyAdapterAbstractClass tpaac;
    
    @Inject
    public XbotLogicClass(PrimitiveFactory primitiveFactory, @Assisted("index") int index) {
        tpaac = primitiveFactory.create(index);
    }
}
