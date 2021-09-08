package info.smart_tools.smartactors.downloader.commons.actors;

import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;

public class WhileActor extends StatelessActor {

    @Executable()
    public void whileReturnToStep(
            final ConditionalInterface conditional, final int pointToReturn, final IRoutingSlip routingSlip
    ) {
        if (conditional.compare()) {
            routingSlip.setNextStep(pointToReturn);
        }
    }

    @Executable()
    public void whileReturnByShift(
            final ConditionalInterface conditional, final int shift, final IRoutingSlip routingSlip
    ) {
        if (conditional.compare()) {
            routingSlip.shiftNextStep(shift);
        }
    }
}
