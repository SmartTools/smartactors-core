package info.smart_tools.smartactors.endpoint_components_generic.parse_tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Item of {@link ParseTree}
 */
public class TreeObject {
    private int level;
    private List<String> uri;
    private Map<String, String> result;
    private boolean ended;

    /**
     * Constructor for creating item
     *
     * @param level level at the tree
     * @param uri   uri
     */
    public TreeObject(final int level, final List<String> uri) {
        this.level = level;
        this.uri = uri;
        this.result = new HashMap<>();
        ended = false;
    }

    /**
     * Method for getting level of the tree item
     *
     * @return level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Method for setting level of the tree item
     *
     * @param level level value
     */
    public void setLevel(final int level) {
        this.level = level;
    }

    public String getUriElem(final int index) {
        return uri.get(index);
    }

    public void setUri(final List<String> uri) {
        this.uri = uri;
    }

    public void addResult(final String key, final String value) {
        this.result.put(key, value);
    }

    public Map<String, String> getResult() {
        return result;
    }

    public boolean hasElem(final int index) {
        return uri.size() > index;
    }

    public List<String> getUri() {
        return uri;
    }

    public boolean isEnded() {
        return ended;
    }

    public void setEnded(final boolean ended) {
        this.ended = ended;
    }
}
