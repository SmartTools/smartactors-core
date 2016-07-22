package info.smart_tools.smartactors.core.message_to_bytes_mapper;


import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.SerializeException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
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
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy()
        );
        IKey keyIObject = Keys.getOrAdd(DSObject.class.toString());
        IKey keyString = Keys.getOrAdd(String.class.toString());
        IKey keyEmptyIObject = Keys.getOrAdd("EmptyIObject");
        IOC.register(keyIObject,
                new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new DSObject((String) args[0]);
                            } catch (InvalidArgumentException ignored) {
                            }
                            return null;
                        }
                )
        );
        IOC.register(keyEmptyIObject,
                new CreateNewInstanceStrategy(
                        (args) -> new DSObject()
                )
        );
        IOC.register(keyString,
                new CreateNewInstanceStrategy(
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
        IObject iObject = IOC.resolve(Keys.getOrAdd(DSObject.class.toString()), "{\"hello\": \"world\"}");
        byte[] bytes = iObject.serialize().toString().getBytes();
        IObject iObject2 = mapper.deserialize(bytes);
        verify(iObject.serialize().equals(iObject2.serialize()));
    }
}
