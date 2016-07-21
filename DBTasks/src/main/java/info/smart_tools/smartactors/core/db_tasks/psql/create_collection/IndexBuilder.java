package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * The builder for query of creation of indexes to some collection.
 */
final class IndexBuilder {
    private String index;
    private String collection;
    private String field;

    /** A indexes creation templates. */
    private Map<String, List<String[]>> indexCreationTemplates = new HashMap<String, List<String[]>>() {
        {
            put("ordered", Collections.singletonList(new String[] { "CREATE INDEX ON ", " USING BTREE ((", "));\n" }));
            put("tags", Collections.singletonList(new String[] { "CREATE INDEX ON ", " USING GIN ((", "));\n" }));
            put("fulltext", Collections.singletonList(new String[] { "CREATE INDEX ON ",
                    " USING GIN ((to_tsvector('russian',(", ")::text)));\n" }));
            put("datetime", Collections.singletonList(new String[] { "CREATE INDEX ON ",
                    " USING BTREE ((parse_timestamp_immutable(", ")));\n" }));
            put("id", Arrays.asList(new String[] { "CREATE INDEX ON ", " USING BTREE ((", "));\n" },
                    new String[] {"CREATE INDEX ON ", " USING HASH ((", "));\n" }));
        }
    };

    /** A length of indexes templates. */
    private Map<String, Integer> indexTemplatesLength = new HashMap<String, Integer>() {
        private final int orderTemplateLength = 35,
                tagsTemplateLength = 33,
                fulltextTemplateLength = 64,
                datetimeTemplateLength = 62,
                idTemplateLength = 69;

        {
            put("ordered", orderTemplateLength);
            put("tags", tagsTemplateLength);
            put("fulltext", fulltextTemplateLength);
            put("datetime", datetimeTemplateLength);
            put("id", idTemplateLength);
        }
    };

    /**
     * Default constructor for {@link IndexBuilder}.
     *              Creates a new instance of {@link IndexBuilder}.
     */
    private IndexBuilder() {}

    /**
     * Factory method for creation a new instance of {@link IndexBuilder}.
     *
     * @return a new instance of {@link IndexBuilder}.
     */
    static IndexBuilder create() {
        return new IndexBuilder();
    }

    /**
     * Appends collection which builds index.
     *
     * @param collectionName - the collection name which builds index.
     *
     * @return a link to yourself {@link IndexBuilder}.
     */
    IndexBuilder withCollection(@Nonnull final String collectionName) {
        collection = collectionName;
        return this;
    }

    /**
     * Appends index type for which you want to create.
     *
     * @param queryIndex - index type.
     *
     * @return a link to yourself {@link IndexBuilder}.
     */
    IndexBuilder withIndex(@Nonnull final String queryIndex) {
        if (!indexCreationTemplates.containsKey(queryIndex)) {
            throw new IllegalArgumentException("Index type - " + queryIndex + " not supported.");
        }

        index = queryIndex;
        return this;
    }

    /**
     * Appends field for which you want create a index.
     *
     * @param indexedField - field for which you want create a index
     *
     * @return a link to yourself {@link IndexBuilder}.
     */
    IndexBuilder withField(@Nonnull final String indexedField) {
        field = indexedField;
        return this;
    }

    /**
     * Builds query for creation of some index to some collection.
     *
     * @return a query for creation index to some collection.
     *
     * @throws QueryBuildException when one of the index or field are null or empty.
     */
    String build() throws QueryBuildException {
        requiresNonnull(index, "The index should not be a null or empty, should try invoke 'withIndex'.");
        requiresNonnull(collection, "The collection should not be a null or empty, should try invoke 'withCollection'.");
        requiresNonnull(field, "The field should not be a null or empty, should try invoke 'withField'.");

        StringBuilder indexBuilder = new StringBuilder(indexTemplatesLength.get(index) +
                collection.length() + field.length());
        List<String[]> templates = indexCreationTemplates.get(index);
        for (String[] template : templates) {
            indexBuilder
                    .append(template[0])
                    .append(collection)
                    .append(template[1])
                    .append(field)
                    .append(template[2]);
        }

        return indexBuilder.toString();
    }

    private void requiresNonnull(final String str, final String message) throws QueryBuildException {
        if (str == null || str.isEmpty()) {
            throw new QueryBuildException(message);
        }
    }
}
