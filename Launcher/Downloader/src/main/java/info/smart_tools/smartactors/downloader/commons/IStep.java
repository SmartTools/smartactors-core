package info.smart_tools.smartactors.downloader.commons;

public interface IStep {
    String getActorName();
    String getMethodName();
    MethodParameters getMethodParameters();
    void setActorName(String name);
    void setMethodName(String name);
    void setMethodParameters(MethodParameters methodParameters);
}
