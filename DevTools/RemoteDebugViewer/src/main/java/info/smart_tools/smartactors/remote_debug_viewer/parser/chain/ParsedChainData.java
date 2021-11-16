package info.smart_tools.smartactors.remote_debug_viewer.parser.chain;

public class ParsedChainData {

    private final String name;

    public ParsedChainData(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "ParsedChainData{" +
                "name='" + name + '\'' +
                '}';
    }
}
