package info.smart_tools.simpleactors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import info.smart_tools.simpleactors.commons.IActor;
import info.smart_tools.simpleactors.commons.IArguments;
import info.smart_tools.simpleactors.commons.IRoutingSlip;
import info.smart_tools.simpleactors.commons.IStep;
import info.smart_tools.simpleactors.commons.exception.ProcessExecutionException;
import info.smart_tools.simpleactors.commons.exception.SimpleActorsStartException;
import info.smart_tools.simpleactors.commons.json.deserializers.IRoutingDeserializer;
import info.smart_tools.simpleactors.commons.json.deserializers.IStepDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Starter for SimpleActors
 */
public class SimpleActorsStarter {

    private final static ObjectMapper mapper;

    private final Map<String, IActor> actors = new HashMap<>();

    static {
        mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(IRoutingSlip.class, new IRoutingDeserializer());
        module.addDeserializer(IStep.class, new IStepDeserializer());
        mapper.registerModule(module);
    }

    /**
     * Register new actor in SimpleActors
     *
     * @param name name of the actor used in routing slips
     * @param actor instance of actor
     * @return current instance of {@link SimpleActorsStarter}
     */
    public SimpleActorsStarter addActor(final String name, final IActor actor) {
        this.actors.put(name, actor);
        return this;
    }

    /**
     * Start processing arguments from CLI
     *
     * @param arguments CLI arguments
     * @throws SimpleActorsStartException if failed to start SimpleActors instance
     */
    public void start(final IArguments arguments) throws SimpleActorsStartException {
        try {
            CommandProcessor cp = new CommandProcessor(mapper, actors);
            cp.process(arguments);
        } catch (ProcessExecutionException e) {
            throw new SimpleActorsStartException("Failed to start command processor", e);
        }
    }
}
