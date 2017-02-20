package xbot.common.injection;

import com.google.inject.Inject;

public class XbotLogicClass {

    @Inject
    public XbotLogicClass(ThirdPartyAdapterAbstractClassFactory thirdAdapterFactory) {
        thirdAdapterFactory.create(5);
    }
}
