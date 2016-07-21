package info.smart_tools.smartactors.core.itask_dispatcher;

/**
 * Task dispatcher - the object responsible for taking tasks from some queue and executing them.
 */
public interface ITaskDispatcher {
    /**
     * Start taking tasks from queue and executing them.
     */
    void start();

    /**
     * Stop taking tasks from queue and executing them.
     */
    void stop();
}
