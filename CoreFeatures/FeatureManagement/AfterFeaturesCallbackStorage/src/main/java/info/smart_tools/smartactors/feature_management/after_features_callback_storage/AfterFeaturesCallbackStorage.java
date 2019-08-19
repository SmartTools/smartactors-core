package info.smart_tools.smartactors.feature_management.after_features_callback_storage;

import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;

/**
 * Thread storage of IQueue
 */
public final class AfterFeaturesCallbackStorage {

    private AfterFeaturesCallbackStorage() {}

    private static final ThreadLocal<IQueue> LOCAL_CALLBACK_QUEUE = new ThreadLocal<>();

    public static IQueue getLocalCallbackQueue() {
        return LOCAL_CALLBACK_QUEUE.get();
    }

    public static void setLocalCallbackQueue(final IQueue queue) {
        LOCAL_CALLBACK_QUEUE.set(queue);
    }
}
