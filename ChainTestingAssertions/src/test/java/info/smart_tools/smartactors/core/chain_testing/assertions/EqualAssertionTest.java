package info.smart_tools.smartactors.core.chain_testing.assertions;

import info.smart_tools.smartactors.core.chain_testing.exceptions.AssertionFailureException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Test for {@link EqualAssertion}.
 */
public class EqualAssertionTest extends AssertionTestBase {
    @Test
    public void Should_passWhenValueIsEqualToReference()
            throws Exception {
        apply(EqualAssertion.class, "{'to':'reference string'}".replace('\'', '"'), "reference string");
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_failWhenValueIsNotEqualToReferenceValue()
            throws Exception {
        apply(EqualAssertion.class, "{'to':'reference string'}".replace('\'', '"'), "wrong string");
    }

    @Test(expected = AssertionFailureException.class)
    public void Should_throwWhenCannotReadReferenceValue()
            throws Exception {
        IObject desc = mock(IObject.class);

        when(desc.getValue(any())).thenThrow(ReadValueException.class);

        apply(EqualAssertion.class, desc, "value");
    }
}
