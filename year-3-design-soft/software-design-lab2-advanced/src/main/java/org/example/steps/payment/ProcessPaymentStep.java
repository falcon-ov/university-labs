package org.example.steps.payment;

import org.example.context.PaymentContext;
import org.example.core.IPipelineStep;

public class ProcessPaymentStep implements IPipelineStep<PaymentContext> {
    @Override
    public void execute(PaymentContext ctx) {
        System.out.println("Списываем " + ctx.getAmount() + "...");
        ctx.setPaid(true);
    }

    @Override
    public void describe(StringBuilder sb) {
        sb.append("ProcessPaymentStep: выполняет списание средств\n");
    }
}
