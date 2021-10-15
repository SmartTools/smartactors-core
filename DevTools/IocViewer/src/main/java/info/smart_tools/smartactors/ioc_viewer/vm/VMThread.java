package info.smart_tools.smartactors.ioc_viewer.vm;

import com.sun.jdi.StackFrame;
import com.sun.jdi.ThreadReference;
import info.smart_tools.smartactors.ioc_viewer.common.JDIUtils;

public class VMThread {

    private final String threadName;
    private final ThreadReference threadReference;

    public VMThread(final String threadName, final ThreadReference threadReference) {
        this.threadName = threadName;
        this.threadReference = threadReference;
    }

    public String getThreadName() {
        return threadName;
    }

    public ThreadReference getThreadReference() {
        return threadReference;
    }

    public VMFrame getFrameByName(final String frameName) {
        StackFrame frame = JDIUtils.findFrameByName(threadReference, frameName);
        if (frame == null) {
            throw new RuntimeException("Failed to obtain frame \"" + frameName + "\".");
        }

        return new VMFrame(frame, frameName);
    }

    public Boolean isRunning() {
        return threadReference.status() == ThreadReference.THREAD_STATUS_RUNNING;
    }

    public Boolean isSuspended() {
        return threadReference.status() == ThreadReference.THREAD_STATUS_SLEEPING;
    }

    public void suspend() {
        threadReference.suspend();
    }

    public void resume() {
        threadReference.resume();
    }
}
