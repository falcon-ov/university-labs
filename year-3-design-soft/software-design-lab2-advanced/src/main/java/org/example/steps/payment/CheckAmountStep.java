package org.example.steps.payment;

import org.example.context.PaymentContext;
import org.example.core.IPipelineStep;

public class CheckAmountStep implements IPipelineStep<PaymentContext> {
    @Override
    public void execute(PaymentContext ctx) {
        if (ctx.getAmount() <= 0) {
            System.out.println("Сумма некорректна!");
            ctx.setDone(true);
        }
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("CheckAmountStep: проверяет, что сумма оплаты положительная\n");
    }
}
