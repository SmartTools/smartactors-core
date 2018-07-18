package info.smart_tools.smartactors.endpoint_interfaces.imessage_handler;

/**
 * Default type of {@link IMessageContext message context}.
 *
 * <p>
 *  Contains the following parameters:
 *
 *  <ul>
 *      <li>Source message</li>
 *      <li>Destination message</li>
 *      <li>Connection context</li>
 *  </ul>
 *
 *  These parameters are enough for all cases known at moment of creation of this package.
 * </p>
 *
 * @param <TSrc> type of source message
 * @param <TDst> type of destination message
 * @param <TCtx> type of connection context
 */
public interface IDefaultMessageContext<TSrc, TDst, TCtx> extends IMessageContext {
    /** @return the source message */
    TSrc getSrcMessage();

    /** @return the destination message */
    TDst getDstMessage();

    /** @return the connection context */
    TCtx getConnectionContext();

    /** @param message the source message */
    void setSrcMessage(TSrc message);

    /** @param message the destination message */
    void setDstMessage(TDst message);

    /** @param context the connection context */
    void setConnectionContext(TCtx context);
}
