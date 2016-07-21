package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.ifield_name.IFieldName;

public class DeclaredParam implements IDeclaredParam {
    private IFieldName name;
    private int count;

    private DeclaredParam(final IFieldName name, final int count) {
        this.name = name;
        this.count = count;
    }

    public static DeclaredParam create(final IFieldName name, final int count) {
        return new DeclaredParam(name, count);
    }

    @Override
    public boolean equals(final Object obj) {
        return obj instanceof DeclaredParam && name.equals(((DeclaredParam) obj).name);

    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    public IFieldName getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
