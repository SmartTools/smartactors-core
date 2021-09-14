package info.smart_tools.simpleactors.commons.actors;

import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.simpleactors.commons.IRoutingSlip;
import info.smart_tools.simpleactors.commons.StatelessActor;

public class WhileActor extends StatelessActor {

    @Executable
    public void whileReturnToStep(
            final ConditionalInterface conditional, final int pointToReturn, final IRoutingSlip routingSlip
    ) {
        if (conditional.compare()) {
            routingSlip.setNextStep(pointToReturn);
        }
    }

    @Executable
    public void whileReturnByShift(
            final ConditionalInterface conditional, final int shift, final IRoutingSlip routingSlip
    ) {
        if (conditional.compare()) {
            routingSlip.shiftNextStep(shift);
        }
    }
}
