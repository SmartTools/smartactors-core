package info.smart_tools.smartactors.dumpable_interface.idumpable;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.dumpable_interface.idumpable.exceptions.DumpException;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Interface for objects that can create a serializable copy (dump) of their internal state.
 */
public interface IDumpable {
    /**
     * Create serializable copy of this object's state.
     *
     * @param options options of
     * @return the copy of internal state of this object
     * @throws DumpException if any error occurs
     * @throws InvalidArgumentException if {@code options} argument is {@code null} or contains invalid values
     */
    IObject dump(IObject options) throws DumpException, InvalidArgumentException;
}
