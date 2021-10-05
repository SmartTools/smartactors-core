package info.smart_tools.smartactors.builder.jcommander.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.builder.Params__MoveArgsToMessage;
import info.smart_tools.smartactors.builder.jcommander.Arguments;

import java.util.HashMap;
import java.util.Map;

public class ArgsToMessageActor extends StatelessActor {

    @Executable
    public Map<String, Object> moveArgsToMessage(final Arguments arguments) {
        Map<String, Object> args = new HashMap<>();

        args.put(Params__MoveArgsToMessage.PATH_TO_FEATURE, arguments.getFeaturePath());

        return args;
    }
}
