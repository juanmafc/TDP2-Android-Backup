package fiubatdp2g1_hoycomo.hoycomo.model;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;


public class Order {

    public static class DishOrder {
        private int quantity = 1;
        private Dish dish;
        private List<DishOption> dishOptions = new ArrayList<>();
        private double price = 0;
        private double oneDishPrice = 0;

        public DishOrder(Dish dish, List<DishOption> dishOptions, int quantity) {
            this.dish = dish;
            this.quantity = quantity;
            if (this.dish.getDiscount() > 0) {
                this.price += this.getDishPriceWithDiscount();
            } else {
                this.price += this.getDishPrice();
            }
            this.dishOptions.addAll(dishOptions);
            this.price += this.getOptionsPrice();
            this.oneDishPrice = this.price ;
            this.price = this.price * this.quantity;
        }

        public DishOrder(String commerceId, String dishId, List<String> optionsId, final int quantity) {
            HoyComoDatabase.getDish(commerceId, dishId,  new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    DishOrder.this.dish = HoyComoDatabaseParser.ParseDish(response);
                    if (DishOrder.this.getDiscount() > 0) {
                        DishOrder.this.price += DishOrder.this.getDishPriceWithDiscount();
                        DishOrder.this.oneDishPrice = DishOrder.this.getDishPriceWithDiscount();
                    } else {
                        DishOrder.this.price += DishOrder.this.getDishPrice();
                        DishOrder.this.oneDishPrice = DishOrder.this.getDishPrice();
                    }
                    DishOrder.this.quantity = quantity;
                }
            });

            for (String optionId : optionsId) {

                HoyComoDatabase.getDishOptions(commerceId, dishId, optionId, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        DishOrder.this.addOption( HoyComoDatabaseParser.ParseDishOption(response) );
                    }
                });
            }
        }

        private void addOption(DishOption option) {
            this.dishOptions.add(option);
            this.price += option.getPrice();
        }

        public int getQuantity() {
            return quantity;
        }

        public double getOneDishPrice() {
            return oneDishPrice;
        }

        public double getTotalPrice() {
            return this.price;
        }

        public String getDishName() {
            return this.dish.getName();
        }

        public List<DishOption> getDishOptions() {
            return dishOptions;
        }

        public double getDiscount() {
            return this.dish.getDiscount();
        }

        public double getDishPrice() {
            return this.dish.getPrice();
        }

        public double getDishPriceWithDiscount() {
            return this.dish.getPriceWithDiscount();
        }

        public double getOptionsPrice() {
            double optionsPrice = 0;
            for (DishOption dishOption : this.dishOptions)  {
                optionsPrice  += dishOption.getPrice();
            }
            return optionsPrice;
        }

        public int getDishId() {
            return this.dish.getId();
        }

        public boolean hasOptions() {
            return !(this.dishOptions.isEmpty());
        }

        public String getImageUrl() {
            return this.dish.getImageUrl();
        }
    }

    Commerce commerce;
    double totalPrice = 0;
    List<DishOrder> orderedDishes = new ArrayList<>();
    String state;
    private String orderId = "-1";
    private String date = "66/66/6666 66:66";
    private String updateTime = null;

    public Order(Commerce commerce) {
        this.commerce = commerce;
        this.state = "ordering";
    }


    public Order(String orderId, String commerceId, String state, Double price, String date, String updateTime) {
        this.orderId = orderId;
        HoyComoDatabase.getCommerce(commerceId,  new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Order.this.commerce = HoyComoDatabaseParser.ParseCommerce(response);
                //MyOrdersFragment.updateList();
            }
        });
        this.state = state;
        this.totalPrice = price;
        this.date = date;
        this.updateTime = updateTime;
    }


    public void addDish(Dish dish, List<DishOption> selectedOptions, int quantity) {
        DishOrder dishOrder = new DishOrder(dish, selectedOptions, quantity);
        this.addDishOrder(dishOrder);
    }

    public void addDishOrder(DishOrder dishOrder) {
        this.orderedDishes.add( dishOrder );
        this.totalPrice += dishOrder.getTotalPrice();
    }


    public void addDishOrderWithoutAddingPrice(DishOrder dishOrder) {
        this.orderedDishes.add( dishOrder );
    }




    public Commerce getCommerce() {
        return this.commerce;
    }

    public String getState() {
        if (this.state.isEmpty()) {
            return "ESTADO HARDCODEADO EN ORDER";
        }
        return this.state;
    }

    public String getDisplayableState() {
        if (this.state.equals("ordering")) {
            return "Pidiendo";
        }
        else if (this.state.equals("canceled")) {
            return "Cancelado";
        }
        else if (this.state.equals("waiting")) {
            return "En espera";
        }
        else if (this.state.equals("preparing")) {
            return "En preparación";
        }
        else if (this.state.equals("finished")) {
            return "Terminado";
        }
        else if (this.state.equals("sending")) {
            return "En envío";
        }
        else if (this.state.equals("delivered-needs-confirmation")) {
            return "Entregado";
        }
        else if (this.state.equals("delivered-rejected")) {
            return "Rechazado";
        }
        else if (this.state.equals("delivered-confirmed")) {
            return "Entregado";
        }
        return "ESTADO INCORRECTO";
    }




    public double getPrice() {
        return this.totalPrice;
    }

    public List<DishOrder> getDisheOrders() {
        return this.orderedDishes;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    public String getId() {
        return this.orderId;
    }

    public int getNumberOfDishes() {
        return this.orderedDishes.size();
    }

    public String getDeliveredTime() {
        return updateTime;
    }

}
