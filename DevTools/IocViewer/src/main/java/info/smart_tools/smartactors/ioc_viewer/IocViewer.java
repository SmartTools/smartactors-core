package info.smart_tools.smartactors.ioc_viewer;

import info.smart_tools.smartactors.ioc_viewer.common.MapNode;
import info.smart_tools.smartactors.ioc_viewer.common.MapUtils;
import info.smart_tools.smartactors.ioc_viewer.parser.IocDependency;
import info.smart_tools.smartactors.ioc_viewer.parser.IocParser;
import info.smart_tools.smartactors.ioc_viewer.vm.VM;
import info.smart_tools.smartactors.ioc_viewer.vm.VMBreakpoint;
import info.smart_tools.smartactors.ioc_viewer.vm.VMFrame;
import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMThread;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class IocViewer {

    public static void main(String[] args) throws Exception {
        VM vm = new VM().connectTo("localhost", "5001");

        VMThread viewerThread = vm.getThread("ioc-viewer-thread");
        viewerThread.suspend();

        VMBreakpoint breakpoint = vm.createBreakpoint(viewerThread, "IocViewerThread");
        viewerThread.resume();
        breakpoint.enable();

        while (!viewerThread.isRunning()) {
            System.out.println("Waiting for thread \"" + viewerThread.getThreadName() + "\" to be runnable");
        }

        VMFrame frame = viewerThread.getFrameByName("IocViewerThread");
        VMObject frameObject = frame.thisObject();

        VMObject strategyStorage = frameObject.getFieldValue("container").toObject();

        VMValue storageHashMap = strategyStorage.getFieldValue("strategyStorage");
        VMObject hashMap = storageHashMap.toObject();
        VMValue tableValue = hashMap.getFieldValue("table");
        List<VMValue> table = tableValue.castTo(List.class);

        // TODO: some dependencies from IOC are missing
        Map<String, List<IocDependency>> ioc = table.stream()
                .map(it -> MapUtils.getEntryFromMap(it, viewerThread))
                .map(IocParser::parseDependencyData)
                .collect(Collectors.toMap(MapNode::getKey, MapNode::getValue));

        System.out.println("bonk");
    }
}
