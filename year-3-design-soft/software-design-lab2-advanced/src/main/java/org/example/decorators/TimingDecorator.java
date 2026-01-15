package org.example.decorators;


import org.example.core.IPipelineStep;
import org.example.core.StepDecorator;

public class TimingDecorator<TContext> extends StepDecorator<TContext> {
    private long start;
    public TimingDecorator(IPipelineStep<TContext> inner) { super(inner); }

    @Override protected void before(TContext ctx) { start = System.nanoTime(); }
    @Override protected void after(TContext ctx) {
        long ms = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Время выполнения: " + ms + " ms");
    }
    @Override
    public void describe(StringBuilder sb) {
        sb.append(getClass().getSimpleName()).append(" -> ");
        inner.describe(sb);
    }
}
