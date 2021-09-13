package info.smart_tools.simpleactors.commons;

import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.RoutingSlip;
import info.smart_tools.smartactors.downloader.commons.Step;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RoutingSlipTests {

    @Test
    public void creatingByArray() {
        IStep step = new Step("actorName", "methodName", null);
        IRoutingSlip slip = new RoutingSlip("testSlip", step);
        if (slip.hasNext()) {
            assertEquals(step, slip.next());
        } else {
            fail();
        }
        assertEquals("testSlip", slip.getName());
    }

    @Test
    public void creatingByList() {
        IStep step = new Step("actorName", "methodName", null);
        IRoutingSlip slip = new RoutingSlip("testSlip", Collections.singletonList(step));
        if (slip.hasNext()) {
            assertEquals(step, slip.next());
        } else {
            fail();
        }
        assertEquals("testSlip", slip.getName());
    }

    @Test
    public void checkIterator() {
        IStep step1 = new Step("a1", "m1", null);
        IStep step2 = new Step("a2", "m1", null);
        IStep step3 = new Step("a3", "m1", null);

        IRoutingSlip slip = new RoutingSlip("name", step1, step2, step3);
        if (slip.hasNext()) {
            assertEquals(step1, slip.next());
        } else {
            fail();
        }
        if (slip.hasNext()) {
            assertEquals(step2, slip.next());
        } else {
            fail();
        }
        if (slip.hasNext()) {
            assertEquals(step3, slip.next());
        } else {
            fail();
        }
        if (slip.hasNext()) {
            fail();
        }
    }

    @Test
    public void checkChangingCurrentStep() {
        IStep step1 = new Step("a1", "m1", null);
        IStep step2 = new Step("a2", "m1", null);
        IStep step3 = new Step("a3", "m1", null);

        IRoutingSlip slip = new RoutingSlip("name", step1, step2, step3);
        slip.next();
        slip.next();
        slip.next();
        if(slip.hasNext()) {
            fail();
        }
        slip.setNextStep(0);
        if (slip.hasNext()) {
            assertEquals(step1, slip.next());
        } else {
            fail();
        }
        slip.shiftNextStep(1);
        if (slip.hasNext()) {
            assertEquals(step3, slip.next());
        } else {
            fail();
        }
        if(slip.hasNext()) {
            fail();
        }
    }

    @Test
    public void checkInsertingSlipToSlip() {
        IStep step1 = new Step("a1", "m1", null);
        IStep step2 = new Step("a2", "m1", null);
        IStep step3 = new Step("a3", "m1", null);

        IRoutingSlip slip1 = new RoutingSlip("slip1", step1, step2, step3);
        slip1.next();
        slip1.next();
        slip1.next();

        IStep step4 = new Step("a4", "m1", null);
        IStep step5 = new Step("a5", "m1", null);

        IRoutingSlip slip2 = new RoutingSlip("slip2", step4, step5);

        slip1.insertSlip(slip2);
        if (slip1.hasNext()) {
            assertEquals(step4, slip1.next());
        } else {
            fail();
        }
        if (slip1.hasNext()) {
            assertEquals(step5, slip1.next());
        } else {
            fail();
        }
        if (slip1.hasNext()) {
            fail();
        }
    }
}
