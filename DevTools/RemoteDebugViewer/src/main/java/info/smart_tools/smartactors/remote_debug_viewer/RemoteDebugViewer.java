package info.smart_tools.smartactors.remote_debug_viewer;

import info.smart_tools.smartactors.remote_debug_viewer.parser.chain.ChainParser;
import info.smart_tools.smartactors.remote_debug_viewer.parser.chain.ParsedChainData;
import info.smart_tools.smartactors.remote_debug_viewer.parser.config.ConfigParser;
import info.smart_tools.smartactors.remote_debug_viewer.parser.config.ParsedConfigData;
import info.smart_tools.smartactors.remote_debug_viewer.parser.ioc.IocParser;
import info.smart_tools.smartactors.remote_debug_viewer.parser.ioc.ParsedIocValue;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VM;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMBreakpoint;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMFrame;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMMethod;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMObject;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMThread;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteDebugViewer {

    public static void main(String[] args) throws Exception {
        VM vm = new VM().connectTo("localhost", "5001");

        VMThread viewerThread = vm.getThread("remote-debug-thread");
        viewerThread.suspend();

        VMBreakpoint breakpoint = vm.createBreakpoint(viewerThread, "RemoteDebugThread");
        viewerThread.resume();

        breakpoint.enable();
        while (!viewerThread.isRunning()) {
            System.out.println("Waiting for thread \"" + viewerThread.getThreadName() + "\" to be runnable");
        }

        VMFrame frame = viewerThread.getFrameByName("RemoteDebugThread");
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

        VMMethod parseChainsMethod = frameObject.getMethod("getChainData");
        List<VMValue> chainValues = parseChainsMethod.invoke(viewerThread, new ArrayList<>(), 0).castTo(List.class);

        List<ParsedChainData> chains = chainValues.stream()
                .map(ChainParser::parseChainData)
                .collect(Collectors.toList());

        System.out.println(ioc.size());
        System.out.println("bonk");

        Thread.sleep(5000);
    }
}
