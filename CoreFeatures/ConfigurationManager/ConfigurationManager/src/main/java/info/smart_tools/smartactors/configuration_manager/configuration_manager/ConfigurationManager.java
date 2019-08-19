package info.smart_tools.smartactors.configuration_manager.configuration_manager;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

import java.text.MessageFormat;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Implementation of {@link IConfigurationManager}
 */
public class ConfigurationManager implements IConfigurationManager {
    private final List<ISectionStrategy> sectionStrategies;
    private final Set<IFieldName> registeredSections;

    /**
     * The constructor.
     */
    public ConfigurationManager() {
        sectionStrategies = new CopyOnWriteArrayList<>();
        registeredSections = new CopyOnWriteArraySet<>();
    }

    @Override
    public void addSectionStrategy(final ISectionStrategy strategy) throws InvalidArgumentException {
        if (null == strategy) {
            throw new InvalidArgumentException("Strategy should not be null.");
        }

        if (!registeredSections.add(strategy.getSectionName())) {
            throw new InvalidArgumentException("Strategy for the section is already registered.");
        }

        sectionStrategies.add(strategy);
    }

    @Override
    public void removeSectionStrategy(final IFieldName sectionName) throws InvalidArgumentException {
        if (null == sectionName) {
            throw new InvalidArgumentException("Strategy section name should not be null.");
        }

        if (!registeredSections.remove(sectionName)) {
            throw new InvalidArgumentException("Strategy for the section has not been registered.");
        }

        sectionStrategies.removeIf(strategy -> strategy.getSectionName() == sectionName);
    }

    @Override
    public void applyConfig(final IObject config)
            throws InvalidArgumentException, ConfigurationProcessingException {
        for (ISectionStrategy sectionStrategy : sectionStrategies) {
            try {
                if (null != config.getValue(sectionStrategy.getSectionName())) {
                    sectionStrategy.onLoadConfig(config);
                }
            } catch (ReadValueException e) {
                throw new ConfigurationProcessingException(
                        MessageFormat.format("Could not read section ''{0}'' from given configuration object.",
                                sectionStrategy.getSectionName()), e);
            }
        }
    }

    @Override
    public void revertConfig(final IObject config)
            throws InvalidArgumentException, ConfigurationProcessingException {
        ConfigurationProcessingException exception = new ConfigurationProcessingException("Error occurred while reverting configuration load.");
        ListIterator<ISectionStrategy> sectionStrategyIterator = sectionStrategies.listIterator(sectionStrategies.size());
        ISectionStrategy sectionStrategy;

        while (sectionStrategyIterator.hasPrevious()) {
            sectionStrategy = sectionStrategyIterator.previous();
            try {
                if (null != config.getValue(sectionStrategy.getSectionName())) {
                    sectionStrategy.onRevertConfig(config);
                }
            } catch (ConfigurationProcessingException e) {
                exception.addSuppressed(e);
            } catch (ReadValueException e) {
                exception.addSuppressed(new ConfigurationProcessingException(
                        MessageFormat.format("Could not read section ''{0}'' from given configuration object.",
                                sectionStrategy.getSectionName()), e));
            }
        }
        if (exception.getSuppressed().length > 0) {
            throw exception;
        }
    }
}
