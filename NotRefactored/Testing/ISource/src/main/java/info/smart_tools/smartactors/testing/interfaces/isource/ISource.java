package info.smart_tools.smartactors.testing.interfaces.isource;

import info.smart_tools.smartactors.testing.interfaces.isource.exception.SourceExtractionException;

/**
 * Interface {@link ISource}.
 *
 * @param <T1> the type of source data
 * @param <T2> the type of some elements
 */
public interface ISource<T1, T2> {

    /**
     * Set source of test data
     * @param source the source of test data
     * @return the broken elements
     * @throws SourceExtractionException if any error occurs when the source is extracting
     */
    T1 setSource(T1 source) throws SourceExtractionException;

    /**
     * Get next data element
     * @return T2 the next data element
     */
    T2 next();
}
