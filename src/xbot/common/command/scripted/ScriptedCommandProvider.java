package xbot.common.command.scripted;

import edu.wpi.first.wpilibj.command.Command;


/**
 * An interface which describes a provider which translates configuration
 * options into a Command instance.
 *
 */
public interface ScriptedCommandProvider {
    public Command get(Object[] parameters);
}