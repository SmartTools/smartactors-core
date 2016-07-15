package info.smart_tools.smartactors.core.db_tasks.psql.create_collection;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class IndexBuilderTest {
    private String collection;
    private String field;

    @Before
    public void setUp() {
        collection = "testCollectionName";
        field = "testFieldRepresentation";
    }

    @Test
    public void buildIndexForQueryTest() throws Exception {
        List<String> indexes = new ArrayList<>(Arrays.asList("ordered", "tags", "fulltext", "datetime", "id"));
        List<String> buildingResult = new ArrayList<>(
                Arrays.asList(
                        "CREATE INDEX ON " + collection + " USING BTREE ((" + field + "));\n",
                        "CREATE INDEX ON " + collection + " USING GIN ((" + field + "));\n",
                        "CREATE INDEX ON " + collection +
                                " USING GIN ((to_tsvector('russian',(" + field + ")::text)));\n",
                        "CREATE INDEX ON " + collection +
                                " USING BTREE ((parse_timestamp_immutable(" + field + ")));\n",
                        "CREATE INDEX ON " + collection + " USING BTREE ((" + field + "));\n" +
                                "CREATE INDEX ON " + collection + " USING HASH ((" + field + "));\n"));
        int round = indexes.size();
        IndexBuilder builder = IndexBuilder
                .create()
                .withCollection(collection)
                .withField(field);
        for (int i = 0; i < round; ++i) {
            String buildingIndex = builder
                    .withIndex(indexes.get(i))
                    .build();
            assertEquals(buildingIndex, buildingResult.get(i));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_ThrowsException_WithReason_InvalidIndex() {
        IndexBuilder
                .create()
                .withCollection(collection)
                .withIndex("invalidIndex");
    }
}
