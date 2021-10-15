package info.smart_tools.smartactors.ioc_viewer.parser;

import info.smart_tools.smartactors.ioc_viewer.common.MapNode;
import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.List;
import java.util.stream.Collectors;

public class IocParser {

    public static MapNode<List<IocDependency>> parseDependencyData(final MapNode<VMValue> node) {
        VMValue depValue = node.getValue();
        VMObject depHashMap = depValue.toObject();
        VMValue depTableValue = depHashMap.getFieldValue("table");
        List<VMValue> depTable = depTableValue.castTo(List.class);

        List<IocDependency> dependencies = depTable.stream()
                .map(IocParser::mapDependency)
                .collect(Collectors.toList());

        return new MapNode<>(node.getKey(), dependencies);
    }

    private static IocDependency mapDependency(final VMValue value) {
        try {
            VMObject object = value.toObject();

            VMValue keyModule = object.getFieldValue("key");
            VMValue valueStrategy = object.getFieldValue("val");
            VMObject objectModule = keyModule.toObject();

            VMValue valueName = objectModule.getFieldValue("name");
            VMValue valueVersion = objectModule.getFieldValue("version");

            return new IocDependency(
                    valueName.castTo(String.class),
                    valueVersion.castTo(String.class),
                    valueStrategy.getValue()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse " + value.getValue() + " as IOC dependency.", e);
        }
    }
}
