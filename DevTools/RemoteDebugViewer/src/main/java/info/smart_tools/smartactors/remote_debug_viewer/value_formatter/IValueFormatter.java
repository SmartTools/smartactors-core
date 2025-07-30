package info.smart_tools.smartactors.remote_debug_viewer.value_formatter;

import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

public interface IValueFormatter {

    <T> T format(VMValue value);
}
