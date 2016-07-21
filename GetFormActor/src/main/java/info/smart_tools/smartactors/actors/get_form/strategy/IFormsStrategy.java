package info.smart_tools.smartactors.actors.get_form.strategy;

import info.smart_tools.smartactors.core.iobject.IObject;

import java.util.List;

/**
 * Interface for strategy for choosing form
 */
public interface IFormsStrategy {
    /**
     *
     * @param forms the forms from cached collection
     * @return form
     */
    IObject getForm(List<IObject> forms);
}
