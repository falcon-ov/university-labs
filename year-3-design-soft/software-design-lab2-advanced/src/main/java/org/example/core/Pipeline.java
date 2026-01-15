package org.example.core;


import java.util.ArrayList;
import java.util.List;

public class Pipeline<TContext extends HasDoneFlag> {
    private final List<IPipelineStep<TContext>> steps = new ArrayList<>();

    public Pipeline<TContext> add(IPipelineStep<TContext> step) {
        steps.add(step);
        return this;
    }

    public void execute(TContext context) {
        for (IPipelineStep<TContext> step : steps) {
            if (context.isDone()) break; // Chain of Responsibility
            step.execute(context);
        }
    }

    public void describe(StringBuilder sb) {
        sb.append("Pipeline:\n");
        for (IPipelineStep<TContext> step : steps) {
            step.describe(sb);
        }
    }
    public void printDescription() {
        StringBuilder sb = new StringBuilder();
        describe(sb);
        System.out.print(sb.toString());
    }

}
