package xbot.common.injection;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

public class ThirdPartyRealAdapter extends ThirdPartyAdapterAbstractClass {
    
    private ThirdPartyClass mine;
    // holds a real class internally, delegates methods.
    
    @Inject
    public ThirdPartyRealAdapter(
            @Assisted("index") int index) {
        super(index);
        mine = new ThirdPartyClass(index);
    }
}