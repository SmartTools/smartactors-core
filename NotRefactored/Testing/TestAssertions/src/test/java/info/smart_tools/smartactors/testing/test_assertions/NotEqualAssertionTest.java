package info.smart_tools.smartactors.testing.test_assertions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.testing.interfaces.iassertion.exception.AssertionFailureException;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link NotEqualAssertion}.
 */
public class NotEqualAssertionTest extends AssertionTestBase {
    @Test
    public void Should_passWhenValueIsNotEqualToReference()
            throws Exception {
        apply(NotEqualAssertion.class, "{'to':'reference string'}".replace('\'', '"'), "not reference string");
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_failWhenValueIsEqualToReferenceValue()
            throws Exception {
        apply(NotEqualAssertion.class, "{'to':'reference string'}".replace('\'', '"'), "reference string");
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenCannotReadReferenceValue()
            throws Exception {
        IObject desc = mock(IObject.class);

        when(desc.getValue(any())).thenThrow(ReadValueException.class);

        apply(NotEqualAssertion.class, desc, "value");
    }
}