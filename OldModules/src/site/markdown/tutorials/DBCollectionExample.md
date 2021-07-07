# How to work with database collections

## Overview

Collection is the set of documents stored in a database.
We know the document can be presented as [JSON](https://en.wikipedia.org/wiki/JSON) string or [IObject](IObjectExample.html).

Each collection has it's name — the string of alphanumeric characters and `_`.

The only sign the object belongs to the collection is existence in the object the field named "collection_nameID". This field contains the unique identifier of the object in this collection.

For example, if the document is in the "forms" collections, it has the field "formsID". The value of the field is the unique identifier of this document in the collection "forms".

The document can be in multiple collections at the same time. It has multiple ID fields in such case.

## Database query

You should do the following to perform a database query.

1. Resolve [IoC](IOCExample.html) dependency to take command object which interacts with DB.

        ITask task = IOC.resolve(
            Keys.getKeyByName("command_name"),          // each command has it's own unique name
            connection,                             // object - connection to the DB
            "collection_name",                      // each document belongs to the collection
            other comma-separated parameters        // the set of parameters depends on the command
        );

    If the dependency cannot be resolved, `IOC.resolve` throws `ResolveDependencyException`.

2. Execute the task.

        task.execute();

    If the command cannot be executed, `task.execute()` throws `TaskExecutionException`.

## Database commands

### Create collection

Creates the collection in the database to be used in the following database queries.
Use this task on an initialization step, to create all necessary collections on the first server start.

Command name: `db.collection.create`

Additional parameters:

- options — `IObject` — object specifying the set of additional options for the collection creation.

#### Indexes

ID of the document is always the primary key, so GetById always use index. 
You can specify additional indexes to do Search more effectively as options.

The example of options object with indexes:

    {
        "ordered": [ "a", "b" ],
        "datetime": "date",
        "tags": "tags",
        "fulltext": "text",
        "language": "english"
    }

You should define a field or array of fields for each possible index type in the options.
Also some additional parameters may be required.

Available index types:

* `ordered` — typical btree ordered index, to be used to test for equality, ranges, for sorting, etc...
* `datetime` — btree index containing conversion to datetime, use it to optimize `$date-from` and `$date-to` search operators.
* `tags` — an index for fields containing arrays of tags, use it to optimize `$hasTag` search operator. 
* `fulltext` — full text index, requires additional `language` option, use it to optimize `$fulltext` search operator.

Ordered, datetime and tags indexes are created for each field independently, so queries for all specified fields can be done in any combination.
However, fulltext index is only one for the collection, the texts from all specified fields are concatenated for the indexing.

### Upsert

Adds the new object to the collection or updates the existed object.

Command name: `db.collection.upsert`

Additional parameters:

- document — `IObject` — object which should be inserted/updated in the DB

The Upsert command checks the existence of value of the "collection_nameID" field, if the ID field is absent it inserts the document, otherwise it updates the document.
When the document is successfully inserted, the command adds the field "collection_nameID" to the document.

#### Example

    ITask task = IOC.resolve(
            Keys.getKeyByName("db.collection.upsert"),
            connection,
            collectionName,
            document
    );
    task.execute();

### Insert

Adds new object to the collection.

Command name: `db.collection.insert`

If the document contains the field "collection_nameID" the command throws a successor of `TaskPrepareException`. In other cases it's behavior is the same as of Upsert command.

It's recommended to use Upsert if there is no strong necessity to use Insert.

### Delete

Deletes the object from the collection.

Command name: `db.collection.delete`

Additional parameters:

- document — `IObject` — the object which should be deleted from the DB collection.

The object must have the field "collection_nameID" which contains the unique identifier of the document in the collection.

When this field is present in the document, it's be tried to delete the object from the collection, the field "collection_nameID" is deleted from the document.

If the document is absent in the collection, no error appears because the absence of the document with the specified id is the target postcondition, the field "collection_nameID" is deleted from the in-memory document.
 
#### Example
 
    ITask task = IOC.resolve(
            Keys.getKeyByName("db.collection.delete"),
            connection,
            collectionName,
            document
    );
    task.execute();

### GetById

Takes the document by it's id.

Command name: `db.collection.getbyid`

Additional parameters:

- id — unique identifier of the document in the collection
- callback — lambda of type `IAction<IObject>` which receives the document got by id

If the document with such id does not exist, the `TaskExecutionException` is thrown with `DocumentNotFoundException` cause.

#### Example

    ITask task = IOC.resolve(
            Keys.getKeyByName("db.collection.getbyid"),
            connection,
            collectionName,
            documentiId,
            (IAction<IObject>) foundDoc -> {
                try {
                    System.out.println("Found by id");
                    System.out.println((String) doc.serialize());
                } catch (SerializeException e) {
                    throw new ActionExecutionException(e);
                }
            }
    );
    task.execute();

### Search

Searching of the document in the collection.

Command name: `db.collection.search`

Additional parameters:

- criteria — search criteria for documents which should be selected from the collection, the `IObject` document
- callback — lambda of type `IAction<IObject[]>` which receives the set of selected documents

If no documents for the specified criteria were found, the callback function receives empty array.
 
#### Criteria
 
The search criteria is the complex IObject which contains three parts: filter, pagination control and sorting order.
For example, it may look like this.

    {
        "filter": {
            "$or": [
                { "a": { "$eq": "b" } },
                { "b": { "$gt": 42 } }
            ]
        },
        "page": {
             "size": 50,
             "number": 2
        },
        "sort": [
             { "a": "asc" },
             { "b": "desc" }
         ]
    }

##### Filter

Filter is the criterion to filter the resulting documents. 
It's the equivalent of SQL WHERE clause.

The filter is the set of conditions and operators.
Conditions join operators together.
Operators match the specified document field against the specified criteria.

Available conditions:

* `$and` — ANDs operators and nested conditions
* `$or` — ORs operators and nested conditions
* `$not` — negate all nested operators and conditions, is equivalent to NOT(conditionA AND conditionB)
   
Available operators:

* `$eq` — test for equality of the document field and the specified value
* `$ne` — test for not equality
* `$lt` — "less than", the document field is less than the specified value
* `$gt` — "greater than", the document field is larger than the specified value
* `$lte` — less or equal
* `$gte` — greater or equal
* `$isNull` — checks for null if the specified value is "true" or checks for not null if "false"
* `$date-from` — greater or equal for datetime fields
* `$date-to` — less or equal for datetime fields
* `$in` — checks for equality to any of the specified values in the array
* `$hasTag` — check the document field which is an array of tags contains the specified tag value
* `$fulltext` — full text search over a text field, the fulltext index on the collection is required (see above)

It's possible to check nested fields using dot-separated syntax.
 
    {
        "filter":
            { "a.b.c": { "$eq": 123 } }
    }
    
Multiple conditions for the same field can be defined. It implies AND relations between then, all conditions must be satisfied.
 
    {
        "filter":
            { "finished": { "$gt": 15, "$lt": 20 } }
    }
     
Also multiple conditions for different fields can be ANDed implicitly too.

    {
        "filter":
            { "status": { "$eq": "A" }, "age": { "$lt": 30 } }
    }            

###### Fulltext search

Fulltext search has a special, more complex syntax.

It doesn't require the document field because the search is done over pre-indexed fields defined during the collection creation.
So, in the simplest form the fulltext filter may look like this.

    {
        "filter": { 
            "$fulltext": "term1 term2" 
        }
    }
     
However, it's better to define the language for the fulltext query explicitly.

    {
        "filter": { 
            "$fulltext": {
                "query": "term1 term2",
                "language": "english"
            } 
        }
    }
        
The query can be more complex to join search terms with different conditions.

    {
        "filter": { 
            "$fulltext": {
                "query": {
                    "$or": [ "term1",  "term2" ]
                },
                "language": "english"
            } 
        }
    }

##### Page

Page criterion is used for pagination.
You may define the page size and page number.

This is equivalent of SQL LIMIT and OFFSET clauses.
However, here you must work in terms of pages, while SQL works in terms of rows to skip.

If the pagination is not defined, only first 100 documents from the collection are returned, i.e. page number 1 of size 100.
Also if the page size is more than 1000, it's limited to 1000.

##### Sort

Sort criterion is used to determine the order of documents in the result.
It's the equivalent of SQL ORDER BY clause.

Because the sorting order is important, you define the ordered array of sort conditions.
You define the pair: the document field name and the sort direction: "asc" or "desc".
    
#### Example
    
    ITask task = IOC.resolve(
            Keys.getKeyByName("db.collection.search"),
            connection,
            collectionName,
            new DSObject(String.format(
                "{ " +
                    "\"filter\": { \"%1$s\": { \"$eq\": \"new value\" } }," +
                    "\"page\": { \"size\": 2, \"number\": 2 }," +
                    "\"sort\": [ { \"%1$s\": \"asc\" } ]" +
                "}",
                testField.toString())),
            (IAction<IObject[]>) docs -> {
                try {
                    for (IObject doc : docs) {
                        System.out.println("Found by " + testField);
                        System.out.println((String) doc.serialize());
                    }
                } catch (SerializeException e) {
                    throw new ActionExecutionException(e);
                }
            }
    );
    task.execute();    

### Count

Counts the number of documents in the collection matching specified criteria.

Command name: `db.collection.count`

Additional parameters:

- criteria — search criteria for documents which should be counted in the collection, the `IObject` document
- callback — lambda of type `IAction<Long>` which receives the count of found documents

Search criteria are the same as for Search command described above. However, only `filter` part of it is taken.

If no documents for the specified criteria were found, the callback function receives zero.

It's recommended to avoid to use this task because the counting can be as slow as selecting the same documents using Search task. 
Try to store and update the desired counter separately and explicitly.

## More complete example

Get the document by id.

    public interface IGetDocumentMessage {
        CollectionName collectionName();
        string id();
        void document(IObject doc);
    };

    public class MessageHandler {

        void Handle(final IGetDocumentMessage mes) {
    
            IPool pool = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"));
            try (PoolGuard guard = new PoolGuard(pool)) {
                 
                ITask task = IOC.resolve(
                    Keys.getKeyByName("db.collection.getbyid"),
                    guard.getObject(),
                    mes.collectionName(),
                    mes.id(),
                    (doc) -> { mes.document(doc); }
            );
             
            task.execute();
            
        }
    }

Also see the sample [server implementation](../xref/info/smart_tools/smartactors/core/examples/db_collection/package-frame.html) for details.

Implementation details:

* [PostgreSQL implementation](db_collection/PostgresDBCollections.html)
* [In-memory implementaion](db_collection/InMemoryDBCollections.html)
