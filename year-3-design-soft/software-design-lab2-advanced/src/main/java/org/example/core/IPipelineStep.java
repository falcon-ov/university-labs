package org.example.core;

public interface IPipelineStep<TContext> {
    void execute(TContext context);

    default void describe(StringBuilder sb) {
        sb.append(getClass().getSimpleName()).append("\n");
    }
}
