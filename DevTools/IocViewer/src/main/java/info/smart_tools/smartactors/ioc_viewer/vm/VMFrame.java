package info.smart_tools.smartactors.ioc_viewer.vm;

import com.sun.jdi.ObjectReference;
import com.sun.jdi.StackFrame;

public class VMFrame {

    private final StackFrame frame;
    private final String frameName;

    public VMFrame(final StackFrame frame, final String frameName) {
        this.frame = frame;
        this.frameName = frameName;
    }

    public VMObject thisObject() {
        ObjectReference object = frame.thisObject();
        if (object == null) {
            throw new RuntimeException("Failed to obtain object for frame \"" + frameName + "\".");
        }

        return new VMObject(object, object.referenceType());
    }

    public StackFrame getFrame() {
        return frame;
    }

    public String getFrameName() {
        return frameName;
    }
}
