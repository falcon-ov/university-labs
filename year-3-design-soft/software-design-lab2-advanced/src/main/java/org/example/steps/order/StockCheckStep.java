package org.example.steps.order;

import org.example.context.OrderContext;
import org.example.core.IPipelineStep;

public class StockCheckStep implements IPipelineStep<OrderContext> {
    @Override
    public void execute(OrderContext ctx) {
        if (ctx.getItems().contains("out-of-stock")) {
            System.out.println("Товар отсутствует на складе!");
            ctx.setApproved(false);
            ctx.setDone(true);
        }
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("StockCheckStep: проверяет наличие товаров на складе\n");
    }
}
