package info.smart_tools.smartactors.actors.get_form;

import info.smart_tools.smartactors.actors.get_form.exception.GetFormActorException;
import info.smart_tools.smartactors.actors.get_form.strategy.IFormsStrategy;
import info.smart_tools.smartactors.actors.get_form.wrapper.GetFormMessage;
import info.smart_tools.smartactors.core.cached_collection.CachedCollection;
import info.smart_tools.smartactors.core.cached_collection.ICachedCollection;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

import java.util.List;

/**
 * Actor that put form to message from cached collection
 */
public class GetFormActor {
    private static final String KEY_NAME = "form";
    private ICachedCollection collection;

    /**
     * Constructor for actor
     * @param params the wrapper for IObject, contains collectionName
     * @throws GetFormActorException for errors during create
     */
    public GetFormActor(final IObject params) throws GetFormActorException {
        try {
            IField field = IOC.resolve(Keys.getOrAdd(IField.class.toString()), "collectionName");
            collection = IOC.resolve(Keys.getOrAdd(CachedCollection.class.toString()), field.in(params), KEY_NAME);
        } catch (Exception e) {
            throw new GetFormActorException("Can't create GetFormActor", e);
        }
    }

    /**
     * Set form to message using strategy
     * @param message the wrapper for message
     * @throws GetFormActorException for errors during get form
     */
    public void getForm(final GetFormMessage message) throws  GetFormActorException {
        try {
            List<IObject> forms = collection.getItems(message.getFormKey());
            IFormsStrategy strategy = IOC.resolve(Keys.getOrAdd(IFormsStrategy.class.toString()), message.getFormKey());
            message.setForm(strategy.getForm(forms));
        } catch (Exception e) {
            throw new GetFormActorException("Failed to get form from collection", e);
        }
    }
}
