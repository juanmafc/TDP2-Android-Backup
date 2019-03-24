package fiubatdp2g1_hoycomo.hoycomo.model;

import java.util.ArrayList;
import java.util.List;

public class Dish {

    private String category;
    private int id;
    private String name;
    private String description;
    private boolean activate;
    private double discount = 0;
    private double price;
    private String imageUrl;
    
    private List<DishOption> extras = new ArrayList<>();;
    private List<DishOption> garnishes = new ArrayList<>();;

    public Dish(int id, String name, String description, double price, String imageUrl, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.category = category;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActivate() {
        return activate;
    }

    public void setActivate(boolean activate) {
        this.activate = activate;
    }

    public int getDiscount() {
        return (int) discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCategory() {
        return category;
    }

    public void setOptions(List<DishOption> options) {
        for(DishOption option : options) {
            if (option.getType() == 1) {
                this.extras.add(option);
            }
            else if (option.getType() == 2) {
                this.garnishes.add(option);
            }
        }
    }

    public List<DishOption> getExtras() {
        return this.extras;
    }

    public boolean hasExtras() {
        return !(this.extras.isEmpty());
    }

    public List<DishOption> getGarnishes() {
        return this.garnishes;
    }

    public boolean hasGarnishes() {
        return !(this.garnishes.isEmpty());
    }

    public double getPriceWithDiscount() {
        return (this.price*(100-this.discount))/100;
    }

}
