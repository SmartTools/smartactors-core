package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.database.interfaces.idatabase.exception.IDatabaseException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.SerializeException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class InMemoryDatabaseTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }


    @Override
    protected void registerMocks() throws Exception {
        InMemoryDatabaseIOCInitializer.init();
    }

    @Test
    public void testGetByIdExistingElem() throws IDatabaseException, InvalidArgumentException, SerializeException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        database.insert(document, "collection_name");

        Object id = document.getValue(new FieldName("collection_nameID"));
        IObject outputDocument = database.getById(id, "collection_name");

        assertTrue(document.serialize().equals(outputDocument.serialize()));
    }

    @Test(expected = IDatabaseException.class)
    public void testGetByIdNotExistingElem() throws IDatabaseException, InvalidArgumentException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject outputDocument = database.getById(1, "collection_name");
    }

    @Test
    public void testInsert_shouldAddId() throws InvalidArgumentException, IDatabaseException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        database.insert(document, "collection_name");
        Object id = document.getValue(new FieldName("collection_nameID"));
        assertTrue(id instanceof String);
    }

    @Test
    public void testUpsertAsInsert_shouldAddId() throws InvalidArgumentException, IDatabaseException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        database.upsert(document, "collection_name");
        Object id = document.getValue(new FieldName("collection_nameID"));
        assertTrue(id instanceof String);
    }

    @Test
    public void testUpsertAsUpdate_shouldNotChangeId() throws InvalidArgumentException, IDatabaseException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        database.upsert(document, "collection_name");
        Object id = document.getValue(new FieldName("collection_nameID"));
        assertTrue(id instanceof String);
        database.upsert(document, "collection_name");
        assertEquals(id, document.getValue(new FieldName("collection_nameID")));
    }

    @Test
    public void testUpdate_shouldNotChangeId() throws InvalidArgumentException, IDatabaseException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        database.insert(document, "collection_name");
        Object id = document.getValue(new FieldName("collection_nameID"));
        assertTrue(id instanceof String);
        database.update(document, "collection_name");
        assertEquals(id, document.getValue(new FieldName("collection_nameID")));
    }

    @Test
    public void testSearchEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        List<IObject> outputList =
                database.select(new DSObject("{\"filter\":{\"hello\": {\"$eq\": \"world\"}}}"), "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testSearchEqAtEmptyDB() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        List<IObject> outputList =
                database.select(new DSObject("{\"filter\":{\"hello\": {\"$eq\": \"world\"}}}"), "collection_name");
        assertTrue(outputList.size() == 0);
    }

    @Test
    public void testSearchAndEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        List<IObject> outputList =
                database.select(new DSObject("{\"filter\":{\"$and\": [{\"hello\": {\"$eq\": \"world\"}}]}}"), "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testSearchOrEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        IObject document3 = new DSObject("{\"hello\": \"world2\"}");
        IObject document4 = new DSObject("{\"hello\": \"world3\"}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"$or\": [{\"hello\": {\"$eq\": \"world\"}}, {\"hello\": {\"$eq\": \"world2\"}}]}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
    }

    @Test
    public void testSearchNotOrEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{\"hello\": \"world1\"}");
        IObject document2 = new DSObject("{\"hello\": \"world2\"}");
        IObject document3 = new DSObject("{\"hello\": \"world3\"}");
        IObject document4 = new DSObject("{\"hello\": \"world4\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{" +
                                "\"filter\": {" +
                                    "\"$and\": [ " +
                                        "{\"$not\": [" +
                                            "{\"$or\": [" +
                                                "{\"hello\": {\"$eq\": \"world1\"}}, " +
                                                "{\"hello\": {\"$eq\": \"world3\"}}" +
                                            "]}" +
                                        "]}" +
                                    "]" +
                                "}" +
                            "}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document4.serialize()));
    }

    @Test
    public void testSearchGtForNumbers() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": 1}");
        IObject document2 = new DSObject("{\"a\": 2}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$gt\": 2.3}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document4.serialize()));
    }

    @Test
    public void testSearchLtForNumbers() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": 1}");
        IObject document2 = new DSObject("{\"a\": 2.3}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$lt\": 2.3}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testSearchGteForNumbers() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": 1}");
        IObject document2 = new DSObject("{\"a\": 2.3}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$gte\": 2.3}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 3);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(2).serialize().equals(document4.serialize()));
    }

    @Test
    public void testSearchLteForNumbers() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": 1}");
        IObject document2 = new DSObject("{\"a\": 2.3}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$lte\": 2.3}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document2.serialize()));
    }

    @Test
    public void testSearchIn() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": 1}");
        IObject document2 = new DSObject("{\"a\": 2.3}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$in\": [2.3, 3, 4, 5]}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
    }

    @Test
    public void testNestedEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}}");
        IObject document2 = new DSObject("{\"a\": {\"b\": 2}}");
        IObject document3 = new DSObject("{\"a\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a.b\": {\"$eq\": 1}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testIsNull() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}}");
        IObject document2 = new DSObject("{\"c\": {\"b\": 2}}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$isNull\": true}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
    }

    @Test
    public void testHasTagField() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}}");
        IObject document2 = new DSObject("{\"c\": {\"b\": 2}}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$hasTag\": \"b\"}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testHasTagAtList() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": [\"b\", 1]}");
        IObject document2 = new DSObject("{\"c\": [\"b\", 2]}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": {\"b\": 3}}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"$and\": [{\"a\": {\"$hasTag\": \"b\"}}]}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document4.serialize()));
    }

    @Test
    public void testPagingOneElem() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": [\"b\", 1]}");
        IObject document2 = new DSObject("{\"c\": [\"b\", 2]}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": {\"b\": 3}}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{}, \"page\": {\"size\": 1, \"number\":1}}"),
                        "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
    }

    @Test
    public void testPagingSomeElems() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": [\"b\", 1]}");
        IObject document2 = new DSObject("{\"c\": [\"b\", 2]}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": {\"b\": 3}}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{}, \"page\": {\"size\": 2, \"number\":1}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document2.serialize()));
    }

    @Test
    public void testPagingWithEmptyResult() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": [\"b\", 1]}");
        IObject document2 = new DSObject("{\"c\": [\"b\", 2]}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": {\"b\": 3}}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{}, \"page\": {\"size\": 1, \"number\":5}}"),
                        "collection_name");
        assertTrue(outputList.size() == 0);
    }

    @Test
    public void testSortingAscOneField() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"c\": 0}");
        IObject document2 = new DSObject("{\"c\": 5}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 1}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"sort\": [{\"c\": \"asc\"}]}"),
                        "collection_name");
        assertTrue(outputList.size() == 5);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document5.serialize()));
        assertTrue(outputList.get(2).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(3).serialize().equals(document4.serialize()));
        assertTrue(outputList.get(4).serialize().equals(document2.serialize()));
    }

    @Test
    public void testSortingDescOneField() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"c\": 0}");
        IObject document2 = new DSObject("{\"c\": 5}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 1}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"sort\": [{\"c\": \"desc\"}]}"),
                        "collection_name");
        assertTrue(outputList.size() == 5);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document4.serialize()));
        assertTrue(outputList.get(2).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(3).serialize().equals(document5.serialize()));
        assertTrue(outputList.get(4).serialize().equals(document.serialize()));
    }

    @Test
    public void testSortingByNotExistingField() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{}");
        IObject document2 = new DSObject("{\"c\": 5}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 1}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"sort\": [{\"c\": \"desc\"}]}"),
                        "collection_name");
        assertTrue(outputList.size() == 5);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document4.serialize()));
        assertTrue(outputList.get(2).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(3).serialize().equals(document5.serialize()));
        assertTrue(outputList.get(4).serialize().equals(document.serialize()));
    }

    @Test
    public void testSortingByManyFields() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{}");
        IObject document2 = new DSObject("{\"c\": 5, \"a\": 3}");
        IObject document3 = new DSObject("{\"c\": 5, \"a\": 2}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 3, \"a\": 4}");
        IObject document6 = new DSObject("{\"c\": 3, \"a\": 2}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        database.insert(document6, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"sort\": [{\"c\": \"desc\"}, {\"a\": \"asc\"}]}"),
                        "collection_name");
        assertTrue(outputList.size() == 6);
        assertTrue(outputList.get(0).serialize().equals(document3.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(2).serialize().equals(document4.serialize()));
        assertTrue(outputList.get(3).serialize().equals(document6.serialize()));
        assertTrue(outputList.get(4).serialize().equals(document5.serialize()));
    }

    @Test
    public void testDeleteElem() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{}");
        IObject document2 = new DSObject("{\"c\": 5, \"a\": 3}");
        IObject document3 = new DSObject("{\"c\": 5, \"a\": 2}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 3, \"a\": 4}");
        IObject document6 = new DSObject("{\"c\": 3, \"a\": 2}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        database.insert(document6, "collection_name");
        database.delete(document, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject(),
                        "collection_name");
        assertTrue(outputList.size() == 5);
    }

    @Test
    public void testDeleteIdFieldAfterDeleteDocument() throws InvalidArgumentException, IDatabaseException, SerializeException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{}");
        IObject document2 = new DSObject("{\"c\": 5, \"a\": 3}");
        IObject document3 = new DSObject("{\"c\": 5, \"a\": 2}");
        IObject document4 = new DSObject("{\"c\": 4}");
        IObject document5 = new DSObject("{\"c\": 3, \"a\": 4}");
        IObject document6 = new DSObject("{\"c\": 3, \"a\": 2}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        database.insert(document6, "collection_name");
        database.delete(document, "collection_name");
        assertTrue(null == document.getValue(new FieldName("collection_nameID")));
    }

    @Test(expected = IDatabaseException.class)
    public void testInsertToNotExistingCollection() throws InvalidArgumentException, IDatabaseException, SerializeException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        IObject document = new DSObject("{}");
        database.insert(document, "collection_name");
    }

    @Test(expected = IDatabaseException.class)
    public void testUpsertToNotExistingCollection() throws InvalidArgumentException, IDatabaseException, SerializeException, ReadValueException {
        InMemoryDatabase database = new InMemoryDatabase();
        IObject document = new DSObject("{}");
        database.upsert(document, "collection_name");
    }

    @Test
    public void testShortRecordOneCondition() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}}");
        IObject document2 = new DSObject("{\"c\": {\"b\": 2}}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"a\": {\"$isNull\": true}}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
    }

    @Test
    public void testShortRecordManyConditions() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}}");
        IObject document2 = new DSObject("{\"c\": {\"b\": 2}}");
        IObject document3 = new DSObject("{\"c\": 3}");
        IObject document4 = new DSObject("{\"a\": 3.4}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{\"a\": {\"$isNull\": true}, \"c\": {\"$eq\": 3}}}"),
                        "collection_name");
        assertTrue(outputList.size() == 1);
        assertTrue(outputList.get(0).serialize().equals(document3.serialize()));
    }

    @Test
    public void testLongSelect() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"a\": {\"b\": 1}, \"daily\": 3}");
        IObject document2 = new DSObject("{\"c\": {\"b\": 2}, \"daily\": -1}");
        IObject document3 = new DSObject("{\"c\": 3, \"daily\": 3}");
        IObject document4 = new DSObject("{\"a\": 7, \"daily\": 3}");
        IObject document5 = new DSObject("{\"a\": 3, \"monthly\": -1}");
        IObject document6 = new DSObject("{\"a\": 7, \"weekly\": 1}");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        database.insert(document4, "collection_name");
        database.insert(document5, "collection_name");
        database.insert(document6, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{\"filter\":{" +
                                "              \"a\": {\n" +
                                "                \"$eq\": 7\n" +
                                "              },\n" +
                                "              \"$or\": [\n" +
                                "                {\n" +
                                "                  \"daily\": {\n" +
                                "                    \"$gt\": 0\n" +
                                "                  }\n" +
                                "                },\n" +
                                "                {\n" +
                                "                  \"weekly\": {\n" +
                                "                    \"$gt\": 0\n" +
                                "                  }\n" +
                                "                },\n" +
                                "                {\n" +
                                "                  \"monthly\": {\n" +
                                "                    \"$gt\": 0\n" +
                                "                  }\n" +
                                "                }\n" +
                                "              ]" +
                                "}}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document4.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document6.serialize()));
    }

    @Test
    public void testFullTextSelect() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document = new DSObject("{\"text\": \"sample text\" }");
        IObject document2 = new DSObject("{\"text\": \"another text\" }");
        IObject document3 = new DSObject("{\"text\": \"txt shouldn't be found\" }");
        database.insert(document, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{ \"filter\": { \"text\": { \"$fulltext\": \"text\" } } }"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document2.serialize()));
    }

    @Test
    public void testCountEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        long count =
                database.count(new DSObject("{\"filter\":{\"hello\": {\"$eq\": \"world\"}}}"), "collection_name");
        assertEquals(1L, count);
    }

    @Test
    public void testCountWithNullCondition() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        long count = database.count(null, "collection_name");
        assertEquals(2L, count);
    }

    @Test
    public void testCountWithEmptyFilter() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        long count = database.count(new DSObject("{\"filter\":{} }"), "collection_name");
        assertEquals(2L, count);
    }

    @Test
    public void testCountWithEmptyCondition() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{\"hello\": \"world\"}");
        IObject document2 = new DSObject("{\"hello\": \"world1\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        long count = database.count(new DSObject("{ }"), "collection_name");
        assertEquals(2L, count);
    }

    @Test
    public void testSearchNotEq() throws InvalidArgumentException, IDatabaseException, SerializeException {
        InMemoryDatabase database = new InMemoryDatabase();
        database.createCollection("collection_name");
        IObject document1 = new DSObject("{ \"hello\": \"earth\", \"bye\": \"mars\" }");
        IObject document2 = new DSObject("{ \"hello\": \"mars\", \"bye\": \"earth\" }");
        IObject document3 = new DSObject("{\"hello\": \"earth\"}");
        database.insert(document1, "collection_name");
        database.insert(document2, "collection_name");
        database.insert(document3, "collection_name");
        List<IObject> outputList =
                database.select(
                        new DSObject("{ " +
                                "\"filter\": { " +
                                "\"$not\": [" +
                                "{ \"hello\": { \"$eq\": \"earth\" } }, " +
                                "{ \"bye\": { \"$eq\": \"mars\" } }" +
                                "]" +
                                "}" +
                                "}"),
                        "collection_name");
        assertTrue(outputList.size() == 2);
        assertTrue(outputList.get(0).serialize().equals(document2.serialize()));
        assertTrue(outputList.get(1).serialize().equals(document3.serialize()));
    }

}
