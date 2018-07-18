package info.smart_tools.smartactors.endpoint_components_netty_plugins.http_message_attribute_extractors_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors.HttpHeaderExtractor;
import info.smart_tools.smartactors.endpoint_components_netty.http_message_attribute_extractors.HttpRequestMethodExtractor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

public class HttpMessageAttributeExtractorsPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public HttpMessageAttributeExtractorsPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("http_message_attribute_extractors")
    public void registerExtractors() throws Exception {
        IFieldName messageExtractorFN
                = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageExtractor");
        IFieldName headerNameFN
                = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "header");
        IFieldName defaultValueFN
                = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "defaultValue");

        IAdditionDependencyStrategy extractorStorage
                = IOC.resolve(Keys.getOrAdd("expandable_strategy#message attribute extractor"));

        /*
         * {
         *  "messageExtractor": "..",
         *  "header": "..headerName..",
         *  "defaultValue": "..value.."
         * }
         */
        extractorStorage.register("netty/http/header", new ApplyFunctionToArgumentsStrategy(args -> {
            IObject conf = (IObject) args[1];

            try {
                String headerName = (String) conf.getValue(headerNameFN);
                String defaultValue = (String) conf.getValue(defaultValueFN);
                IFunction messageExtractor = IOC.resolve(
                        Keys.getOrAdd("netty message extractor"), conf.getValue(messageExtractorFN));

                return new HttpHeaderExtractor(messageExtractor, headerName, defaultValue);
            } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));

        /*
         * {
         *  "messageExtractor": ".."
         * }
         */
        extractorStorage.register("netty/http/method", new ApplyFunctionToArgumentsStrategy(args -> {
            IObject conf = (IObject) args[1];

            try {
                IFunction messageExtractor = IOC.resolve(
                        Keys.getOrAdd("netty message extractor"), conf.getValue(messageExtractorFN));

                return new HttpRequestMethodExtractor(messageExtractor);
            } catch (ReadValueException | InvalidArgumentException | ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}
