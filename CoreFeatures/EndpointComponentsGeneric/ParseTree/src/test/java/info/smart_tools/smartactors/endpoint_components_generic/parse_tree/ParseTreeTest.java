package info.smart_tools.smartactors.endpoint_components_generic.parse_tree;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ParseTreeTest {
    @Before
    public void setUp() {}

    @Test
    public void testParseTreeWithOneTemplate() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra");
        assertEquals(resultMap.size(), 1);
        assertEquals(resultMap.get("messageMapId"), "123");
    }


    @Test
    public void testParseTreeWithNotExistingTemplate() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        Map<String, String> resultMap = tree.match("/notHome/123/abrakadabra");
        assertEquals(resultMap, null);
    }

    @Test
    public void testParseTreeWithNotExistingTemplateAtTheLastPart() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra1");
        assertEquals(resultMap, null);
    }

    @Test
    public void testParseTreeWithTwoDifferentTemplatesOnTheFirstLevel() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        tree.addTemplate("/notHome/:foo/abrakadabra");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra");
        assertEquals(resultMap.size(), 1);
        assertNull(resultMap.get("foo"));
        assertEquals(resultMap.get("messageMapId"), "123");
    }

    @Test
    public void testParseTreeWithTwoDifferentTemplatesOnTheLowestLevel() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        tree.addTemplate("/home/:foo/abrakadabra1");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra");
        assertEquals(resultMap.size(), 1);
        assertNull(resultMap.get("foo"));
        assertEquals(resultMap.get("messageMapId"), "123");
    }

    @Test
    public void testPreferEquality() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra");
        tree.addTemplate("/home/123/:messageMapId");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra");
        assertEquals(resultMap.size(), 1);
        assertEquals(resultMap.get("messageMapId"), "abrakadabra");
    }

    @Test
    public void testPreferEqualityOnTheLowestLevel() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra/x");
        tree.addTemplate("/home/123/:messageMapId/y");
        Map<String, String> resultMap = tree.match("/home/123/abrakadabra/x");
        assertEquals(resultMap.size(), 1);
        assertEquals(resultMap.get("messageMapId"), "123");
    }

    @Test
    public void testOnTheManyTemplates() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/home/:messageMapId/abrakadabra/x");
        tree.addTemplate("/foo/:bar");
        tree.addTemplate("/foo/:bar/userId/:id");
        tree.addTemplate("/home/123/:messageMapId/y");

        Map<String, String> resultMap = tree.match("/foo/123/userId/x");
        Map<String, String> resultMap1 = tree.match("/foo/123");
        Map<String, String> resultMap2 = tree.match("/home/123/y");
        assertEquals(resultMap.size(), 2);
        assertEquals(resultMap.get("bar"), "123");

        assertEquals(resultMap1.size(), 1);
        assertEquals(resultMap1.get("bar"), "123");

        assertNull(resultMap2);
    }

    @Test
    public void testEmptyTemplate() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/");
        Map<String, String> resultMap = tree.match("/");
        assertEquals(resultMap.size(), 0);
    }

    @Test
    public void testConstTemplateNotPass() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/api");
        Map<String, String> resultMap = tree.match("/hello");
        assertEquals(resultMap, null);
    }

    @Test
    public void testConstTemplatePass() {
        IParseTree tree = new ParseTree();
        tree.addTemplate("/api");
        Map<String, String> resultMap = tree.match("/api");
        assertTrue(resultMap.isEmpty());
    }

}
