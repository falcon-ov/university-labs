package org.example.decorators;

import org.example.core.IPipelineStep;
import org.example.core.StepDecorator;

public class LoggingDecorator<TContext> extends StepDecorator<TContext> {
    public LoggingDecorator(IPipelineStep<TContext> inner) {
        super(inner);
    }

    @Override protected void before(TContext ctx) {
        System.out.println(">>> " + inner.getClass().getSimpleName() + " старт");
    }
    @Override protected void after(TContext ctx) {
        System.out.println("<<< " + inner.getClass().getSimpleName() + " конец");
    }
    @Override
    public void describe(StringBuilder sb) {
        sb.append(getClass().getSimpleName()).append(" -> ");
        inner.describe(sb);
    }
}
