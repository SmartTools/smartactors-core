package info.smart_tools.smartactors.testing.test_data_source_iobject;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import info.smart_tools.smartactors.testing.interfaces.isource.exception.SourceExtractionException;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Implementation of {@link ISource}.
 * This implementation gets test data from {@link IObject}
 */
public class IObjectDataSource implements ISource<IObject, IObject> {

    private BlockingDeque<IObject> queue;

    /**
     * Default constructor
     */
    public IObjectDataSource() {
        this.queue = new LinkedBlockingDeque<>();
    }

    @Override
    public IObject setSource(IObject source)
            throws SourceExtractionException {
            try {
                this.queue.put(source);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        return null;
    }

    @Override
    public IObject next() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }
}
