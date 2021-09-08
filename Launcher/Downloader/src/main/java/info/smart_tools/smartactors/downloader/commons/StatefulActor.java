package info.smart_tools.smartactors.downloader.commons;

public abstract class StatefulActor extends StatelessActor {

    @Override
    public synchronized void execute(final String methodName, final IMessage message) {
        super.execute(methodName, message);
    }
}





