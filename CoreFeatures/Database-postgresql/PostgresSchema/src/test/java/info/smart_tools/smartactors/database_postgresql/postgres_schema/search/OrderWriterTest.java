package info.smart_tools.smartactors.database_postgresql.postgres_schema.search;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.database.database_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.QueryStatement;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.StringWriter;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class OrderWriterTest {

    private OrderWriter orderWriter;
    private StringWriter body;
    private QueryStatement query;

    @BeforeClass
    public static void prepareIOC() throws PluginException, ProcessExecutionException {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new IFieldNamePlugin(bootstrap).load();
        new PluginDSObject(bootstrap).load();
        bootstrap.start();
    }

    @Before
    public void setUp() throws Exception {
        orderWriter = new OrderWriter();
        body = new StringWriter();

        query = mock(QueryStatement.class);
        when(query.getBodyWriter()).thenReturn(body);

        IOC.register(Keys.getKeyByName(IFieldName.class.getCanonicalName()), new CreateNewInstanceStrategy(
                        (args) -> {
                            try {
                                return new FieldName((String) args[0]);
                            } catch (InvalidArgumentException e) {
                                return null;
                            }
                        }
                )
        );
    }

    @Test
    public void should_WritesORDERClauseIntoQueryStatement() throws Exception {
        IObject criteriaMessage = new DSObject("{ \"sort\": [ { \"testField\": \"desc\" }, { \"anotherTestField\": \"asc\" } ] }");
        List<IObject> sortMessage = (List<IObject>) criteriaMessage.getValue(new FieldName("sort"));
        orderWriter.write(query, sortMessage);
        assertEquals("ORDER BY(document#>'{testField}')DESC,(document#>'{anotherTestField}')ASC", body.toString());
        verify(query, times(0)).pushParameterSetter(any());
    }

    @Test
    public void should_WritesORDERClauseWithTypeCastIntoQueryStatement() throws Exception {
        IObject criteriaMessage = new DSObject(
                "{ \"sort\": [ { \"testField\": {\"direction\": \"desc\", \"type\": \"decimal\"} }, { \"anotherTestField\": \"asc\" } ] }");
        List<IObject> sortMessage = (List<IObject>) criteriaMessage.getValue(new FieldName("sort"));
        orderWriter.write(query, sortMessage);
        assertEquals("ORDER BY((document#>>'{testField}')::decimal)DESC,(document#>'{anotherTestField}')ASC", body.toString());
        verify(query, times(0)).pushParameterSetter(any());
    }

    @Test(expected = QueryBuildException.class)
    public void should_FailOnWrongDirection() throws Exception {
        IObject criteriaMessage = new DSObject("{ \"sort\": [ { \"testField\": 1 } ] }");
        List<IObject> sortMessage = (List<IObject>) criteriaMessage.getValue(new FieldName("sort"));
        orderWriter.write(query, sortMessage);
        fail();
    }

}
