package xbot.common.command.scripted;

import org.junit.Ignore;

import edu.wpi.first.wpilibj.command.Command;
import xbot.common.command.BaseCommand;
import xbot.common.command.scripted.ScriptedCommandFactory;
import xbot.common.command.scripted.ScriptedCommandProvider;

@Ignore
public class TestCommandFactory implements ScriptedCommandFactory {
    private ExecutionCounterCommandProvider lastExecutionCounterCommandProvider;
    
    @Override
    public ScriptedCommandProvider getProviderForName(String commandTypeName) {
        if(commandTypeName.equals("CounterCommand"))
            return this.lastExecutionCounterCommandProvider = new ExecutionCounterCommandProvider();
        
        return null;
    }
    
    public ExecutionCounterCommandProvider getLastExecutionCounterCommandProvider() {
        return this.lastExecutionCounterCommandProvider;
    }
    
    @Ignore
    public static class ExecutionCounterCommandProvider implements ScriptedCommandProvider {
        private ExecutionCounterCommand lastCommand;
        
        @Override
        public BaseCommand get(Object[] parameters) {
            lastCommand = new ExecutionCounterCommand();
            
            if(parameters.length >= 1 && parameters[0] instanceof Integer) {
                lastCommand.setExecLimit((Integer)parameters[0]);
            }
            
            return lastCommand;
        }
        
        public ExecutionCounterCommand getLastCommand() {
            // TODO: Replace this 'last' thing with a name param sent in by the script
            return lastCommand;
        }
    }
}
