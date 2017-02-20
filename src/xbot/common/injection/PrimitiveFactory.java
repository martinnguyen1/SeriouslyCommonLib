package xbot.common.injection;

import com.google.inject.assistedinject.Assisted;

public interface PrimitiveFactory {

    public ThirdPartyAdapterAbstractClass create(
            @Assisted("index") int index);
}
