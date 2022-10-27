package info.smart_tools.smartactors.task.interfaces.itask_dispatcher;

import info.smart_tools.smartactors.task.itask_preprocess_strategy.ITaskProcessStrategy;

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

    /**
     * Set the strategy to be used to process tasks.
     *
     * @param strategy    strategy to use or {@code null} to execute tasks immediately
     */
    void setProcessStrategy(ITaskProcessStrategy strategy);
}
