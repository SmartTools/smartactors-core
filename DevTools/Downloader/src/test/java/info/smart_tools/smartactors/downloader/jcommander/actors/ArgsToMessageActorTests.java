package info.smart_tools.smartactors.downloader.jcommander.actors;

import info.smart_tools.smartactors.downloader.Params__MoveArgsToMessage;
import info.smart_tools.smartactors.downloader.jcommander.Args;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ArgsToMessageActorTests {

    public static final String COMMAND = "command";
    public static final String DESTINATION = "destination";
    public static final String TYPE = "type";
    public static final String FILENAME = "filename";

    @Test
    public void creating() {
        ArgsToMessageActor actor = new ArgsToMessageActor();
        assertNotNull(actor);
        Args args = new Args();
        args.setCommand(COMMAND);
        args.setDestination(DESTINATION);
        args.setFeatureType(TYPE);
        args.setFileName(FILENAME);
        args.setHelp(true);
        args.setRecursiveDownloading(true);
        args.setWithDependencies(true);
        Map<String, Object> result = actor.moveArgsToMessage(args);
        assertTrue((boolean) result.get(Params__MoveArgsToMessage.DOWNLOAD_DEPENDENCIES));
        assertTrue((boolean) result.get(Params__MoveArgsToMessage.RECURSIVE_DOWNLOADING));
        assertEquals(FILENAME, ((Path) result.get(Params__MoveArgsToMessage.SOURCE_FILE)).toString());
        assertEquals(DESTINATION, ((File) result.get(Params__MoveArgsToMessage.FEATURE_DOWNLOAD_DIRECTORY)).getName());

        //
        assertEquals(COMMAND, args.getCommand());
        assertEquals(TYPE, args.getFeatureType());
        assertTrue(args.isHelp());
    }
}
