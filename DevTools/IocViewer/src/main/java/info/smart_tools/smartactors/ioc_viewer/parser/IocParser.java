package info.smart_tools.smartactors.ioc_viewer.parser;

import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.List;
import java.util.stream.Collectors;

public class IocParser {

    public static ParsedIocValue parseIocValue(final VMValue value) {
        VMObject iocValueObject = value.toObject();

        String iocKey = iocValueObject.getFieldValue("key").castTo(String.class);
        List<VMValue> iocDependencies = iocValueObject.getFieldValue("dependencies").castTo(List.class);

        List<ParsedIocDependency> parsedDependencies = iocDependencies.stream()
                .map(IocParser::parseDependency)
                .collect(Collectors.toList());

        return new ParsedIocValue(iocKey, parsedDependencies);
    }

    private static ParsedIocDependency parseDependency(final VMValue dependencyValue) {
        VMObject dependencyObject = dependencyValue.toObject();

        String dependencyName = dependencyObject.getFieldValue("name").castTo(String.class);
        String dependencyVersion = dependencyObject.getFieldValue("version").castTo(String.class);

        return new ParsedIocDependency(dependencyName, dependencyVersion, null);
    }
}
