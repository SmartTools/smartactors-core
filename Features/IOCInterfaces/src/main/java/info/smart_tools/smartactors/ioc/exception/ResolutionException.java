package info.smart_tools.smartactors.ioc.exception;

import java.io.StringWriter;

/**
 * Exception that occurs when resolution has been failed
 */
public class ResolutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ResolutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ResolutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ResolutionException(final Throwable cause) {
        super(cause);
    }

    /**
     * Generate new {@link ResolutionException} exception with advanced options
     * @param targetClass Class type given for resolution
     * @param args arguments given for resolution
     * @param cause exception cause
     * @return new ResolutionException with
     */
    public static ResolutionException ofResolution(final Class<?> targetClass, final Object[] args, final Throwable cause) {
        StringWriter messageWriter = new StringWriter();

        messageWriter.write("Could not resolve dependency of class ");
        messageWriter.write(targetClass.getName());
        messageWriter.write(" with ");
        messageWriter.write(String.valueOf(args.length));
        messageWriter.write(" argument(s)");

        for (Object arg : args) {
            messageWriter.write(" (");
            messageWriter.write(arg.getClass().getSimpleName());
            messageWriter.write(")'");
            messageWriter.write(String.valueOf(arg));
            messageWriter.write("'");
        }

        return new ResolutionException(messageWriter.toString(), cause);
    }
}