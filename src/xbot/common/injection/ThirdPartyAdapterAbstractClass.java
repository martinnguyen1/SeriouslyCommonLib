package xbot.common.injection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public abstract class ThirdPartyAdapterAbstractClass {

    protected int index;
    
    @Inject
    public ThirdPartyAdapterAbstractClass(@Assisted("index") int index) {
        this.index = index;
    }
    
    public int getIndex() {
        return index;
    }
}
