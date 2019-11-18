package kmalfa;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

class AutoTransfer implements Serializable {
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
                    System.err.println(e.getMessage());
                    Thread.currentThread().interrupt();
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AutoTransfer that = (AutoTransfer) o;
        return Float.compare(that.getValue(), getValue()) == 0 &&
                live == that.live &&
                Objects.equals(getFrom(), that.getFrom()) &&
                Objects.equals(getTo(), that.getTo()) &&
                Objects.equals(getPeriod(), that.getPeriod()) &&
                Objects.equals(owner, that.owner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFrom(), getTo(), getValue(), getPeriod(), live, owner);
    }
}
