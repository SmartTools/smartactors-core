package info.smart_tools.smartactors.testing.test_data_source_string_array;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Implementation of {@link ISource}.
 * This implementation gets test data from array of string
 */
public class StringArrayDataSource implements ISource<String[], IObject> {

    private BlockingDeque<IObject> queue;

    /**
     * Default constructor
     */
    public StringArrayDataSource() {
        this.queue = new LinkedBlockingDeque<>();
    }

    @Override
    public String[] setSource(final String ... strings) {
        List<String> brokenItems = new ArrayList<>();
        IObject testObject;
        for (String item : strings) {
            try {
                testObject = new DSObject(item);
            } catch (InvalidArgumentException e) {
                brokenItems.add(item);
                continue;
            }
            try {
                this.queue.put(testObject);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        String[] broken = new String[brokenItems.size()];

        return brokenItems.toArray(broken);
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
