package info.smart_tools.smartactors.downloader.commons;

public class Step implements IStep {

    private String actorName;
    private String methodName;
    private MethodParameters methodParameters;

    public Step(final String actorName, final String methodName, final MethodParameters methodParameters) {
        this.actorName = actorName;
        this.methodName = methodName;
        this.methodParameters = methodParameters;
    }

    public String getActorName() {
        return this.actorName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    public MethodParameters getMethodParameters() {
        return this.methodParameters;
    }

    public void setActorName(final String name) {
        this.actorName = name;
    }

    @Override
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public void setMethodParameters(MethodParameters methodParameters) {
        this.methodParameters = methodParameters;
    }
}
