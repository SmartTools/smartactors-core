package info.smart_tools.smartactors.core.iasync_service;

import java.util.concurrent.CompletableFuture;

/**
 * A service which is capable to start and stop asynchronously.
 *
 * @param <TService> concrete type of service for chaining {@link CompletableFuture} calls.
 */
public interface IAsyncService<TService> {
    /**
     * Asynchronously start the service.
     *
     * @return a future object which can be used for operation completion waiting.
     * Future return value is just this instance.
     */
    CompletableFuture<TService> start();

    /**
     * Asynchronously stop the service.
     *
     * @return a future object which can be used for operation completion waiting.
     * Future return value is just this instance.
     */
    CompletableFuture<TService> stop();
}