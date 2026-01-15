package org.example.steps.order;

import org.example.context.OrderContext;
import org.example.core.IPipelineStep;

public class FinalizeOrderStep implements IPipelineStep<OrderContext> {
    @Override
    public void execute(OrderContext ctx) {
        ctx.setApproved(true);
        ctx.setDone(true);
        System.out.println("Заказ подтверждён. Итоговая сумма: " + ctx.getTotal());
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("FinalizeOrderStep: подтверждает заказ и завершает пайплайн\n");
    }
}
