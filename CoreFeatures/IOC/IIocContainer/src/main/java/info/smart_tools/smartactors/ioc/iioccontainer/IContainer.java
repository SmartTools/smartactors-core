package info.smart_tools.smartactors.ioc.iioccontainer;


import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;

/**
 * Interface of IOC
 */
public interface IContainer {

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    IKey getIocKey();

    /**
     * Return specific instance of {@link IKey} for strategy of resolving key by name
     * @return instance of {@link IKey}
     */
    IKey getKeyForKeyByNameStrategy();

    /**
     * Resolve dependency by given given {@link IKey} instance and args
     * @param key instance of {@link IKey}
     * @param args needed parameters for resolve dependency
     * @param <T> type of class for resolution
     * @return instance of class with classId identifier
     * @throws ResolutionException if resolution is impossible because of any errors
     */
    <T> T resolve(IKey key, Object... args)
            throws ResolutionException;

    /**
     * Register new dependency by instance of {@link IKey}
     * @param key instance of {@link IKey}
     * @param strategy instance of {@link IStrategy}
     * @throws RegistrationException when registration is impossible because of any error
     */
    void register(IKey key, IStrategy strategy)
            throws RegistrationException;

    /**
     * Unregister dependency with given key
     * @param key instance of {@link IKey}
     * @throws DeletionException if any errors occurred
     * @return the previous instance of {@link IStrategy} associated with <tt>key</tt>,
     *         or <tt>null</tt> if there was no association for <tt>key</tt>.
     */
    IStrategy unregister(IKey key)
            throws DeletionException;
}