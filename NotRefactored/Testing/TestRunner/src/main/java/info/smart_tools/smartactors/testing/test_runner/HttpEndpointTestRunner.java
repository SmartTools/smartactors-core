package info.smart_tools.smartactors.testing.test_runner;

import info.smart_tools.smartactors.base.exception.initialization_exception.InitializationException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.testing.interfaces.isource.ISource;
import info.smart_tools.smartactors.testing.interfaces.isource.exception.SourceExtractionException;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.ITestRunner;
import info.smart_tools.smartactors.testing.interfaces.itest_runner.exception.TestExecutionException;

/**
 * Runs tests on receiver chains.
 *
 * If the chain should be completed without exceptions then the description should have the following format:
 *
 * <pre>
 *     {
 *         "name": "Unique test name",                      // Unique name of the test
 *         "entryPoint": "httpEndpoint",                    // The entry point (chain, httpEndpoint, etc)
 *         "environment": {
 *             "message": {                                 // The message to send
 *                 "a": 10,
 *                 "b": 42
 *             },
 *             "context": {},                                // Context of the message
 *             "request": {                                  // Description of simulating request
 *                  "protocolVersion": "HTTP/1.1",
 *                  "keepAlive": true,
 *                  "headers": [
 *                  {
 *                      "name": "",
 *                      "value": ""
 *                  }
 *                  ],
 *                  "method": "POST",
 *                  "uri": "/"
 *             }
 *         },
 *         "chainName": "addChain",                         // Name of the chain to test
 *         "assert": [                                      // List of assertions to check
 *          {
 *              "name": "result should be correct",         // Unique name of the assertion
 *              "type": "eq",                               // Type of the assertion
 *              "value": "message/c"                        // Value to check. The value is described just like getter method of wrapper
 *                                                          // interface
 *              "to": 52,                                   // Additional argument(s) of assertion. Name(s) depend(s) on concrete assertion
 *                                                          // type
 *          }
 *         ]
 *     }
 * </pre>
 *
 * If the tested chain should be completed with exception then the description should have the following format:
 *
 * <pre>
 *     {
 *         "name": "Another test name",
 *         "entryPoint": "httpEndpoint",
 *         "environment": {
 *             "message": {
 *                 "a": 10,
 *                 "b": 0
 *             },
 *             "context": {},
 *             "request": {                                  // Description of simulating request
 *                  "protocolVersion": "HTTP/1.1",
 *                  "keepAlive": true,
 *                  "headers": [
 *                  {
 *                      "name": "",
 *                      "value": ""
 *                  }
 *                  ],
 *                  "method": "POST",
 *                  "uri": "/"
 *             }
 *         },
 *         "chainName": "divideChain",
 *         "intercept": {
 *             "class": "java.lang.ArithmeticException",    // Canonical name of exception class
 *             "target": "divideReceiver"                   // Id of the receiver that should throw exception (as if the "intercept" section
 *                                                          // was a description of a object)
 *         }
 *     }
 * </pre>
 */
public class HttpEndpointTestRunner implements ITestRunner {

    private final IFieldName contentFieldName;
    private final IFieldName chainNameFieldName;
    private final IFieldName callbackFieldName;

    /**
     * Default constructor.
     * @throws InitializationException if instance creation was failed
     */
    public HttpEndpointTestRunner()
            throws InitializationException {
        try {
            this.contentFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "content"
            );
            this.chainNameFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainName"
            );
            this.callbackFieldName = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "callback"
            );
        } catch (ResolutionException e) {
            throw new InitializationException("Could not create new instance of HttpEndpointTestRunner.", e);
        }
    }

    /**
     * Run a test described by the given {@link IObject}.
     *
     * @param description    description of the test to run
     * @param callback       callback to call when test is completed (with {@code null} as the only argument in case of success or with
     *                       exception describing failure reasons in case of failure)
     * @throws InvalidArgumentException if {@code description} is {@code null}
     * @throws InvalidArgumentException if {@code callback} is {@code null}
     * @throws TestExecutionException if any error was occurred
     */
    public void runTest(final IObject description, final IAction<Throwable> callback)
            throws InvalidArgumentException, TestExecutionException {
        if (null == description) {
            throw new InvalidArgumentException("Description should not be null.");
        }
        if (null == callback) {
            throw new InvalidArgumentException("Callback should not be null.");
        }

        try {
            IObject sourceObject = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject")
            );
            Object chainName = description.getValue(this.chainNameFieldName);

            sourceObject.setValue(this.contentFieldName, description);
            sourceObject.setValue(this.callbackFieldName, callback);
            sourceObject.setValue(this.chainNameFieldName, chainName);
            ISource<IObject, IObject> source = IOC.resolve(
                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "test_data_source")
            );
            source.setSource(sourceObject);
        } catch (ReadValueException | ChangeValueException | ResolutionException | SourceExtractionException e) {
            throw new TestExecutionException(e);
        }
    }

}
