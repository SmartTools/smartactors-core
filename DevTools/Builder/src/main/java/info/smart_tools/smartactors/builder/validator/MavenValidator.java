package info.smart_tools.smartactors.builder.validator;

import java.io.File;

public class MavenValidator implements IValidator {
    @Override
    public void validate(final String featurePath) throws ValidationException {
        try {
            new File(featurePath + "/pom.xml");
        } catch (Exception e) {
            throw new ValidationException("Unable to load pom.xml in " + featurePath, e);
        }

        try {
            new File(System.getenv("M2_HOME"));
        } catch (Exception e) {
            throw new ValidationException("Unable to read M2_HOME", e);
        }
    }
}
