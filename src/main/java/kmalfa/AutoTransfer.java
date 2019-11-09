package kmalfa;

import java.util.Date;

public class AutoTransfer {
    private String from;
    private String to;
    private float value;
    private Date period;

    AutoTransfer(String from, String to, float value, Date period) {
        this.from = from;
        this.to = to;
        this.value = value;
        this.period = period;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof AutoTransfer))
            return false;
        AutoTransfer at = (AutoTransfer) obj;
        return at.getFrom().equals(this.getFrom()) && at.getTo().equals(this.getTo()) && at.getValue() == this.getValue() && at.getPeriod().equals(this.getPeriod());
    }
}
