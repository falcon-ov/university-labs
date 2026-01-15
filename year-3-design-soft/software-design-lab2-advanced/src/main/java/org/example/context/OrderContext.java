package org.example.context;

import org.example.core.HasDoneFlag;
import java.util.List;

public class OrderContext implements HasDoneFlag {
    private boolean done;
    private List<String> items;
    private double total;
    private boolean approved;

    public OrderContext(List<String> items) {
        this.items = items;
    }

    public List<String> getItems() { return items; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public boolean isApproved() { return approved; }
    public void setApproved(boolean approved) { this.approved = approved; }

    @Override public boolean isDone() { return done; }
    @Override public void setDone(boolean done) { this.done = done; }
}
