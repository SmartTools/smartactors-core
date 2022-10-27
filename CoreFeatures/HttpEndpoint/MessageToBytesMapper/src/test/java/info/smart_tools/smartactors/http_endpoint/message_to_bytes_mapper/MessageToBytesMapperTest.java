package info.smart_tools.smartactors.http_endpoint.message_to_bytes_mapper;


import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
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

import static com.google.common.base.Verify.verify;

public class MessageToBytesMapperTest {
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
        IKey keyIObject = Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject");
        IKey keyString = Keys.getKeyByName(String.class.toString());
        IKey keyEmptyIObject = Keys.getKeyByName("EmptyIObject");
        IOC.register(
                keyIObject,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                return new DSObject((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(
                keyEmptyIObject,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new DSObject()
                )
        );
        IOC.register(
                keyString,
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new String((byte[]) args[0])
                )
        );
    }

    @Test
    public void messageToBytesMapperShouldReturnEmptyIObject_WhenByteArrayOnDeserializationIsEmpty() throws ResolutionException {
        MessageToBytesMapper mapper = new MessageToBytesMapper();
        IObject iObject = mapper.deserialize(new byte[0]);
        verify(!iObject.iterator().hasNext());
    }
    @Test
    public void messageToBytesMapperShouldReturnDeserializedIObject() throws ResolutionException, SerializeException {
        MessageToBytesMapper mapper = new MessageToBytesMapper();
        IObject iObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{\"hello\": \"world\"}");
        byte[] bytes = iObject.serialize().toString().getBytes();
        IObject iObject2 = mapper.deserialize(bytes);
        verify(iObject.serialize().equals(iObject2.serialize()));
    }
    @Test
    public void messageToBytesMapperShouldDeleteNonASCIICharacters() throws ResolutionException, SerializeException {
        MessageToBytesMapper mapper = new MessageToBytesMapper();
        String stringWithNonASCII = "\uFEFF{\"hello\": \"world\"}";
        IObject iObject = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{\"hello\": \"world\"}");
        byte[] bytes = stringWithNonASCII.getBytes();
        IObject iObject2 = mapper.deserialize(bytes);
        verify(iObject.serialize().equals(iObject2.serialize()));
    }
}
