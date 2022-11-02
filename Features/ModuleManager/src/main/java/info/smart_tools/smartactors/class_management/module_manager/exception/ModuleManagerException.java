package info.smart_tools.smartactors.class_management.module_manager.exception;


import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;

/**
 * Exception for {@link ModuleManager}
 */
public class ModuleManagerException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ModuleManagerException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ModuleManagerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ModuleManagerException(final Throwable cause) {
        super(cause);
    }
}
