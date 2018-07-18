package info.smart_tools.smartactors.endpoint_components_generic.parse_tree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link IParseTree} for parsing rest URI by template in format
 * "/smth/:variable1/smth2/:variable2"
 */
public class ParseTree implements IParseTree {
    private int level;
    private List<ParseTree> variableChildElems;
    private List<ParseTree> constChildElems;
    private String templateElem;
    private boolean isEnd;

    /**
     * Constructor for creating empty tree
     */
    public ParseTree() {
        constChildElems = new ArrayList<>();
        variableChildElems = new ArrayList<>();
        this.level = 0;
        isEnd = false;
    }

    private ParseTree(final int level, final Template template) {
        constChildElems = new ArrayList<>();
        variableChildElems = new ArrayList<>();
        this.level = level;
        if (template.size() - 1 == level) {
            isEnd = true;
        }
    }

    @Override
    public Map<String, String> match(final String uri) {
        TreeObject treeObject = new TreeObject(0, Arrays.asList(uri.split("/")));
        TreeObject resultObject = match(treeObject);
        return resultObject.isEnded() ? resultObject.getResult() : null;
    }

    private TreeObject match(final TreeObject treeObject) {
        if (!treeObject.hasElem(0)) {
            treeObject.setEnded(true);
        }
        if (!treeObject.hasElem(level)) {
            return treeObject;
        }
        if (!treeObject.getUriElem(level).equals(templateElem) && !isVariable()) {
            treeObject.setEnded(false);
            return treeObject;
        }
        TreeObject resultObject = new TreeObject(level, treeObject.getUri());
        for (int i = 0; i < constChildElems.size(); i++) {
            TreeObject bufObject = constChildElems.get(i).match(treeObject);
            if (bufObject.isEnded() && bufObject.getLevel() > resultObject.getLevel()) {
                resultObject = bufObject;
            }
        }
        for (int i = 0; i < variableChildElems.size(); i++) {
            TreeObject bufObject = variableChildElems.get(i).match(treeObject);
            if (bufObject.isEnded() && bufObject.getLevel() > resultObject.getLevel()) {
                resultObject = bufObject;
            }
        }
        if (resultObject.getLevel() == level) {
            if (!resultObject.isEnded() && isEnd) {
                resultObject.setEnded(true);
            }
        }

        if (isVariable()) {
            resultObject.addResult(templateElem.substring(1), treeObject.getUriElem(level));
        }
        return resultObject;
    }

    private String getTemplateElem() {
        return templateElem;
    }

    private boolean isVariable() {
        return this.getTemplateElem().startsWith(":");
    }

    @Override
    public void addTemplate(final String template) {
        addTemplate(new Template(template));
    }

    private void addTemplate(final Template template) {
        if (template.size() <= level) {
            return;
        }
        this.templateElem = template.get(level);
        if (template.isVariable(level + 1)) {
            for (ParseTree variableChildElem : variableChildElems) {
                if (variableChildElem.getTemplateElem().equals(template.get(level + 1))) {
                    variableChildElem.addTemplate(template);
                    return;
                }
            }
            ParseTree treeElem = new ParseTree(level + 1, template);
            variableChildElems.add(treeElem);
            treeElem.addTemplate(template);
            return;
        }
        for (ParseTree constTreeElem : constChildElems) {
            if (constTreeElem.getTemplateElem().equals(template.get(level + 1))) {
                constTreeElem.addTemplate(template);
                return;
            }
        }
        if (level + 1 < template.size()) {
            ParseTree treeElem = new ParseTree(level + 1, template);
            constChildElems.add(treeElem);
            treeElem.addTemplate(template);
        }
    }
}