package xbot.common.injection;

import com.google.inject.assistedinject.Assisted;

public interface ThirdPartyAdapterAbstractClassFactory {

    public ThirdPartyAdapterAbstractClass create(
            @Assisted("index") int index);
}
