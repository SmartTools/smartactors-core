package info.smart_tools.smartactors.http_endpoint.deserialize_strategy_get.parse_tree;

import java.util.Map;

/**
 * Tree for matching strings on template
 */
public interface IParseTree {
    /**
     * Method for add template of the uri to the tree
     *
     * @param template uri template
     */
    void addTemplate(String template);

    /**
     * Method for mathcing uri to the map
     *
     * @param uri uri, that should be matched
     * @return matched map
     */
    Map<String, String> match(String uri);
}
