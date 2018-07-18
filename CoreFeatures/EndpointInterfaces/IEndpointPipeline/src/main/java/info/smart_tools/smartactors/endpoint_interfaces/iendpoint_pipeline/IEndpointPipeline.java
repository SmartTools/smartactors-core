package info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;

import java.util.List;

/**
 * Interface for handler pipeline.
 *
 * <p>
 *  Canonical pipeline description should look like the following:
 * </p>
 *
 * <pre>
 *  {
 *      "id": "pipeline-id",
 *      "stages": [
 *          {
 *              "type": "stage-type-1",
 *              ...
 *          },
 *          ...
 *      ]
 *  }
 * </pre>
 *
 * <p>
 *  Each of objects in "stages" array represents one or more {@link IMessageHandler message handler}. Message handlers
 *  are passed by message in the order they are listed in "stages" array.
 * </p>
 *
 * @param <T> type {@link IMessageContext message context} at input of the pipeline
 */
public interface IEndpointPipeline<T extends IMessageContext> {
    /**
     * List of all handlers included in the pipeline.
     *
     * <p>
     *  Handlers are iterated in the same order as they are passed by message.
     * </p>
     *
     * @return list of all handlers included in the pipeline
     */
    List<IMessageHandler> getHandlers();

    /**
     * Returns factory creating message contexts acceptable for this pipeline.
     *
     * <p>
     *  Factory implementation may depend on pipeline implementation and types of message contexts required by pipeline
     *  handlers.
     * </p>
     *
     * @return function creating message contexts to be sent to pipeline
     */
    IFunction0<T> getContextFactory();

    /**
     * @return callback that sends a given message context to this pipeline
     */
    IMessageHandlerCallback<T> getInputCallback();
}
