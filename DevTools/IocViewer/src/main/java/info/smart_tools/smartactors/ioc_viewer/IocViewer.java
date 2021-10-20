package info.smart_tools.smartactors.ioc_viewer;

import info.smart_tools.smartactors.ioc_viewer.common.MapNode;
import info.smart_tools.smartactors.ioc_viewer.common.MapUtils;
import info.smart_tools.smartactors.ioc_viewer.parser.ParsedIocDependency;
import info.smart_tools.smartactors.ioc_viewer.parser.IocParser;
import info.smart_tools.smartactors.ioc_viewer.parser.ParsedIocValue;
import info.smart_tools.smartactors.ioc_viewer.vm.VM;
import info.smart_tools.smartactors.ioc_viewer.vm.VMBreakpoint;
import info.smart_tools.smartactors.ioc_viewer.vm.VMFrame;
import info.smart_tools.smartactors.ioc_viewer.vm.VMMethod;
import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMThread;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.ArrayList;
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

        do {
            breakpoint.enable();
            while (!viewerThread.isRunning()) {
                System.out.println("Waiting for thread \"" + viewerThread.getThreadName() + "\" to be runnable");
            }

            VMFrame frame = viewerThread.getFrameByName("IocViewerThread");
            VMObject frameObject = frame.thisObject();

            VMObject strategyStorage = frameObject.getFieldValue("container").toObject();

            VMMethod parseIocMethod = frameObject.getMethod("parseIoc");
            List<VMValue> iocValues = parseIocMethod.invoke(viewerThread, new ArrayList<>(), 0).castTo(List.class);

            // TODO: some dependencies from IOC are missing
            List<ParsedIocValue> ioc = iocValues.stream()
                    .map(IocParser::parseIocValue)
                    .collect(Collectors.toList());

            System.out.println(ioc.size());
            System.out.println("bonk");

            breakpoint.disable();
            viewerThread.resume();

            Thread.sleep(5000);
        } while (true);
    }
}
