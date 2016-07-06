package info.smart_tools.smartactors.actors.get_form.wrapper;

import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Wrapper for GetFormActor
 */
public interface GetFormMessage {
    /**
     * @return collection name
     */
    String getFormKey();

    /**
     * Set form to message
     * @param form the form from cached collection
     */
    void setForm(IObject form);
}
