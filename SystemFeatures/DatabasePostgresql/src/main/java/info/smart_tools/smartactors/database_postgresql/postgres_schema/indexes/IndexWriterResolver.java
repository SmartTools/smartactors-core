package info.smart_tools.smartactors.database_postgresql.postgres_schema.indexes;

import info.smart_tools.smartactors.iobject.iobject.IObject;

/**
 * Functional interface to resolve IndexWriter.
 */
interface IndexWriterResolver {

    IndexWriter resolve(IObject indexDefinition) throws Exception;

}
