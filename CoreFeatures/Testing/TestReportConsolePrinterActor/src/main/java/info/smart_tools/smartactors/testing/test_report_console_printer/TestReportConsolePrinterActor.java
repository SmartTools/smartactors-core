package info.smart_tools.smartactors.testing.test_report_console_printer;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.test_report_console_printer.exception.ConsolePrintReportActorException;
import info.smart_tools.smartactors.testing.test_report_console_printer.wrapper.PrintWrapper;

/**
 * Dummy console printer.
 */
public class TestReportConsolePrinterActor {
    private final IField name;
    private final IField body;

    public TestReportConsolePrinterActor() throws ResolutionException {
        name = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "name");
        body = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()), "body");
    }

    public void print(PrintWrapper wrapper) throws ConsolePrintReportActorException {
        try {
            System.out.println(
                    String.format("Test report %s\n%s",
                            name.in(wrapper.getReport()),
                            body.in(wrapper.getReport())));
        } catch (ReadValueException | InvalidArgumentException e) {
            throw new ConsolePrintReportActorException("Can't read values from wrapper: " + e.getMessage(), e);
        }
    }
}
