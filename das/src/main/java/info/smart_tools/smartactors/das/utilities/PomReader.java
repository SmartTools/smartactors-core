package info.smart_tools.smartactors.das.utilities;

import info.smart_tools.smartactors.das.utilities.exception.PomReadingException;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

import java.io.File;
import java.io.FileReader;
import java.util.List;

public final class PomReader {

    private PomReader() {
    }

    public static String getGroupId(final File target)
            throws PomReadingException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            return model.getGroupId();
        } catch (Throwable e) {
            throw new PomReadingException("Could not read the pom file. Could not read 'group id': " + e.getMessage(), e);
        }
    }

    public static String getVersion(final File target)
            throws PomReadingException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            return model.getVersion();
        } catch (Throwable e) {
            throw new PomReadingException("Could not read the pom file. Could not read 'version': " + e.getMessage(), e);
        }
    }

    public static String getArtifactId(final File target)
            throws PomReadingException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            return model.getArtifactId();
        } catch (Throwable e) {
            throw new PomReadingException("Could not read the pom file. Could not read 'artifact id': " + e.getMessage(), e);
        }
    }

    public static List<String> getModules(final File target)
            throws PomReadingException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            return model.getModules();
        } catch (Throwable e) {
            throw new PomReadingException("Could not read the pom file. Could not read 'modules': " + e.getMessage());
        }
    }

    public static List<Dependency> getDependencies(final File target)
            throws PomReadingException {
        Model model = null;
        FileReader reader = null;
        MavenXpp3Reader mavenReader = new MavenXpp3Reader();
        try {
            reader = new FileReader(target);
            model = mavenReader.read(reader);
            reader.close();
            return model.getDependencies();
        } catch (Throwable e) {
            throw new PomReadingException("Could not read the pom file. Could not read 'dependencies': " + e.getMessage());
        }
    }

}
