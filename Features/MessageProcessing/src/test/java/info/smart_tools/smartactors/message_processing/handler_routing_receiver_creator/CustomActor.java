package info.smart_tools.smartactors.message_processing.handler_routing_receiver_creator;

public class CustomActor {

    public Boolean constructorVisited = false;
    public Boolean methodVisited = false;

    public CustomActor(IConstructorWrapper wrapper) {
        this.constructorVisited = wrapper.getA();
        wrapper.setA(true);
    }

    public void getSomeValue(IMethodWrapper wrapper) {
        this.methodVisited = true;
    }
}
