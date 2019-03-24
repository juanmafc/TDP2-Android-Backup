package fiubatdp2g1_hoycomo.hoycomo.model;

public class DishOption {

    private final int id;
    public final String name;
    public final String description;
    public final double price;
    private final int optionType;


    boolean selected;

    public DishOption(int id, String name, String description, double price, int optionType) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.optionType = optionType;

        this.selected = false;
    }

    public void selected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public double getPrice() {
        return this.price;
    }


    public String getName() {
        return this.name;
    }

    public int getId() {
        return this.id;
    }

    public int getType() {
        return this.optionType;
    }
}
