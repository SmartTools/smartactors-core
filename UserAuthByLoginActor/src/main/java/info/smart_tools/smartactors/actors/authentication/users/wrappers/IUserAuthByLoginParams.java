package info.smart_tools.smartactors.actors.authentication.users.wrappers;

/**
 *
 */
public interface IUserAuthByLoginParams {
    /**
     *
     * @return
     */
    String getCollection();

    /**
     *
     * @return
     */
    String getAlgorithm();

    /**
     *
     * @return
     */
    String getCharset();

    /**
     *
     * @return
     */
    String getEncoder();
}
