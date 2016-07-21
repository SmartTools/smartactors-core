package info.smart_tools.smartactors.core.ipath;

/**
 * Interface for abstract file path filter.
 */
public interface IPathFilter {

    /**
     * Returns true if this path must be accepted by the filter.
     * @param path path to check
     * @return true or false
     */
    boolean accept(IPath path);

}
