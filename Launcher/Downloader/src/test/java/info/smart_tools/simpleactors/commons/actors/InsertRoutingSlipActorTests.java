package info.smart_tools.simpleactors.commons.actors;

import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.IStep;
import info.smart_tools.smartactors.downloader.commons.Message;
import info.smart_tools.smartactors.downloader.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.commons.RoutingSlip;
import info.smart_tools.smartactors.downloader.commons.Step;
import info.smart_tools.smartactors.downloader.commons.actors.InsertRoutingSlipActor;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class InsertRoutingSlipActorTests {

    @Test
    public void insertSlip() {
        IStep step11 = new Step(
                "test1",
                "test1",
                new MethodParameters(null, null)
        );
        MethodParameters methodParameters = new MethodParameters(
                Arrays.asList("main slip", "inserting slip", "#slips"), null
        );
        IStep step21 = new Step(
                "InsertRoutingSlipActor",
                "insertRoutingSlip",
                methodParameters
        );
        IRoutingSlip insertingSlip = new RoutingSlip("inserting slip", step11);
        IRoutingSlip mainSlip = new RoutingSlip("main", step21);
        InsertRoutingSlipActor insertRoutingSlipActor = new InsertRoutingSlipActor();
        Message message = Message
                .builder()
                .add(CommonParameters.METHOD_PARAMS, methodParameters)
                .add("main slip", mainSlip)
                .add("inserting slip", "1")
                .add("#slips", new HashMap<String, IRoutingSlip>(){{put("main slip", mainSlip); put("1", insertingSlip);}})
                .build();
        IStep step1 = mainSlip.next();
        insertRoutingSlipActor.execute("insertRoutingSlip", message);
        assertEquals(step21, step1);
        if (mainSlip.hasNext()) {
            IStep step2 = mainSlip.next();
            assertEquals(step11, step2);
        } else {
            fail();
        }
    }
}
