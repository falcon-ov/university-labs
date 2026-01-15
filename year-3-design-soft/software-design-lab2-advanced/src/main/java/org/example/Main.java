package org.example;


import org.example.context.OrderContext;
import org.example.context.PaymentContext;
import org.example.core.Pipeline;
import org.example.decorators.LoggingDecorator;
import org.example.decorators.TimingDecorator;
import org.example.steps.order.*;
import org.example.steps.payment.CheckAmountStep;
import org.example.steps.payment.ConfirmPaymentStep;
import org.example.steps.payment.ProcessPaymentStep;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Pipeline<OrderContext> orderPipeline = new Pipeline<>();
        orderPipeline
                .add(new LoggingDecorator<>(new CartValidationStep()))
                .add(new TimingDecorator<>(new PriceCalculationStep()))
                .add(new LoggingDecorator<>(new DiscountStep()))
                .add(new LoggingDecorator<>(new StockCheckStep()))
                .add(new LoggingDecorator<>(new FinalizeOrderStep()));

        System.out.println("\n=== Пример 1: нормальный заказ ===");
        OrderContext order1 = new OrderContext(List.of("apple", "banana", "milk", "bread", "cheese", "coffee"));
        orderPipeline.execute(order1);

        System.out.println("\n=== Пример 2: пустая корзина ===");
        OrderContext order2 = new OrderContext(List.of());
        orderPipeline.execute(order2);

        System.out.println("\n=== Пример 3: товар отсутствует ===");
        OrderContext order3 = new OrderContext(List.of("apple", "out-of-stock", "milk"));
        orderPipeline.execute(order3);


        Pipeline<PaymentContext> paymentPipeline = new Pipeline<>();
        paymentPipeline
                .add(new CheckAmountStep())
                .add(new ProcessPaymentStep())
                .add(new ConfirmPaymentStep());
        System.out.println("\n=== Пример 4(Другой контекст): сумма 100");
        PaymentContext pay1 = new PaymentContext(100);
        paymentPipeline.execute(pay1);
        System.out.println("\n===  Пример 5(Другой контекст):  сумма 0");
        PaymentContext pay2 = new PaymentContext(0);
        paymentPipeline.execute(pay2);

        System.out.println();

        System.out.println("=== Состав пайплайна заказа ===");
        orderPipeline.printDescription();

        System.out.println();

        System.out.println("=== Состав пайплайна оплаты ===");
        paymentPipeline.printDescription();
    }
}
