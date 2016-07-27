package info.smart_tools.smartactors.actors.validate_form_data;

import info.smart_tools.smartactors.actors.validate_form_data.exception.ValidateFormException;
import info.smart_tools.smartactors.actors.validate_form_data.parser.Parser;
import info.smart_tools.smartactors.actors.validate_form_data.wrapper.ValidateFormDataMessage;
import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ifield_name.IFieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.field.Field;

import java.util.Iterator;
import java.util.Map;

/**
 * Actor for validating form data from client
 */
public class ValidateFormDataActor {
    private Field validationRulesF;

    /**
     * Constructor
     * @param params the empty IObject
     */
    public ValidateFormDataActor(final IObject params) throws InvalidArgumentException {
        try {
            validationRulesF = IOC.resolve(Keys.getOrAdd(IField.class.getCanonicalName()), "validationRules");
        } catch (Exception e) {
            throw new InvalidArgumentException(e);
        }
    }

    /**
     * Validate form data from client
     * @param message the message
     * @throws ValidateFormException if form is not valid
     */
    public void validate(final ValidateFormDataMessage message) throws ValidateFormException {
        try {
            IObject formFields = message.getForm();
            IObject clientData = message.getFormFromRequest();

            IObject resultObject = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

            Iterator<Map.Entry<IFieldName, Object>> fieldsIterator = formFields.iterator();
            Map.Entry<IFieldName, Object> entry = fieldsIterator.next();

            while (entry != null) {
                IFieldName key = entry.getKey();

                String rules = validationRulesF.in((IObject) formFields.getValue(key));
                if (rules != null) {
                    if (!new Parser(rules, (String) clientData.getValue(key)).validate()) {
                        throw new ValidateFormException("Fields is not correct");
                    }
                }
                resultObject.setValue(key, clientData.getValue(key));

                if (!fieldsIterator.hasNext()) {
                    break;
                }
                entry = fieldsIterator.next();
            }
            message.setFormData(resultObject);
        } catch (Exception e) {
            throw new ValidateFormException(e);
        }
    }
}
