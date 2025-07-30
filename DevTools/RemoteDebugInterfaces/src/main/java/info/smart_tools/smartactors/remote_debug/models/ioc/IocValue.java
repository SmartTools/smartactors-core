package info.smart_tools.smartactors.remote_debug.models.ioc;

import java.util.List;

public class IocValue {

    private final String key;
    private final IocStrategy[] strategy;

    public IocValue(final String key, final List<IocStrategy> strategy) {
        this.key = key;
        this.strategy = strategy.toArray(new IocStrategy[0]);
    }

    public String getKey() {
        return key;
    }

    public IocStrategy[] getStrategy() {
        return strategy;
    }
}
