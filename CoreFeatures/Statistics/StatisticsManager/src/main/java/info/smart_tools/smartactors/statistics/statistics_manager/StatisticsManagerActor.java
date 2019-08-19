package info.smart_tools.smartactors.statistics.statistics_manager;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions.SensorShutdownException;
import info.smart_tools.smartactors.statistics.statistics_manager.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.statistics.statistics_manager.exceptions.CommandNotFoundException;
import info.smart_tools.smartactors.statistics.statistics_manager.wrappers.StatisticsCommandWrapper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Statistics manager actor manages sensors (accessible through {@link ISensorHandle sensor handles}), data collectors (represented as
 * actors) and links between them.
 *
 * <p>
 *     This actor holds lists of managed sensors and collectors identified by string identifiers.
 *     Links are represented by steps in chain associated with the sensor, creation or deletion of links causes re-creation of the chain
 *     (using {@link IConfigurationManager configuration manager}).
 * </p>
 *
 * <p>
 *     The sensors are created using a IOC strategy taking 2 arguments -- identifier of the chain where to send the data and {@link IObject}
 *     containing sensor configuration.
 * </p>
 *
 * <p>
 *     For each data collector is created a chain (called "query chain") that has the only step configuration of which is passed on
 *     collector creation. Such chains are meant to be used to query current state of data collector.
 * </p>
 */
public class StatisticsManagerActor {
    /** Interface for a single command */
    @FunctionalInterface
    private interface ICommand {
        Object execute(Object arg) throws CommandExecutionException, InvalidArgumentException;
    }

    /** Class that holds information about a single sensor */
    private class SensorInfo {
        private final IObject originalConfig;
        private final ISensorHandle handle;
        private final String chainName;
        private final Map<String, IObject> collectors;
        private final String originalDependency;

        SensorInfo(final String originalDependency, final IObject conf, final ISensorHandle handle, final String chainName) {
            this.originalDependency = originalDependency;
            this.originalConfig = conf;
            this.handle = handle;
            this.chainName = chainName;
            this.collectors = new HashMap<>();
        }

        String getChainName() {
            return chainName;
        }

        ISensorHandle getHandle() {
            return handle;
        }

        Map<String, IObject> getConnectedCollectors() {
            return collectors;
        }

        IObject getOriginalConfig() {
            return originalConfig;
        }

        String getOriginalDependency() {
            return originalDependency;
        }
    }

    /** Class that holds information about a single data collector */
    private class CollectorInfo {
        private final String objectName;
        private final IObject originalConfig;
        private final String queryChainName;

        CollectorInfo(final String objectName, final IObject originalConfig, final String queryChainName) {
            this.objectName = objectName;
            this.originalConfig = originalConfig;
            this.queryChainName = queryChainName;
        }

        String getObjectName() {
            return objectName;
        }

        IObject getOriginalConfig() {
            return originalConfig;
        }

        String getQueryChainName() {
            return queryChainName;
        }
    }

    private final IFieldName chainsSectionFieldName;
    private final IFieldName objectsSectionFieldName;
    private final IFieldName chainIdFieldName;
    private final IFieldName exceptionalFieldName;
    private final IFieldName externalAccessFieldName;
    private final IFieldName chainStepsFieldName;
    private final IFieldName dependencyFieldName;
    private final IFieldName argsFieldName;
    private final IFieldName idFieldName;
    private final IFieldName sensorFieldName;
    private final IFieldName collectorFieldName;
    private final IFieldName stepConfigFieldName;
    private final IFieldName objectNameFieldName;
    private final IFieldName targetFieldName;
    private final IFieldName queryStepConfigFieldName;

    private final Map<String, SensorInfo> sensors;
    private final Map<String, CollectorInfo> collectors;
    private final Set<String> invalidSensorChains;
    private final Map<String, ICommand> commands;

    private IObject toConfigObject(final IObject object)
            throws SerializeException, ResolutionException {
        String serialized = object.serialize();
        return IOC.resolve(Keys.getKeyByName("configuration object"), serialized);
    }

    private void invalidateChainFor(final String sensorId) {
        invalidSensorChains.add(sensorId);
    }

    private IObject createChainConfigForSensor(final String sensorId)
            throws ResolutionException, ChangeValueException, InvalidArgumentException {
        IObject chainConfig = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));
        chainConfig.setValue(chainIdFieldName, sensors.get(sensorId).getChainName());
        chainConfig.setValue(exceptionalFieldName, Collections.EMPTY_LIST);

        List<IObject> steps = new ArrayList<>(sensors.get(sensorId).getConnectedCollectors().size());

        for (Map.Entry<String, IObject> entry : sensors.get(sensorId).getConnectedCollectors().entrySet()) {
            IObject step = entry.getValue();
            steps.add(step);
        }

        chainConfig.setValue(chainStepsFieldName, steps);
        return chainConfig;
    }

    private void rebuildChains()
            throws ChangeValueException, ResolutionException, InvalidArgumentException, ConfigurationProcessingException,
            SerializeException {
        IObject rootObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        List<IObject> chainsSection = new ArrayList<>(invalidSensorChains.size());

        for (String id : invalidSensorChains) {
            chainsSection.add(createChainConfigForSensor(id));
        }

        rootObject.setValue(chainsSectionFieldName, chainsSection);

        IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

        configurationManager.applyConfig(toConfigObject(rootObject));

        invalidSensorChains.clear();
    }

    private void createCollectorObject(final String objectName, final IObject objectConfig,
                                       final String queryChainName, final IObject queryStepConfig)
            throws ChangeValueException, InvalidArgumentException, ConfigurationProcessingException, ResolutionException,
            SerializeException {
        IObject responseStepConfigObject = IOC.resolve(Keys.getKeyByName("configuration object"),
                ("{\n" +
                        "\"target\": \"respond\",\n" +
                        "\"handler\": \"sendResponse\"\n" +
                 "}"));

        objectConfig.setValue(objectNameFieldName, objectName);
        queryStepConfig.setValue(targetFieldName, objectName);

        IObject chainConfig = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        chainConfig.setValue(chainIdFieldName, queryChainName);
        chainConfig.setValue(chainStepsFieldName, Arrays.asList(queryStepConfig, responseStepConfigObject));
        chainConfig.setValue(exceptionalFieldName, Collections.EMPTY_LIST);
        chainConfig.setValue(externalAccessFieldName, Boolean.TRUE);

        IObject rootConfigObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

        rootConfigObject.setValue(objectsSectionFieldName, Collections.singletonList(objectConfig));
        rootConfigObject.setValue(chainsSectionFieldName, Collections.singletonList(chainConfig));

        IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

        configurationManager.applyConfig(toConfigObject(rootConfigObject));
    }

    private String generateChainNameForSensor(final String sensorId) {
        return MessageFormat.format("sensor-chain/{0}-{1}", sensorId, UUID.randomUUID().toString());
    }

    private String generateCollectorObjectName(final String collectorId) {
        return MessageFormat.format("data-collector/{0}-{1}", collectorId, UUID.randomUUID().toString());
    }

    private String generateCollectorChainName(final String collectorId) {
        return MessageFormat.format("data-collector-chain/{0}-{1}", collectorId, UUID.randomUUID().toString());
    }

    /**
     * The constructor.
     *
     * @throws ResolutionException if error occurs resolving anu dependency
     */
    public StatisticsManagerActor()
            throws ResolutionException {
        chainsSectionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maps");
        objectsSectionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "objects");
        chainIdFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id");
        exceptionalFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "exceptional");
        externalAccessFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "externalAccess");
        chainStepsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "steps");
        dependencyFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "dependency");
        argsFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "args");
        idFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "id");
        sensorFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sensor");
        collectorFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "collector");
        stepConfigFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "stepConfig");
        objectNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "name");
        targetFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "target");
        queryStepConfigFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "queryStepConfig");

        sensors = new HashMap<>();
        collectors = new HashMap<>();
        invalidSensorChains = new HashSet<>();
        commands = new HashMap<>();

        commands.put("createSensor", this::cmdCreateSensor);
        commands.put("shutdownSensor", this::cmdShutdownSensor);
        commands.put("link", this::cmdLink);
        commands.put("unlink", this::cmdUnlink);
        commands.put("enumLinks", this::cmdEnumLinks);
        commands.put("enumSensors", this::cmdEnumSensors);
        commands.put("createCollector", this::cmdCreateCollector);
        commands.put("enumCollectors", this::cmdEnumCollectors);
        commands.put("getCollectorQueryChain", this::cmdGetCollectorQueryChain);
    }

    /**
     * Execute a command.
     *
     * @param message    the message wrapper
     * @throws ReadValueException if error occurs reading the message
     * @throws CommandNotFoundException if the command required is not exist
     * @throws ChangeValueException if error occurs writing back the result
     */
    public void executeCommand(final StatisticsCommandWrapper message)
            throws ReadValueException, CommandNotFoundException, ChangeValueException {
        ICommand command = commands.get(message.getCommand());

        if (null == command) {
            throw new CommandNotFoundException(MessageFormat.format("No such command: ''{0}''", message.getCommand()));
        }

        try {
            message.setCommandResult(command.execute(message.getCommandArguments()));
        } catch (CommandExecutionException | InvalidArgumentException e) {
            message.setException(e);
        }
    }

    /**
     * Create a sensor.
     *
     * Input:
     *
     * <pre>
     *     {
     *         "id": " .. identifier of new sensor .. ",
     *         "dependency": " .. IOC dependency for sensor creation .. ",
     *         "args": {
     *             .. sensor configuration ..
     *         }
     *     }
     * </pre>
     *
     * Output:
     *
     * <pre>
     *     "OK"
     * </pre>
     */
    private Object cmdCreateSensor(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            IObject args = (IObject) arg;

            String id = (String) args.getValue(idFieldName);
            String chainName = generateChainNameForSensor(id);

            if (sensors.containsKey(id)) {
                throw new InvalidArgumentException(MessageFormat.format("Sensor named ''{0}'' already exist.", id));
            }

            ISensorHandle handle = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), args.getValue(dependencyFieldName)),
                    IOC.resolve(Keys.getKeyByName("chain_id_from_map_name"), chainName),
                    args.getValue(argsFieldName)
            );

            sensors.put(id, new SensorInfo(
                    (String) args.getValue(dependencyFieldName),
                    (IObject) args.getValue(argsFieldName),
                    handle,
                    chainName));

            invalidateChainFor(id);
            rebuildChains();

            return "OK";
        } catch (ClassCastException | ReadValueException | InvalidArgumentException | ResolutionException | ChangeValueException
                | ConfigurationProcessingException | SerializeException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Create/update a link between sensor and collector.
     *
     * Input:
     *
     * <pre>
     *     {
     *         "sensor": " .. sensor id .. ",
     *         "collector": " .. collector id .. ",
     *         "stepConfig": {
     *             .. configuration of the chain step, may include "wrapper" and "handler" and other specific fields ..
     *         }
     *     }
     * </pre>
     *
     * Output:
     *
     * <pre>
     *     "OK"
     * </pre>
     */
    private Object cmdLink(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            IObject args = (IObject) arg;
            String sensorId = (String) args.getValue(sensorFieldName);
            String collectorId = (String) args.getValue(collectorFieldName);
            IObject stepConf = (IObject) args.getValue(stepConfigFieldName);

            if (!collectors.containsKey(collectorId)) {
                throw new CommandExecutionException("No such collector");
            }

            if (!sensors.containsKey(sensorId)) {
                throw new CommandExecutionException("No such sensor");
            }

            stepConf.setValue(targetFieldName, collectors.get(collectorId).getObjectName());

            sensors.get(sensorId).getConnectedCollectors().put(collectorId, stepConf);

            invalidateChainFor(sensorId);
            rebuildChains();

            return "OK";
        } catch (ReadValueException | InvalidArgumentException | ConfigurationProcessingException | ChangeValueException
                | ResolutionException | SerializeException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Remove a link between a sensor and data collector.
     *
     * Input:
     *
     * <pre>
     *     {
     *         "sensor": " .. sensor id .. ",
     *         "collector": " .. collector id .. "
     *     }
     * </pre>
     *
     * Output if link exists:
     *
     * <pre>
     *     "OK"
     * </pre>
     *
     * Output if link not exists:
     *
     * <pre>
     *     "NOT EXIST"
     * </pre>
     */
    private Object cmdUnlink(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            IObject args = (IObject) arg;
            String sensorId = (String) args.getValue(sensorFieldName);
            String collectorId = (String) args.getValue(collectorFieldName);

            if (!collectors.containsKey(collectorId)) {
                throw new CommandExecutionException("No such collector");
            }

            if (!sensors.containsKey(sensorId)) {
                throw new CommandExecutionException("No such sensor");
            }

            if (null == sensors.get(sensorId).getConnectedCollectors().remove(collectorId)) {
                return "NOT EXIST";
            }

            invalidateChainFor(sensorId);
            rebuildChains();

            return "OK";
        } catch (ReadValueException | InvalidArgumentException | ConfigurationProcessingException | ChangeValueException
                | ResolutionException | SerializeException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Shutdown the sensor (using {@link ISensorHandle#shutdown() method of sensor handle}).
     *
     * Input:
     *
     * <pre>
     *     " .. sensor id .. "
     * </pre>
     *
     * Output:
     *
     * <pre>
     *     "OK"
     * </pre>
     */
    private Object cmdShutdownSensor(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            String sensorId = (String) arg;

            if (!sensors.containsKey(sensorId)) {
                throw new CommandExecutionException("No such sensor.");
            }

            sensors.get(sensorId).getHandle().shutdown();

            // TODO:: Remove chain

            sensors.remove(sensorId);

            return "OK";
        } catch (SensorShutdownException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Get list of all sensors.
     *
     * Output:
     *
     * <pre>
     *     [
     *         {
     *             "id": " .. sensor id .. ",
     *             "dependency": " .. IOC dependency the sensor was created with .. ",
     *             "args": {
     *                 .. the arguments the sensor was created with ..
     *             }
     *         },
     *         .. for each sensor ..
     *     ]
     * </pre>
     */
    private Object cmdEnumSensors(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            List<IObject> sensorsList = new ArrayList<>(sensors.size());

            for (Map.Entry<String, SensorInfo> entry : sensors.entrySet()) {
                IObject sensorView = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

                sensorView.setValue(idFieldName, entry.getKey());
                sensorView.setValue(dependencyFieldName, entry.getValue().getOriginalDependency());
                sensorView.setValue(argsFieldName, entry.getValue().getOriginalConfig());

                sensorsList.add(sensorView);
            }

            return sensorsList;
        } catch (ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Create a data collector actor and query chain.
     *
     * Input:
     *
     * <pre>
     *     {
     *         "id": " .. collector id .. ",
     *         "args": {
     *             .. configuration of the collector actor itself (except "name" field) ..
     *         },
     *         "queryStepConfig": {
     *             .. configuration of the chain step (except "target" field as it is generated here) ..
     *         }
     *     }
     * </pre>
     *
     * Output:
     *
     * <pre>
     *     "OK"
     * </pre>
     */
    private Object cmdCreateCollector(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            IObject args = (IObject) arg;
            String collectorId = (String) args.getValue(idFieldName);
            IObject config = (IObject) args.getValue(argsFieldName);
            IObject queryStepConfig = (IObject) args.getValue(queryStepConfigFieldName);

            if (collectors.containsKey(collectorId)) {
                throw new InvalidArgumentException(MessageFormat.format("Collector named ''{0}'' already exists.", collectorId));
            }

            String name = generateCollectorObjectName(collectorId);
            String chainName = generateCollectorChainName(collectorId);

            createCollectorObject(name, config, chainName, queryStepConfig);

            collectors.put(collectorId, new CollectorInfo(name, config, chainName));

            return "OK";
        } catch (ReadValueException | ChangeValueException | InvalidArgumentException | ResolutionException | SerializeException
                | ConfigurationProcessingException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Get list of all collectors.
     *
     * Output:
     *
     * <pre>
     *     [
     *         {
     *             "id": " .. identifier of the collector .. ",
     *             "args": {
     *                 .. the initial actor config ..
     *             }
     *         },
     *         .. for each collector ..
     *     ]
     * </pre>
     */
    private Object cmdEnumCollectors(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        try {
            List<IObject> collectorsList = new ArrayList<>(collectors.size());

            for (Map.Entry<String, CollectorInfo> entry : collectors.entrySet()) {
                IObject collectorView = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

                collectorView.setValue(idFieldName, entry.getKey());
                collectorView.setValue(argsFieldName, entry.getValue().getOriginalConfig());

                collectorsList.add(collectorView);
            }

            return collectorsList;
        } catch (ChangeValueException | InvalidArgumentException | ResolutionException e) {
            throw new CommandExecutionException(e);
        }
    }

    /**
     * Get name of the query chain created for a collector with given identifier.
     *
     * Input:
     *
     * <pre>
     *     " .. collector id .."
     * </pre>
     *
     * Output:
     *
     * <pre>
     *     " .. chain name .. "
     * </pre>
     */
    private Object cmdGetCollectorQueryChain(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        if (!collectors.containsKey(String.valueOf(arg))) {
            throw new CommandExecutionException(MessageFormat.format("There is no collector named ''{0}''", arg));
        }

        return collectors.get(String.valueOf(arg)).getQueryChainName();
    }

    /**
     * Get list of present links between sensors and collectors.
     *
     * Output:
     *
     * <pre>
     *     [
     *       {
     *           "sensor": " .. sensor id .. ",
     *           "collector": " .. collector id .. ",
     *           "args": { .. collector step configuration .. }
     *       },
     *       .. for each link ..
     *     ]
     * </pre>
     */
    private Object cmdEnumLinks(final Object arg)
            throws CommandExecutionException, InvalidArgumentException {
        List<IObject> linkViews = new LinkedList<>();

        try {
            for (Map.Entry<String, SensorInfo> sensorEntry : sensors.entrySet()) {
                for (Map.Entry<String, IObject> collectorLinkEntry : sensorEntry.getValue().getConnectedCollectors().entrySet()) {
                    IObject linkView = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"));

                    linkView.setValue(sensorFieldName, sensorEntry.getKey());
                    linkView.setValue(collectorFieldName, collectorLinkEntry.getKey());
                    linkView.setValue(argsFieldName, collectorLinkEntry.getValue());

                    linkViews.add(linkView);
                }
            }
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException e) {
            throw new CommandExecutionException(e);
        }

        return linkViews;
    }
}
