package info.smart_tools.smartactors.builder.validator;

public interface IValidator {

    void validate(String featurePath) throws ValidationException;
}
