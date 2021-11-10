package info.smart_tools.smartactors.ioc_viewer;

import info.smart_tools.smartactors.ioc_viewer.parser.config.ConfigParser;
import info.smart_tools.smartactors.ioc_viewer.parser.config.ParsedConfigData;
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.IocParser;
import info.smart_tools.smartactors.ioc_viewer.parser.ioc.ParsedIocValue;
import info.smart_tools.smartactors.ioc_viewer.vm.VM;
import info.smart_tools.smartactors.ioc_viewer.vm.VMBreakpoint;
import info.smart_tools.smartactors.ioc_viewer.vm.VMFrame;
import info.smart_tools.smartactors.ioc_viewer.vm.VMMethod;
import info.smart_tools.smartactors.ioc_viewer.vm.VMObject;
import info.smart_tools.smartactors.ioc_viewer.vm.VMThread;
import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

import java.util.ArrayList;
import java.util.List;
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

        VMMethod parseIocMethod = frameObject.getMethod("parseIoc");
        List<VMValue> iocValues = parseIocMethod.invoke(viewerThread, new ArrayList<>(), 0).castTo(List.class);

        List<ParsedIocValue> ioc = iocValues.stream()
                .map(IocParser::parseIocValue)
                .collect(Collectors.toList());

        VMMethod parseConfigMethod = frameObject.getMethod("getConfigSections");
        List<VMValue> configValues = parseConfigMethod.invoke(viewerThread, new ArrayList<>(), 0).castTo(List.class);


        List<ParsedConfigData> configs = configValues.stream()
                .map(ConfigParser::parseConfigData)
                .collect(Collectors.toList());

        System.out.println(ioc.size());
        System.out.println("bonk");

        Thread.sleep(5000);
    }
}
