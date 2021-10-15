package info.smart_tools.smartactors.ioc_viewer.common;

import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import com.sun.jdi.VirtualMachine;

public final class JDIUtils {

    public static ThreadReference findThreadByName(final VirtualMachine vm, final String name) {
        return vm.allThreads().stream()
                .filter(it -> it.name().equals(name))
                .findFirst()
                .orElse(null);
    }

    public static StackFrame findFrameByName(final ThreadReference threadReference, final String name) {
        try {
            return threadReference.frames().stream()
                    .filter(it -> it.location().declaringType().name().contains(name))
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
