package info.smart_tools.smartactors.remote_debug_viewer.vm;

import com.sun.jdi.request.BreakpointRequest;

public class VMBreakpoint {

    private final BreakpointRequest breakpoint;

    public VMBreakpoint(final BreakpointRequest breakpoint) {
        this.breakpoint = breakpoint;
    }

    public BreakpointRequest getBreakpoint() {
        return breakpoint;
    }

    public void enable() {
        breakpoint.enable();
    }

    public void disable() {
        breakpoint.disable();
    }
}
