package info.smart_tools.smartactors.endpoint_components_generic.parse_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Template {
    List<String> template = new ArrayList<>();
    boolean[] isVariable;

    public Template(final String template) {
        this.template = Arrays.asList(template.split("/"));
        isVariable = new boolean[template.length()];
        for (int i = 0; i < this.template.size(); i++) {
            String templateElem = this.template.get(i);
            if (templateElem.startsWith(":")) {
                isVariable[i] = true;
            }
        }
    }

    public boolean isVariable(final int i) {
        return isVariable[i];
    }

    public String get(final int i) {
        return template.get(i);
    }

    public int size() {
        return template.size();
    }

}
