package info.smart_tools.smartactors.remote_debug_viewer.parser.chain;

import info.smart_tools.smartactors.remote_debug_viewer.vm.VMObject;
import info.smart_tools.smartactors.remote_debug_viewer.vm.VMValue;

public class ChainParser {
    public static ParsedChainData parseChainData(final VMValue chainValue) {
        VMObject chainObject = chainValue.toObject();

        String chainName = chainObject.getFieldValue("name").castTo(String.class);

        return new ParsedChainData(chainName);
    }
}
