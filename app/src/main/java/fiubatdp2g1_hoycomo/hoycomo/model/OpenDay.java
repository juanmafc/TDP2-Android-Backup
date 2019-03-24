package fiubatdp2g1_hoycomo.hoycomo.model;

public class OpenDay {

    private String day;
    private String from;
    private String to;

    public OpenDay(String day, String from, String to) {
        this.day = day;
        this.from = from;
        this.to = to;
    }

    public String getDay() {
        return this.day;
    }

    public String getFrom() {
        return this.from;
    }

    public String getTo() {
        return this.to;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

}
