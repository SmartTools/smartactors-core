package info.smart_tools.smartactors.builder.feature_builder;

import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class MavenFeatureBuilder implements IFeatureBuilder {

    private static final List<String> MAVEN_GOALS = Arrays.asList("clean", "install", "package");

    @Override
    public void build(final String pathToFeature) {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(new File(pathToFeature + "/pom.xml"));
        request.setGoals(MAVEN_GOALS);

        try {
            Invoker invoker = new DefaultInvoker();
            invoker.setMavenHome(new File(System.getenv("MAVEN_HOME")));
            InvocationResult result = invoker.execute(request);

            if (result.getExitCode() != 0) {
                if (result.getExecutionException() != null) {
                    throw new RuntimeException("Failed to invoke maven goals, exit code: " + result.getExitCode(), result.getExecutionException());
                }
                throw new RuntimeException("Failed to invoke maven goals, exit code: " + result.getExitCode());
            }
        } catch (MavenInvocationException e) {
            throw new RuntimeException("Failed to invoke maven goals", e);
        }
    }
}
