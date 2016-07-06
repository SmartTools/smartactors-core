package info.smart_tools.smartactors.actors.authentication.users.wrappers;

import javax.annotation.Nonnull;

/**
 *
 */
public interface IUserAuthByLoginMessage {
    /**
     *
     * @return
     */
    String getLogin();

    /**
     *
     * @return
     */
    String getPassword();

    /**
     *
     * @param status
     */
    void setAuthStatus(@Nonnull final String status);

    /**
     *
     * @param message
     */
    void setAuthMessage(@Nonnull final String message);
}
