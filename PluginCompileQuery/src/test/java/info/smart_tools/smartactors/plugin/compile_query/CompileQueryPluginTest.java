//package info.smart_tools.smartactors.plugin.compile_query;
//
//import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
//import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
//import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
//import info.smart_tools.smartactors.core.db_storage.interfaces.StorageConnection;
//import info.smart_tools.smartactors.core.iaction.IPoorAction;
//import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
//import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
//import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
//import info.smart_tools.smartactors.core.ikey.IKey;
//import info.smart_tools.smartactors.core.ioc.IOC;
//import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
//import info.smart_tools.smartactors.core.named_keys_storage.Keys;
//import info.smart_tools.smartactors.core.sql_commons.QueryStatement;
//import info.smart_tools.smartactors.core.sql_commons.QueryStatementFactory;
//import org.junit.Before;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.mockito.ArgumentCaptor;
//import org.powermock.core.classloader.annotations.PrepareForTest;
//import org.powermock.modules.junit4.PowerMockRunner;
//
//import java.util.HashMap;
//
//import static org.junit.Assert.assertTrue;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.powermock.api.mockito.PowerMockito.*;
//
//@PrepareForTest({IOC.class, Keys.class, QueryKey.class, CompileQueryPlugin.class})
//@RunWith(PowerMockRunner.class)
//public class CompileQueryPluginTest {
//
//    private CompileQueryPlugin plugin;
//    private IBootstrap bootstrap;
//
//    @Before
//    public void setUp() throws ResolutionException {
//
//        mockStatic(IOC.class);
//        mockStatic(Keys.class);
//        mockStatic(QueryKey.class);
//
//        IKey key1 = mock(IKey.class);
//        IKey keyQuery = mock(IKey.class);
//        when(IOC.getKeyForKeyStorage()).thenReturn(key1);
//        when(IOC.resolve(eq(key1), eq(CompiledQuery.class.toString()))).thenReturn(keyQuery);
//
//        bootstrap = mock(IBootstrap.class);
//        plugin = new CompileQueryPlugin(bootstrap);
//    }
//
//    @Test
//    public void MustCorrectLoadPlugin() throws Exception {
//
//        IKey compiledQueryKey = mock(IKey.class);
//        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenReturn(compiledQueryKey);
//
//        HashMap<QueryKey, CompiledQuery> queryMap = mock(HashMap.class);
//        whenNew(HashMap.class).withNoArguments().thenReturn(queryMap);
//
//        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
//        whenNew(BootstrapItem.class).withArguments("CompileQueryPlugin").thenReturn(bootstrapItem);
//        when(bootstrapItem.after("IOC")).thenReturn(bootstrapItem);
//        plugin.load();
//
//        verifyNew(BootstrapItem.class).withArguments("CompileQueryPlugin");
//
//        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
//        verify(bootstrapItem).process(iPoorActionArgumentCaptor.capture());
//
//        iPoorActionArgumentCaptor.getValue().execute();
//
//        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor =
//                ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);
//        verifyStatic();
//        IOC.register(eq(compiledQueryKey), createNewInstanceStrategyArgumentCaptor.capture());
//
//        StorageConnection connection = mock(StorageConnection.class);
//        String task = "task";
//        QueryStatementFactory factory = mock(QueryStatementFactory.class);
//
//        String id = "id";
//        when(connection.getId()).thenReturn(id);
//
//        QueryKey queryKey = mock(QueryKey.class);
//        when(QueryKey.create(task, id)).thenReturn(queryKey);
//
//        QueryStatement queryStatement = mock(QueryStatement.class);
//        when(factory.create()).thenReturn(queryStatement);
//
//        CompiledQuery query = mock(CompiledQuery.class);
//        when(connection.compileQuery(queryStatement)).thenReturn(query);
//
//        assertTrue("Must be equal", createNewInstanceStrategyArgumentCaptor.getValue().resolve(connection, task, factory) == query);
//
//        verify(connection).getId();
//        verifyStatic();
//        QueryKey.create(task, id);
//        verify(queryMap).get(queryKey);
//
//        verify(factory).create();
//        verify(connection).compileQuery(queryStatement);
//        verify(queryMap).put(queryKey, query);
//
//        verify(bootstrap).add(eq(bootstrapItem));
//    }
//
//    @Test
//    public void MustIncorrectLoadPluginWhenKeysThrowException() throws Exception {
//
//        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenThrow(new ResolutionException(""));
//
//        try {
//            plugin.load();
//        } catch (PluginException e) {
//
//            verifyStatic();
//            Keys.getOrAdd(CompiledQuery.class.toString());
//            return;
//        }
//        assertTrue("Must throw exception", false);
//    }
//
//    @Test
//    public void MustInCorrectLoadPluginWhenKeysThrowException() throws Exception {
//
//        IKey compiledQueryKey = mock(IKey.class);
//        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenReturn(compiledQueryKey);
//
//        HashMap<QueryKey, CompiledQuery> queryMap = mock(HashMap.class);
//        whenNew(HashMap.class).withNoArguments().thenReturn(queryMap);
//
//        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
//        whenNew(BootstrapItem.class).withArguments("CompileQueryPlugin").thenReturn(bootstrapItem);
//        plugin.load();
//
//        verifyNew(BootstrapItem.class).withArguments("CompileQueryPlugin");
//
//        ArgumentCaptor<IPoorAction> iPoorActionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
//        verify(bootstrapItem).process(iPoorActionArgumentCaptor.capture());
//
//        iPoorActionArgumentCaptor.getValue().execute();
//
//        ArgumentCaptor<CreateNewInstanceStrategy> createNewInstanceStrategyArgumentCaptor =
//                ArgumentCaptor.forClass(CreateNewInstanceStrategy.class);
//        verifyStatic();
//        IOC.register(eq(compiledQueryKey), createNewInstanceStrategyArgumentCaptor.capture());
//
//        StorageConnection connection = mock(StorageConnection.class);
//        String task = "task";
//        QueryStatementFactory factory = mock(QueryStatementFactory.class);
//
//        String id = "id";
//        when(connection.getId()).thenReturn(id);
//
//        QueryKey queryKey = mock(QueryKey.class);
//        when(QueryKey.create(task, id)).thenReturn(queryKey);
//
//        QueryStatement queryStatement = mock(QueryStatement.class);
//        when(factory.create()).thenReturn(queryStatement);
//
//        CompiledQuery query = mock(CompiledQuery.class);
//        when(connection.compileQuery(queryStatement)).thenReturn(query);
//
//        assertTrue("Must be equal", createNewInstanceStrategyArgumentCaptor.getValue().resolve(connection, task, factory) == query);
//
//        verify(connection).getId();
//        verifyStatic();
//        QueryKey.create(task, id);
//        verify(queryMap).get(queryKey);
//
//        verify(factory).create();
//        verify(connection).compileQuery(queryStatement);
//        verify(queryMap).put(queryKey, query);
//
//        verify(bootstrap).add(eq(bootstrapItem));
//    }
//
//    @Test
//    public void MustInCorrectExecuteInIPoorActionWhenThrowRegistrationException() throws Exception {
//
//        IKey cachedCollectionKey = mock(IKey.class);
//        when(Keys.getOrAdd(CompiledQuery.class.toString())).thenReturn(cachedCollectionKey);
//
//        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
//        whenNew(BootstrapItem.class).withArguments("CompileQueryPlugin").thenReturn(bootstrapItem);
//        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
//
//        HashMap<String, CompiledQuery> collectionMap = mock(HashMap.class);
//        whenNew(HashMap.class).withNoArguments().thenReturn(collectionMap);
//
//        plugin.load();
//
//        verifyStatic();
//        Keys.getOrAdd(CompiledQuery.class.toString());
//
//        verifyNew(BootstrapItem.class).withArguments("CompileQueryPlugin");
//
//        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
//        verify(bootstrapItem).process(actionArgumentCaptor.capture());
//
//        doThrow(new RegistrationException("")).when(IOC.class);
//        IOC.register(eq(cachedCollectionKey), any());
//
//        try {
//            actionArgumentCaptor.getValue().execute();
//        } catch (RuntimeException e) {
//            verifyStatic();
//            IOC.register(eq(cachedCollectionKey), any());
//
//            verifyNew(HashMap.class).withNoArguments();
//            verify(bootstrap).add(bootstrapItem);
//            return;
//        }
//        assertTrue("Must throw exception", false);
//    }
//}
