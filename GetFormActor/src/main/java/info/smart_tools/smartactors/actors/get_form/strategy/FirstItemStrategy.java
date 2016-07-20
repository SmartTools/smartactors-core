package info.smart_tools.smartactors.actors.get_form.strategy;

import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.List;

/**
 * Return first element of list
 */
public class FirstItemStrategy implements IFormsStrategy {
    @Override
    public IObject getForm(final List<IObject> forms) {
        return forms.get(0);
    }
}
