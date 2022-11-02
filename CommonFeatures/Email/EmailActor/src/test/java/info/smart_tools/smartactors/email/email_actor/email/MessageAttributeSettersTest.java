package info.smart_tools.smartactors.email.email_actor.email;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.field.field.Field;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static org.mockito.Mockito.*;

public class MessageAttributeSettersTest {
    @Before
    public void setUp() throws ScopeProviderException, RegistrationException, ResolutionException, InvalidArgumentException {
        ScopeProvider.subscribeOnCreationNewScope(
                scope -> {
                    try {
                        scope.setValue(IOC.getIocKey(), new StrategyContainer());
                    } catch (Exception e) {
                        throw new Error(e);
                    }
                }
        );

        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope mainScope = ScopeProvider.getScope(keyOfMainScope);
        ScopeProvider.setCurrentScope(mainScope);

        IOC.register(
                IOC.getKeyForKeyByNameStrategy(),
                new ResolveByNameIocStrategy()
        );
        IKey keyIField = Keys.getKeyByName(IField.class.getCanonicalName());
        IOC.register(keyIField, new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    String fieldName = String.valueOf(args[0]);
                    try {
                        return new Field(new FieldName(fieldName));
                    } catch (InvalidArgumentException e) {
                        throw new RuntimeException("Can't resolve IField: ", e);
                    }
                }
        ));
    }

    @Test
    public void Should_MessageAttributeSettersSetMessageSubjectAndSenderSign()
            throws Exception {
        IObject context = new DSObject("{\"senderAddress\":\"some@address.ru\"}");
        IObject attributes = new DSObject("{\"subject\":\"Subj.\",\"sign\":\"Author\"}");
        MimeMessage mimeMessage = mock(MimeMessage.class);
        SMTPMessageAdaptor messageAdaptor = mock(SMTPMessageAdaptor.class);

        when(messageAdaptor.getMimeMessage()).thenReturn(mimeMessage);

        MessageAttributeSetters.applyAll(attributes, context, messageAdaptor);

        verify(mimeMessage).setSubject("Subj.");
        verify(mimeMessage).setFrom(new InternetAddress("Author<some@address.ru>"));
    }
}
