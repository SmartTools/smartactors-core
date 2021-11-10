package info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager;

import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Strategy that defines methods for processing of some section of server configuration.
 */
public interface ISectionStrategy {
    /**
     * Called when configuration is loaded.
     *
     * @param config    the configuration object
     * @throws ConfigurationProcessingException if any error occurs while loading the configuration
     * @see info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager
     */
    void onLoadConfig(final IObject config) throws ConfigurationProcessingException;

    /**
     * Called when configuration is reverted.
     *
     * @param config    the configuration object
     * @throws ConfigurationProcessingException if any error occurs while reverting the configuration
     * @see info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager
     */
    void onRevertConfig(final IObject config) throws ConfigurationProcessingException;

    /**
     * Return name of the configuration section processed by this strategy.
     *
     * @return name of the configuration section
     */
    IFieldName getSectionName();

    String getSectionSchema();
}
