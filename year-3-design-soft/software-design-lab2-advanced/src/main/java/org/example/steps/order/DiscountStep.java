package org.example.steps.order;

import org.example.context.OrderContext;
import org.example.core.IPipelineStep;

public class DiscountStep implements IPipelineStep<OrderContext> {
    @Override
    public void execute(OrderContext ctx) {
        if (ctx.getTotal() > 50) {
            ctx.setTotal(ctx.getTotal() * 0.9);
            System.out.println("Применена скидка 10%. Итог: " + ctx.getTotal());
        }
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("DiscountStep: применяет скидку 10% при сумме > 50\n");
    }
}
