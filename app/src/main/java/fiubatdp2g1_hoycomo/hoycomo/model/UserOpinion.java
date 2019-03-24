package fiubatdp2g1_hoycomo.hoycomo.model;

public class UserOpinion {

    private final String userName;
    private final int id;
    private final String time;
    private String userId;
    int stars;
    String comment;

    public UserOpinion(int id, String userId, int stars, String comment, String userName, String time) {
        this.id = id;
        this.userId = userId;
        this.stars = stars;
        this.comment = comment;
        this.userName = userName;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public int getStars() {
        return this.stars;
    }

    public String getComment() {
        return this.comment;
    }

    public String getUserId() {
        return this.userId;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getTime() {
        return this.time;
    }
}
