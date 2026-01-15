package org.example.steps.payment;

import org.example.context.PaymentContext;
import org.example.core.IPipelineStep;

public class ConfirmPaymentStep implements IPipelineStep<PaymentContext> {
    @Override
    public void execute(PaymentContext ctx) {
        if (ctx.isPaid()) {
            System.out.println("Оплата подтверждена!");
        }
        ctx.setDone(true);
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("ConfirmPaymentStep: подтверждает успешную оплату\n");
    }
}
