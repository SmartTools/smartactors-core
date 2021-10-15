package info.smart_tools.smartactors.ioc_viewer.value_formatter;

import info.smart_tools.smartactors.ioc_viewer.vm.VMValue;

public interface IValueFormatter {

    <T> T format(VMValue value);
}
