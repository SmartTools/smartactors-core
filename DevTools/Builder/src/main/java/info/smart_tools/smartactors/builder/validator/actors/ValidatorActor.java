package info.smart_tools.smartactors.builder.validator.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.builder.validator.IValidator;
import info.smart_tools.smartactors.builder.validator.ValidationException;

public class ValidatorActor extends StatelessActor {

    private IValidator validator;

    public ValidatorActor(final IValidator validator) {
        this.validator = validator;
    }

    @Executable
    public void validate(final String featurePath) {
        try {
            this.validator.validate(featurePath);
        } catch (ValidationException e) {
            throw new RuntimeException("Unable to validate arguments", e);
        }
    }
}
