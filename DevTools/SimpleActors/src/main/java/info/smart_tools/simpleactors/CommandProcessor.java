package info.smart_tools.simpleactors;

import com.fasterxml.jackson.databind.ObjectMapper;
import info.smart_tools.simpleactors.commons.CommonParameters;
import info.smart_tools.simpleactors.commons.IActor;
import info.smart_tools.simpleactors.commons.IArguments;
import info.smart_tools.simpleactors.commons.IMessage;
import info.smart_tools.simpleactors.commons.IRoutingSlip;
import info.smart_tools.simpleactors.commons.IStep;
import info.smart_tools.simpleactors.commons.Message;
import info.smart_tools.simpleactors.commons.exception.ProcessExecutionException;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Processes commands from CLI with requested routing slip
 */
public class CommandProcessor {

    private final Map<String, IRoutingSlip> slips = new HashMap<>();

    private final ObjectMapper objectMapper;
    private final Map<String, IActor> actors;

    /**
     * Create new instance of {@link CommandProcessor} and load all routing slips from {@code src/resources/routing_slips}
     *
     * @param mapper JSON mapper used for loading routing slips
     * @param actors actors that were registered during starter initialization
     */
    public CommandProcessor(final String toolName, final ObjectMapper mapper, final Map<String, IActor> actors) {
        this.objectMapper = mapper;
        this.actors = actors;

        URL url = this.getClass().getClassLoader().getResource(toolName + "_routing_slips");
        try (
                FileSystem zipfs = initFileSystem(url.toURI());
                Stream<Path> paths = Files.walk(Paths.get(url.toURI()))
        ) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(
                            f -> {
                                try {
                                    this.slips.put(
                                            f.getFileName().toString().split("\\.")[0],
                                            this.objectMapper.readValue(Files.newInputStream(f), IRoutingSlip.class)
                                    );
                                } catch (Exception e) {
                                    throw new RuntimeException("Could not parse json file - " + f.getFileName(), e);
                                }
                            }
                    );
        } catch (Exception e) {
            throw new RuntimeException("Could not read routing slips.", e);
        }
    }

    /**
     * Process provided arguments with requested routing slip
     *
     * @param arguments arguments from CLi
     * @throws ProcessExecutionException if failed to load routing slip or actor
     */
    public void process(final IArguments arguments) throws ProcessExecutionException {
        IRoutingSlip slip = this.slips.get(arguments.getCommand());
        IMessage message = Message
                .builder()
                .add(CommonParameters.ARGUMENTS, arguments)
                .add(CommonParameters.SLIP_STORAGE, this.slips)
                .build();
        if (null != slip) {
            message.put(CommonParameters.SLIP, slip);
            while (slip.hasNext()) {
                IStep step = slip.next();
                IActor actor = this.actors.get(step.getActorName());
                if (null != actor) {
                    message.put(CommonParameters.METHOD_PARAMS, step.getMethodParameters());
                    actor.execute(step.getMethodName(), message);
                } else {
                    throw new ProcessExecutionException(
                            String.format(
                                    "Actor \"%s\" not found in routing slip \"%s\".",
                                    step.getActorName(), slip.getName()
                            )
                    );
                }
            }
        } else {
            throw new ProcessExecutionException(String.format("Routing slip \"%s\" not found.", arguments.getCommand()));
        }
    }

    private FileSystem initFileSystem(URI uri) throws IOException {
        return FileSystems.newFileSystem(uri, Collections.emptyMap(), this.getClass().getClassLoader());
    }
}
