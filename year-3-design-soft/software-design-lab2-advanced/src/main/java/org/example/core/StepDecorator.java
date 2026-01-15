package org.example.core;

public abstract class StepDecorator<TContext> implements IPipelineStep<TContext> {
    protected final IPipelineStep<TContext> inner;

    public StepDecorator(IPipelineStep<TContext> inner) {
        this.inner = inner;
    }

    @Override
    public void execute(TContext ctx) {
        before(ctx);
        inner.execute(ctx);
        after(ctx);
    }

    protected void before(TContext ctx) {}
    protected void after(TContext ctx) {}
}

