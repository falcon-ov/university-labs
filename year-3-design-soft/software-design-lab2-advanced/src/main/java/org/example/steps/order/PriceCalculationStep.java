package org.example.steps.order;

import org.example.context.OrderContext;
import org.example.core.IPipelineStep;

public class PriceCalculationStep implements IPipelineStep<OrderContext> {
    @Override
    public void execute(OrderContext ctx) {
        double total = ctx.getItems().size() * 10.0; // условно каждая вещь по 10
        ctx.setTotal(total);
        System.out.println("Сумма заказа: " + total);
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("PriceCalculationStep: считает сумму заказа (10 за товар)\n");
    }
}
