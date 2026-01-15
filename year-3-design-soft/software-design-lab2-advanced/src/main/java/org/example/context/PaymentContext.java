package org.example.context;

import org.example.core.HasDoneFlag;

public class PaymentContext implements HasDoneFlag {
    private boolean done;
    private double amount;
    private boolean paid;

    public PaymentContext(double amount) { this.amount = amount; }

    public double getAmount() { return amount; }
    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    @Override public boolean isDone() { return done; }
    @Override public void setDone(boolean done) { this.done = done; }
}
