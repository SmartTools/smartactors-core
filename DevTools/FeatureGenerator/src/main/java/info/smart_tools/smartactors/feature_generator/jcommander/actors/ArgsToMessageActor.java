package info.smart_tools.smartactors.feature_generator.jcommander.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.feature_generator.Params__MoveArgsToMessage;
import info.smart_tools.smartactors.feature_generator.jcommander.Arguments;

import java.util.HashMap;
import java.util.Map;

public class ArgsToMessageActor extends StatelessActor {

    @Executable
    public Map<String, Object> parseArguments(final Arguments arguments) {
        Map<String, Object> args = new HashMap<>();

        args.put(Params__MoveArgsToMessage.GROUP_ID, arguments.getGroupId());
        args.put(Params__MoveArgsToMessage.PROJECT_PATH, arguments.getProjectPath());
        args.put(Params__MoveArgsToMessage.NAME, arguments.getName());
        args.put(Params__MoveArgsToMessage.VERSION, arguments.getVersion());

        return args;
    }
}
