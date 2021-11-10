package info.smart_tools.smartactors.ioc_viewer.vm;

import com.sun.jdi.Bootstrap;
import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VirtualMachineManager;
import com.sun.jdi.connect.AttachingConnector;
import com.sun.jdi.connect.Connector;
import com.sun.jdi.request.BreakpointRequest;
import com.sun.jdi.request.EventRequestManager;
import info.smart_tools.smartactors.ioc_viewer.common.JDIUtils;

import java.util.Map;

public class VM {

    private VirtualMachine virtualMachine;

    private AttachingConnector connector;

    public VM connectTo(final String hostname, final String port) throws Exception {
        VirtualMachineManager vmm = Bootstrap.virtualMachineManager();
        vmm.attachingConnectors().forEach(it -> {
            if ("dt_socket".equalsIgnoreCase(it.transport().name())) {
                setConnector(it);
            }
        });

        Map<String, Connector.Argument> args = connector.defaultArguments();
        args.get("port").setValue(port);
        args.get("hostname").setValue(hostname);

        this.virtualMachine = connector.attach(args);
        return this;
    }

    public VMThread getThread(final String threadName) {
        ThreadReference threadReference = JDIUtils.findThreadByName(virtualMachine, threadName);

        if (threadReference == null) {
            throw new RuntimeException("Thread \"" + threadName + "\" not found in target VM.");
        }

        return new VMThread(threadName, threadReference);
    }

    public VMBreakpoint createBreakpoint(final VMThread thread, final String frameName) {
        if (!thread.isSuspended()) {
            throw new RuntimeException("Thread \"" + thread.getThreadName() + "\" is not suspended, unable to create breakpoint.");
        }

        EventRequestManager requestManager = virtualMachine.eventRequestManager();
        StackFrame breakpointFrame = JDIUtils.findFrameByName(thread.getThreadReference(), "IocViewerThread");
        if (breakpointFrame == null) {
            throw new RuntimeException("Frame \"" + frameName + "\" not found in thread \"" + thread.getThreadName() + "\".");
        }

        BreakpointRequest breakpoint = requestManager.createBreakpointRequest(breakpointFrame.location());

        return new VMBreakpoint(breakpoint);
    }

    private void setConnector(AttachingConnector connector) {
        this.connector = connector;
    }

    public void disconnect() {
        virtualMachine.dispose();
    }
}
