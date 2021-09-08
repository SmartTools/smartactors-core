package info.smart_tools.smartactors.downloader.commons.actors;

import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.IRoutingSlip;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;

import java.util.Map;

public class InsertRoutingSlipActor extends StatelessActor {

    @Executable()
    public void insertRoutingSlip(
            final IRoutingSlip mainSlip, final String insertingSlipName, final Map<String, IRoutingSlip> slips
    ) {
        if (null != slips && null != mainSlip) {
            IRoutingSlip insertingSlip = slips.get(insertingSlipName);
            if (null != insertingSlip) {
                mainSlip.insertSlip(insertingSlip);
            }
        }
    }
}
