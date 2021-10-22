package info.smart_tools.smartactors.ioc_viewer.parser;

public class ParsedIocStrategy {

    private final ParsedIocModule module;
    private final Object dependency;

    public ParsedIocStrategy(final ParsedIocModule module, final Object dependency) {
        this.module = module;
        this.dependency = dependency;
    }

    public ParsedIocModule getModule() {
        return module;
    }

    public Object getDependency() {
        return dependency;
    }
}
