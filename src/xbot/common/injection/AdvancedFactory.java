package xbot.common.injection;

import com.google.inject.assistedinject.Assisted;

public interface AdvancedFactory {

    public XbotLogicClass createLogicClass(
            @Assisted("index") int index);
}
