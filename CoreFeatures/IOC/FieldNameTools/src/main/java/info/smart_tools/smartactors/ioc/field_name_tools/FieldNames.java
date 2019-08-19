package info.smart_tools.smartactors.ioc.field_name_tools;

import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Service locator for resolving named instances of {@link IFieldName}
 */
public final class FieldNames {

    private static IKey iFieldNameKey = null;

    /**
     * Default private constructor
     */
    private FieldNames() {
    }

    /**
     * Resolve instance of {@link IFieldName} by given name
     * @param name name of instance of {@link IFieldName}
     * @throws ResolutionException if dependency resolution has been failed
     * @return instance of {@link IFieldName}
     */
    @Deprecated
    public static IFieldName resolveByName(final String name)
            throws ResolutionException {
        if (iFieldNameKey == null) {
            iFieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        }
        return (IFieldName) IOC.resolve(iFieldNameKey, name);
    }

    /**
     * Resolve instance of {@link IFieldName} by given name
     * @param name name of instance of {@link IFieldName}
     * @throws ResolutionException if dependency resolution has been failed
     * @return instance of {@link IFieldName}
     */
    public static IFieldName getFieldNameByName(final String name)
            throws ResolutionException {
        if (iFieldNameKey == null) {
            iFieldNameKey = Keys.getKeyByName(IFieldName.class.getCanonicalName());
        }
        return (IFieldName) IOC.resolve(iFieldNameKey, name);
    }
}
