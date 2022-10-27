package info.smart_tools.smartactors.uploader.jcommander.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.uploader.Params__MoveArgsToMessage;
import info.smart_tools.smartactors.uploader.jcommander.Arguments;

import java.util.HashMap;
import java.util.Map;

public class ArgsToMessageActor extends StatelessActor {

    @Executable
    public Map<String, Object> parseArguments(final Arguments arguments) {
        Map<String, Object> parsedArgs = new HashMap<>();
        parsedArgs.put(Params__MoveArgsToMessage.FEATURE_PATH, arguments.getFeaturePath());
        parsedArgs.put(Params__MoveArgsToMessage.USERNAME, arguments.getUsername());
        parsedArgs.put(Params__MoveArgsToMessage.PASSWORD, arguments.getPassword());
        parsedArgs.put(Params__MoveArgsToMessage.REPOSITORY_ID, arguments.getRepositoryId());
        parsedArgs.put(Params__MoveArgsToMessage.REPOSITORY_URL, arguments.getRepositoryUrl());
        return parsedArgs;
    }
}
