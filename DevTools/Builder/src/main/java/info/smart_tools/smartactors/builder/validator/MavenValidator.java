package info.smart_tools.smartactors.builder.validator;

import java.io.File;

public class MavenValidator implements IValidator {
    @Override
    public void validate(final String featurePath) {
        if (!new File(featurePath + "/pom.xml").exists()) {
            System.out.println("[\033[1;31mERROR\033[0m] Unable to load pom.xml in " + featurePath);
            System.exit(-1);
        }

        if (System.getenv("M2_HOME") == null) {
            System.out.println("[\033[1;31mERROR\033[0m] Unable to read M2_HOME from environment. Make sure that M2_HOME is set properly");
            System.exit(-1);
        }
    }
}
