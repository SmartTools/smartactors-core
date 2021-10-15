package info.smart_tools.smartactors.ioc_viewer.common;

import info.smart_tools.smartactors.ioc_viewer.vm.VMMethod;
import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMThread;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.ArrayList;

public class MapUtils {

    public static MapNode<VMValue> getEntryFromMap(final VMValue node, final VMThread thread) {
        try {
            VMObject object = node.toObject();

            VMValue nodeKey = object.getFieldValue("key");
            VMValue nodeValue = object.getFieldValue("val");
            VMMethod method = nodeKey.toObject().getMethod("toString");

            VMValue stringValue = method.invoke(thread, new ArrayList<>(), 0);

            String key = stringValue.castTo(String.class);
            return new MapNode<>(key, nodeValue);
        } catch (Exception e) {
            throw new RuntimeException("Failed to get entry " + node.getValue() + " from map.", e);
        }
    }
}
