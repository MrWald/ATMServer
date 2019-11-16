package kmalfa;

import java.util.Date;

public class AutoTransfer {
    private String from;
    private String to;
    private float value;
    private Date period;
    private boolean live;
    private RmiServer owner;

    AutoTransfer(String from, String to, float value, Date period, RmiServer owner) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.period = period;
        live = true;
        this.owner = owner;
        new Thread(() -> {
            while (live) {
                try {
                    Thread.sleep(this.period.getTime());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                this.owner.withdrawMoney(this.from, this.value);
                this.owner.replenishAccount(this.to, this.value);
            }
        }).start();
    }

    String getFrom() {
        return from;
    }

    String getTo() {
        return to;
    }

    float getValue() {
        return value;
    }

    Date getPeriod() {
        return period;
    }

    void stop() {
        live = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutoTransfer))
            return false;
        AutoTransfer at = (AutoTransfer) obj;
        return at.getFrom().equals(this.getFrom()) && at.getTo().equals(this.getTo()) && at.getValue() == this.getValue() && at.getPeriod().equals(this.getPeriod());
    }
}
