package info.smart_tools.simpleactors.commons;

public abstract class StatefulActor extends StatelessActor {

    @Override
    public synchronized void execute(final String methodName, final IMessage message) {
        super.execute(methodName, message);
    }
}





