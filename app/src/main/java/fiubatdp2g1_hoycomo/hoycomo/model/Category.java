package fiubatdp2g1_hoycomo.hoycomo.model;

import fiubatdp2g1_hoycomo.hoycomo.R;

public class Category {
    private String name;
    private int id;
    private int photoId;

    public Category(int id, String name, int photoId) {
        this.id = id;
        this.name = name;
        /*To do replace image category*/
        this.photoId = R.drawable.fast_food_icon;
        //this.photoId = photoId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhotoId() {
        return photoId;
    }

    public void setPhotoId(int photoId) {
        this.photoId = photoId;
    }
}