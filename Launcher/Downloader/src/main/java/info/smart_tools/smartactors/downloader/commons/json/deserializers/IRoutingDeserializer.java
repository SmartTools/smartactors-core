package info.smart_tools.smartactors.downloader.commons.json.deserializers;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.RoutingSlip;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class IRoutingDeserializer extends StdDeserializer<IRoutingSlip> {

    public IRoutingDeserializer() {
        this(null);
    }

    public IRoutingDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public IRoutingSlip deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {
        ObjectCodec codec = p.getCodec();
        JsonNode node = codec.readTree(p);

        String name = node.get("name").asText();
        List<IStep> steps = new ArrayList<>();
        if (null != node.get("steps") && node.get("steps").isArray()) {
            Iterator<JsonNode> it = node.get("steps").elements();
            while (it.hasNext()) {
                steps.add(codec.treeToValue(it.next(), IStep.class));
            }
        }

        return new RoutingSlip(name, steps);
    }
}
