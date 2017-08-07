package info.smart_tools.smartactors.testing.test_report_text_builder.wrapper;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

public interface TestSuiteWrapper {
    IObject getTestSuite() throws ReadValueException;

    void setReport(IObject report) throws ChangeValueException;
}
