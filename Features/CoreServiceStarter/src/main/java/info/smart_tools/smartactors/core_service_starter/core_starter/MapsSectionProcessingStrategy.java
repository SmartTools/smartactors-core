package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainCreationException;

import java.util.List;
import java.util.ListIterator;

/**
 * Creates and registers message maps (receiver chains) using configuration.
 *
 * Expects the following configuration format:
 *
 * <pre>
 *     {
 *         "maps": [
 *             {
 *                 "id": "mapName",
 *                 // . . .
 *             },
 *             {
 *                 "id": "map2Name",
 *                 // . . .
 *             }
 *         ]
 *     }
 * </pre>
 */
public class MapsSectionProcessingStrategy implements ISectionStrategy {
    private final IFieldName name;

    private final IFieldName mapIdFieldName;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public MapsSectionProcessingStrategy()
            throws ResolutionException {
        this.name = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maps");
        this.mapIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id");
    }

    @Override
    public void onLoadConfig(final IObject config)
            throws ConfigurationProcessingException {
        try {
            List<IObject> section = (List<IObject>) config.getValue(name);

            IChainStorage chainStorage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));

            for (IObject mapDescription : section) {
                Object chainId = IOC.resolve(Keys.getKeyByName("chain_id_from_map_name"), mapDescription.getValue(mapIdFieldName));
                try {
                    chainStorage.register(chainId, mapDescription);
                } catch (ChainCreationException e) {
                    throw new ConfigurationProcessingException("Could not create chain for map #'" + String.valueOf(chainId) + "'.", e);
                }
            }
        } catch (InvalidArgumentException | ResolutionException | ReadValueException e) {
            throw new ConfigurationProcessingException("Error occurred loading \"maps\" section of configuration.", e);
        }
    }

    @Override
    public void onRevertConfig(final IObject config)
            throws ConfigurationProcessingException {
        ConfigurationProcessingException exception = new ConfigurationProcessingException("Error occurred reverting \"maps\" configuration section.");
        try {
            List<IObject> section = (List<IObject>) config.getValue(name);
            ListIterator<IObject> sectionIterator = section.listIterator(section.size());
            IChainStorage chainStorage = IOC.resolve(Keys.getKeyByName(IChainStorage.class.getCanonicalName()));
            IObject mapDescription;

            while (sectionIterator.hasPrevious()) {
                mapDescription = sectionIterator.previous();
                try {
                    Object chainId = IOC.resolve(Keys.getKeyByName("chain_id_from_map_name"), mapDescription.getValue(mapIdFieldName));
                    chainStorage.unregister(chainId);
                } catch (InvalidArgumentException | ReadValueException | ResolutionException e) {
                    exception.addSuppressed(e);
                }
            }
        } catch (InvalidArgumentException | ResolutionException | ReadValueException e) {
            exception.addSuppressed(e);
        }
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }

    @Override
    public IFieldName getSectionName() {
        return name;
    }

    @Override
    public String getSectionSchema() {
        return null;
    }
}
