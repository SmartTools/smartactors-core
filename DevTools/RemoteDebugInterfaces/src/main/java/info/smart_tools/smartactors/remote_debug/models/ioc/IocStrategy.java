package info.smart_tools.smartactors.remote_debug.models.ioc;

import java.util.Objects;

public class IocStrategy {

    private final IocModule module;
    private final Object strategy;

    public IocStrategy(final IocModule module, final Object strategy) {
        this.module = module;
        this.strategy = strategy;
    }

    public IocModule getModule() {
        return module;
    }

    public Object getStrategy() {
        return strategy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IocStrategy that = (IocStrategy) o;
        return Objects.equals(module, that.module) && Objects.equals(strategy, that.strategy);
    }

    @Override
    public int hashCode() {
        return Objects.hash(module, strategy);
    }
}
