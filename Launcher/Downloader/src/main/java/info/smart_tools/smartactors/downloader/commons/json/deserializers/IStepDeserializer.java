package info.smart_tools.smartactors.downloader.commons.json.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.Step;

import java.io.IOException;

public class IStepDeserializer extends StdDeserializer<IStep> {

    public IStepDeserializer() {
        this(null);
    }

    public IStepDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IStep deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        String actorName = node.get("actorName").asText();
        String methodName = node.get("methodName").asText();

        MethodParameters methodParameters = codec.treeToValue(node.get("methodParameters"), MethodParameters.class);

        return new Step(actorName, methodName, methodParameters);
    }
}
