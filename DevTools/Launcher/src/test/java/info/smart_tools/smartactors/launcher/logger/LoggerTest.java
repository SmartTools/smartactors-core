package info.smart_tools.smartactors.launcher.logger;

import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.Assert.assertEquals;

public class LoggerTest {

    private ILogger logger;

    @Before
    public void init() {
        this.logger = new Logger();
    }

    @Test
    public void testDebugMessage() throws Exception {
        System.setProperty("launcher.debug", "true");
        this.logger = new Logger();
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        logger.debug("This is a message");
        os.flush();
        String logMessage = os.toString();
        assertEquals("[DEBUG] This is a message\n", logMessage);
    }

    @Test
    public void testInfoMessageWithArguments() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        logger.info("This is a message, that contains {0} and {1}", "cookies", "birds");
        os.flush();
        String logMessage = os.toString();
        assertEquals("[INFO] This is a message, that contains cookies and birds\n", logMessage);
    }

    @Test
    public void testWarningMessage() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        logger.warning("This is a warning");
        os.flush();
        String logMessage = os.toString();
        assertEquals("[WARNING] This is a warning\n", logMessage);
    }

    @Test
    public void testErrorMessageWithArgs() throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        System.setOut(new PrintStream(os));
        logger.error("This error is all about {0} and {1}", "rainbows", "cookies");
        os.flush();
        String logMessage = os.toString();
        assertEquals("[ERROR] This error is all about rainbows and cookies\n", logMessage);
    }
}
