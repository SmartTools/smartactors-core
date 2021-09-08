package info.smart_tools.smartactors.downloader.jcommander.actors;

import info.smart_tools.smartactors.downloader.Params__MoveArgsToMessage;
import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;
import info.smart_tools.smartactors.downloader.jcommander.Args;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ArgsToMessageActor extends StatelessActor {

    @Executable()
    public Map moveArgsToMessage(final Args arguments) {
        Map<String, Object> response = new HashMap<>();
        response.put(Params__MoveArgsToMessage.FEATURE_DOWNLOAD_DIRECTORY, new File(arguments.getDestination()));
        response.put(Params__MoveArgsToMessage.FEATURE_DOWNLOAD_DIRECTORY_PATH, new File(arguments.getDestination()).toPath());
        response.put(Params__MoveArgsToMessage.SOURCE_FILE, Paths.get(arguments.getFileName()));
        response.put(Params__MoveArgsToMessage.DOWNLOAD_DEPENDENCIES, arguments.isWithDependencies());
        response.put(Params__MoveArgsToMessage.RECURSIVE_DOWNLOADING, arguments.isRecursiveDownloading());

        return response;
    }
}
