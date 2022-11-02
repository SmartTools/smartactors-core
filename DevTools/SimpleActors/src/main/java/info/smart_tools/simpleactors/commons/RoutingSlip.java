package info.smart_tools.simpleactors.commons;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RoutingSlip implements IRoutingSlip {

    private final String name;
    private final List<IStep> steps;
    private int currentPosition;

    public RoutingSlip(final String name, final IStep ... steps) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.steps.addAll(Arrays.asList(steps));
        this.currentPosition = 0;
    }

    public RoutingSlip(final String name, final List<IStep> steps) {
        this.name = name;
        this.steps = new ArrayList<>();
        this.steps.addAll(steps);
        this.currentPosition = 0;
    }

    public String getName() {

        return this.name;
    }

    public IStep next() {
        IStep step = this.steps.get(this.currentPosition);
        currentPosition++;

        return step;
    }

    public boolean hasNext() {

        return this.steps.size() - 1 >= this.currentPosition;
    }

    public void setNextStep(final int number) {
        this.currentPosition = number;
    }

    public void shiftNextStep(final int number) {
        this.currentPosition += number;
    }

    public void insertSlip(final IRoutingSlip slip) {
        List<IStep> steps = new ArrayList<>();
        while (slip.hasNext()) {
            steps.add(slip.next());
        }
        Collections.reverse(steps);
        steps.forEach(this::insertStep);
    }

    public void insertStep(final IStep step) {
        this.steps.add(this.currentPosition, step);
    }
}
