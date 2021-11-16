package info.smart_tools.smartactors.remote_debug_viewer.parser.ioc;

import info.smart_tools.smartactors.remote_debug_viewer.vm.VMObject;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

import java.util.List;
import java.util.stream.Collectors;

public class IocParser {

    public static ParsedIocValue parseIocValue(final VMValue value) {
        VMObject iocValueObject = value.toObject();

        String iocKey = iocValueObject.getFieldValue("key").castTo(String.class);
        List<VMValue> iocStrategies = iocValueObject.getFieldValue("strategy").castTo(List.class);

        List<ParsedIocStrategy> parsedStrategies = iocStrategies.stream()
                .map(IocParser::parseStrategy)
                .collect(Collectors.toList());

        return new ParsedIocValue(iocKey, parsedStrategies);
    }

    private static ParsedIocStrategy parseStrategy(final VMValue strategyValue) {
        VMObject strategyObject = strategyValue.toObject();

        VMValue moduleValue = strategyObject.getFieldValue("module");
        ParsedIocModule module = parseModule(moduleValue);
        // TODO: temporary cast to string, should be an actual strategy
        String strategyName = strategyObject.getFieldValue("strategy").castTo(String.class);

        return new ParsedIocStrategy(module, strategyName);
    }

    private static ParsedIocModule parseModule(final VMValue moduleValue) {
        VMObject moduleObject = moduleValue.toObject();

        String name = moduleObject.getFieldValue("name").castTo(String.class);
        String version = moduleObject.getFieldValue("version").castTo(String.class);

        return new ParsedIocModule(name, version);
    }
}
