package info.smart_tools.smartactors.dumpable_interface.idumpable.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable#dump(IObject)} when error occurs creating
 * dump of the object.
 */
public class DumpException extends Exception {
    /**
     * The constructor.
     *
     * @param message    the message
     * @param cause      the cause
     */
    public DumpException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
