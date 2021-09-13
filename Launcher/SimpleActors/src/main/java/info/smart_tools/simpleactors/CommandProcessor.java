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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class CommandProcessor {

    private final Map<String, IRoutingSlip> slips = new HashMap<>();

    private final ObjectMapper objectMapper;
    private final Map<String, IActor> actors;

    public CommandProcessor(final ObjectMapper mapper, final Map<String, IActor> actors) {
        this.objectMapper = mapper;
        this.actors = actors;

        try (
                Stream<Path> paths = Files
                        .walk(Paths.get(this.getClass().getClassLoader().getResource("routing_slips").toURI()))
        ) {
            paths
                    .filter(Files::isRegularFile)
                    .forEach(
                            f -> {
                                try {
                                    this.slips.put(
                                            f.toFile().getName().split("\\.")[0],
                                            this.objectMapper.readValue(f.toFile(), IRoutingSlip.class)
                                    );
                                } catch (Exception e) {
                                    throw new RuntimeException("Could not parse json file - " + f.getFileName(), e);
                                }
                            }
                    );
        } catch (Exception e) {
            System.err.println("Could not read routing slips.");
            e.printStackTrace();
        }
    }

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
}
