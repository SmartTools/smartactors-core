package info.smart_tools.smartactors.core.iresource_source;

import info.smart_tools.smartactors.core.iaction.IPoorAction;

/**
 * Source of resources.
 */
public interface IResourceSource {
    /**
     * Add an action to be executed when resource becomes available.
     *
     * @param action action to execute when the resource becomes available
     */
    void onAvailable(IPoorAction action);
}
