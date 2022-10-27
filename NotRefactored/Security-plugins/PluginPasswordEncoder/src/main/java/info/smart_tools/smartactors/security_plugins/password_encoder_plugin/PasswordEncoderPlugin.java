package info.smart_tools.smartactors.security_plugins.password_encoder_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.security.encoding.MDPasswordEncoder;
import info.smart_tools.smartactors.security.encoding.codec.Base64;
import info.smart_tools.smartactors.security.encoding.codec.CharSequenceCodec;
import info.smart_tools.smartactors.security.encoding.codec.Hex;
import info.smart_tools.smartactors.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.security.encoding.encoders.IEncoder;

/**
 * Plugin for strategies for password encoders and its dependencies
 */
public class PasswordEncoderPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public PasswordEncoderPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            IBootstrapItem<String> item = new BootstrapItem("PasswordEncoderPlugin");
            item
                .process(() -> {
                    try {
                        IOC.register(Keys.getKeyByName("CharSequenceCodec"), new ApplyFunctionToArgumentsStrategy(
                                (args) -> {
                                    try {
                                        String charset = String.valueOf(args[0]);
                                        return CharSequenceCodec.create(charset);
                                    } catch (Exception e) {
                                        throw new RuntimeException("Error during resolving char sequence codec", e);
                                    }
                                }
                            )
                        );
                        IOC.register(Keys.getKeyByName("Base64Encoder"), new ApplyFunctionToArgumentsStrategy(
                                (args) -> {
                                    try {
                                        return Base64.create();
                                    } catch (Exception e) {
                                        throw new RuntimeException("Error during resolving encoder", e);
                                    }
                                }
                            )
                        );
                        IOC.register(Keys.getKeyByName("HexEncoder"), new ApplyFunctionToArgumentsStrategy(
                                (args) -> {
                                    try {
                                        return Hex.create();
                                    } catch (Exception e) {
                                        throw new RuntimeException("Error during resolving encoder", e);
                                    }
                                }
                            )
                        );
                        IOC.register(Keys.getKeyByName("PasswordEncoder"), new ApplyFunctionToArgumentsStrategy(
                                (args) -> {
                                    try {
                                        String algorithm = String.valueOf(args[0]);
                                        String encoderType = String.valueOf(args[1]);
                                        String charset = String.valueOf(args[2]);

                                        IEncoder encoder = IOC.resolve(Keys.getKeyByName(encoderType));
                                        ICharSequenceCodec charSequenceCodec = IOC.resolve(Keys.getKeyByName("CharSequenceCodec"), charset);
                                        return MDPasswordEncoder.create(algorithm, encoder, charSequenceCodec);
                                    } catch (Exception e) {
                                        throw new RuntimeException("Error during resolving password encoder", e);
                                    }
                                }
                            )
                        );
                    } catch (ResolutionException e) {
                        throw new ActionExecutionException("PasswordEncoder plugin can't load: can't get key");
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecutionException("PasswordEncoder plugin can't load: can't create strategy");
                    } catch (RegistrationException e) {
                        throw new ActionExecutionException("PasswordEncoder plugin can't load: can't register new strategy");
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't get BootstrapItem", e);
        }
    }
}
