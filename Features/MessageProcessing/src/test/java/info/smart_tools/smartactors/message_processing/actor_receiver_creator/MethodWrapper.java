package info.smart_tools.smartactors.message_processing.actor_receiver_creator;

public class MethodWrapper implements IMethodWrapper {
    public Boolean methodVisited = false;

    @Override
    public String getString() {
        this.methodVisited = true;
        return null;
    }
}
