package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.statistics.sensors.interfaces.ISensorHandle;
import info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions.SensorShutdownException;

/**
 *
 */
public class EmbeddedSensorHandle implements ISensorHandle {
    private final IChainStorage chainStorage;
    private final Object chainId;
    private final Object chainModificationId;

    /**
     * The constructor.
     *
     * @param chainStorage           the chain storage
     * @param chainId                the chain identifier
     * @param chainModificationId    identifier of chain modification embedding the sensor into the chain
     */
    public EmbeddedSensorHandle(final IChainStorage chainStorage, final Object chainId, final Object chainModificationId) {
        this.chainStorage = chainStorage;
        this.chainId = chainId;
        this.chainModificationId = chainModificationId;
    }

    @Override
    public void shutdown() throws SensorShutdownException {
        try {
            chainStorage.rollback(chainId, chainModificationId);
        } catch (ChainModificationException | ChainNotFoundException e) {
            throw new SensorShutdownException(e);
        }
    }
}
