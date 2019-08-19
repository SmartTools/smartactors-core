package info.smart_tools.smartactors.event_handler_with_logging_to_file.event_handler_with_logging_to_file;

import info.smart_tools.smartactors.base.interfaces.iaction.IActionTwoArgs;
import info.smart_tools.smartactors.event_handler.event_handler.Event;
import info.smart_tools.smartactors.event_handler.event_handler.EventHandler;
import info.smart_tools.smartactors.event_handler.event_handler.IEvent;
import info.smart_tools.smartactors.event_handler.event_handler.IEventHandlerContainer;
import info.smart_tools.smartactors.event_handler.event_handler.IExtendedEventHandlerContainer;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;

import java.io.PrintWriter;
import java.util.HashMap;

/**
 * Class-registrar. It registers an instance of {@link PrintToFileEventHandler} to the {@link EventHandler}
 */
public final class EventHandlerWithPrintToFileEventHandler implements IPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public EventHandlerWithPrintToFileEventHandler(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        IEventHandlerContainer container = EventHandler.getEventHandlerContainer();
        ((IExtendedEventHandlerContainer) container).register(
                new PrintToFileEventHandler(
                        "fileLogger",
                        null,
                        (event, writer) -> writer.println(event.getBody().toString()),
                        new HashMap<Object, Object>() {{
                            put(
                                    Exception.class.getCanonicalName(),
                                    (IActionTwoArgs<IEvent, PrintWriter>) (event, writer) -> ((Exception) event.getBody()).printStackTrace(writer)
                            );
                        }}
                )
        );
        EventHandler.handle(
                Event.builder().body("Added PrintToFileEventHandler to the EventHandler.").build()
        );
    }

    @Override
    public void load()
            throws PluginException {
    }
}
