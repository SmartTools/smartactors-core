package info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor;

/**
 * Interface IPluginLoaderVisitor
 * for implements pattern visitor
 * @param <V> type of inspected object
 */
public interface IPluginLoaderVisitor<V> {

    /**
     * Handler for failed plugin loading
     * @param value value of inspected object
     * @param e instance of {@link Throwable}
     */
    void pluginLoadingFail(V value, Throwable e);

    /**
     * Handler for failed package loading
     * @param value value of inspected object
     * @param e instance of {@link Throwable}
     */
    void packageLoadingFail(V value, Throwable e);

    /**
     * Handler for successful plugin loading
     * @param value value of inspected object
     */
    void pluginLoadingSuccess(V value);

    /**
     * Handler for successful package loading
     * @param value value of inspected object
     */
    void packageLoadingSuccess(V value);
}
