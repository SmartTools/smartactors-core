package info.smart_tools.smartactors.downloader.commons;

import java.util.Iterator;

public interface IRoutingSlip extends Iterator<IStep> {

    String getName();

    void setNextStep(int number);

    void shiftNextStep(int number);

    void insertSlip(IRoutingSlip slip);

    void insertStep(IStep step);
}
