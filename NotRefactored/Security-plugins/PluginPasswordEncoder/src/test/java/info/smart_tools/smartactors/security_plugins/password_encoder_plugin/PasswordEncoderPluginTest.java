package info.smart_tools.smartactors.security_plugins.password_encoder_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IActionNoArgs;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.security.encoding.MDPasswordEncoder;
import info.smart_tools.smartactors.security.encoding.codec.Base64;
import info.smart_tools.smartactors.security.encoding.codec.CharSequenceCodec;
import info.smart_tools.smartactors.security.encoding.codec.Hex;
import info.smart_tools.smartactors.security.encoding.codecs.ICharSequenceCodec;
import info.smart_tools.smartactors.security.encoding.codecs.ICodec;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.*;

@PrepareForTest({IOC.class, Keys.class, PasswordEncoderPlugin.class, ApplyFunctionToArgumentsStrategy.class})
@RunWith(PowerMockRunner.class)
public class PasswordEncoderPluginTest {

    private PasswordEncoderPlugin plugin;
    private IBootstrap bootstrap;
    private ArgumentCaptor<IActionNoArgs> actionArgumentCaptor;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new PasswordEncoderPlugin(bootstrap);

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("PasswordEncoderPlugin").thenReturn(bootstrapItem);

        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        when(bootstrapItem.before(anyString())).thenReturn(bootstrapItem);

        plugin.load();

        verifyNew(BootstrapItem.class).withArguments("PasswordEncoderPlugin");

        actionArgumentCaptor = ArgumentCaptor.forClass(IActionNoArgs.class);

        verify(bootstrapItem).process(actionArgumentCaptor.capture());
        verify(bootstrap).add(bootstrapItem);
    }

    @Test
    public void ShouldCorrectLoadPluginAndResolveHex() throws Exception {

        IKey hexEncoderKey = mock(IKey.class);
        when(Keys.getKeyByName("HexEncoder")).thenReturn(hexEncoderKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("HexEncoder");

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> argumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);

        verifyStatic();
        IOC.register(eq(hexEncoderKey), argumentCaptor.capture());

        argumentCaptor.getValue().resolve();

        verifyStatic();
        Hex.create();
    }

    @Test
    public void ShouldCorrectLoadPluginAndResolveBase64() throws Exception {

        IKey base64EncoderKey = mock(IKey.class);
        when(Keys.getKeyByName("Base64Encoder")).thenReturn(base64EncoderKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("Base64Encoder");

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> argumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);

        verifyStatic();
        IOC.register(eq(base64EncoderKey), argumentCaptor.capture());

        argumentCaptor.getValue().resolve();

        verifyStatic();
        Base64.create();
    }

    @Test
    public void ShouldCorrectLoadPluginAndResolveCharset() throws Exception {

        IKey charsetKey = mock(IKey.class);
        when(Keys.getKeyByName("CharSequenceCodec")).thenReturn(charsetKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("CharSequenceCodec");

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> argumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);

        verifyStatic();
        IOC.register(eq(charsetKey), argumentCaptor.capture());

        String charset = "UTF-8";
        argumentCaptor.getValue().resolve(charset);

        verifyStatic();
        CharSequenceCodec.create(charset);
    }

    @Test
    public void ShouldCorrectLoadPluginAndResolvePasswordEncoder() throws Exception {

        IKey passwordEncoderKey = mock(IKey.class);
        when(Keys.getKeyByName("PasswordEncoder")).thenReturn(passwordEncoderKey);

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getKeyByName("PasswordEncoder");

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> argumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);

        verifyStatic();
        IOC.register(eq(passwordEncoderKey), argumentCaptor.capture());

        String algorithm = "sha-512";
        String encoder = "Base64Encoder";
        String charset = "UTF-8";

        IKey charsetKey = mock(IKey.class);
        when(Keys.getKeyByName("CharSequenceCodec")).thenReturn(charsetKey);
        ICharSequenceCodec charsetCodec = mock(ICharSequenceCodec.class);
        when(IOC.resolve(charsetKey, charset)).thenReturn(charsetCodec);

        IKey encoderKey = mock(IKey.class);
        when(Keys.getKeyByName(encoder)).thenReturn(encoderKey);
        ICodec codec = mock(ICodec.class);
        when(IOC.resolve(encoderKey)).thenReturn(codec);

        argumentCaptor.getValue().resolve(algorithm, encoder, charset);

        verifyStatic();
        MDPasswordEncoder.create(algorithm, codec, charsetCodec);
    }

    @Test(expected = PluginException.class)
    public void ShouldThrowException_When_InternalExceptionIsThrown() throws Exception {

        whenNew(BootstrapItem.class).withArguments("PasswordEncoderPlugin").thenThrow(new InvalidArgumentException(""));
        plugin.load();
    }
}
