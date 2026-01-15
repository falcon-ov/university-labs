package org.example.steps.order;

import org.example.context.OrderContext;
import org.example.core.IPipelineStep;

public class CartValidationStep implements IPipelineStep<OrderContext> {
    @Override
    public void execute(OrderContext ctx) {
        if (ctx.getItems().isEmpty()) {
            System.out.println("Корзина пуста!");
            ctx.setApproved(false);
            ctx.setDone(true);
        }
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("CartValidationStep: проверяет, что корзина не пуста\n");
    }
}
