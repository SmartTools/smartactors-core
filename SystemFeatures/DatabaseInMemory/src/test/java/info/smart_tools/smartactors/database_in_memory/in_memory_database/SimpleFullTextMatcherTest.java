package info.smart_tools.smartactors.database_in_memory.in_memory_database;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test for full text matcher
 */
public class SimpleFullTextMatcherTest {

    @Test
    public void testSimpleMatch() {
        SimpleFullTextMatcher matcher = new SimpleFullTextMatcher("abc def efg");
        assertTrue(matcher.matches("abcdef"));
    }

}
