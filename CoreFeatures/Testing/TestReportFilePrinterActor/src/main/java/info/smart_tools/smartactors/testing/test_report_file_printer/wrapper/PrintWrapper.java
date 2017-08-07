package info.smart_tools.smartactors.testing.test_report_file_printer.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface PrintWrapper {
    IObject getReport() throws ReadValueException;
}
