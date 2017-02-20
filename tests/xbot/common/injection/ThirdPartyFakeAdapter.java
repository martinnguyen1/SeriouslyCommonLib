package xbot.common.injection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ThirdPartyFakeAdapter extends ThirdPartyAdapterAbstractClass {

    @Inject
    public ThirdPartyFakeAdapter(@Assisted("index") int index) {
        super(index);
    }

}
