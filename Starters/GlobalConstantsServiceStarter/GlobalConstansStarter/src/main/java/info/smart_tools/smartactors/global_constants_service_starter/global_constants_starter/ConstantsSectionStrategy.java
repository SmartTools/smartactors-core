package info.smart_tools.smartactors.global_constants_service_starter.global_constants_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.List;

/**
 * Strategy processing "const" configuration section.
 *
 * <pre>
 *     {
 *         "const": [
 *             {
 *                 "name": "CONSTANT_1",        // Name of the global constant
 *                 "value": 1337                // Value of the constant
 *             },
 *             {
 *                 "name": "CONSTANT_2",
 *                 "value": {                   // Value may be a object
 *                     "foo": "bar"
 *                 }
 *             }
 *         ]
 *     }
 * </pre>
 */
public class ConstantsSectionStrategy implements ISectionStrategy {
    private final IFieldName sectionFieldName;
    private final IFieldName constantNameFN;
    private final IFieldName constantValueFN;

    /**
     * The constructor.
     *
     * @throws ResolutionException if cannot resolve any dependencies
     */
    public ConstantsSectionStrategy()
            throws ResolutionException {
        sectionFieldName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "const");
        constantNameFN = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
        constantValueFN = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "value");
    }

    @Override
    public void onLoadConfig(final IObject config) throws ConfigurationProcessingException {
        try {
            IObject constants = IOC.resolve(Keys.getOrAdd("global constants"));
            List<IObject> section = (List<IObject>) config.getValue(sectionFieldName);

            for (IObject obj : section) {
                IFieldName cFName = IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), obj.getValue(constantNameFN));
                Object cValue = obj.getValue(constantValueFN);

                constants.setValue(cFName, cValue);
            }
        } catch (ReadValueException | InvalidArgumentException | ResolutionException | ChangeValueException e) {
            throw new ConfigurationProcessingException(e);
        }
    }

    @Override
    public IFieldName getSectionName() {
        return sectionFieldName;
    }
}
