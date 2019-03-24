package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceStarsDialogFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

public class OrderActivity extends AppCompatActivity {


    static private Order orderForSelectedCommerce = null;

    static public void setOrder( Order anOrder ) {
        OrderActivity.orderForSelectedCommerce = anOrder;
    }

    public static Order getOrder() {
        return OrderActivity.orderForSelectedCommerce;
    }


    public static void addDish(Dish dish, List<DishOption> selectedOptions, int quantity) {
        OrderActivity.getOrder().addDish(dish, selectedOptions, quantity);
    }

    public static String getOrderId() {
        return OrderActivity.getOrder().getId();
    }

    public static int getNumberOfDishes() {
        return OrderActivity.getOrder().getNumberOfDishes();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        Commerce selectedCommerce = CommerceActivity.getSelectedCommerce();

        TextView restaurantTextView = findViewById(R.id.order_for_restaurant_name_textview);
        restaurantTextView.setText(selectedCommerce.getName());

        Button orderPriceButton = findViewById(R.id.order_price_textview);
        orderPriceButton.setText( "$" + Utilities.doubleToStringWithTwoDigits( OrderActivity.getOrder().getPrice() ) );


        TextView orderStateTextView = findViewById(R.id.order_state_textview);
        orderStateTextView.setText(OrderActivity.getOrder().getDisplayableState());


        LinearLayout orderedDishesLinearLayout = findViewById(R.id.ordered_dishes_linear_layout);


        LayoutInflater inflater = LayoutInflater.from(this);


        for ( Order.DishOrder dishOrder : OrderActivity.getOrder().getDisheOrders() ){
            View orderedDishLayout = inflater.inflate(R.layout.ordered_dish, null, false);
            CardView orderedDishItem = orderedDishLayout.findViewById(R.id.ordered_dish_cardview);

            //Dish image
            ImageView orderedDishImage = orderedDishItem.findViewById(R.id.ordered_dish_image_view);
            ImagesLoader.Load(dishOrder.getImageUrl(), orderedDishImage);


            //Dish name
            TextView orderedDishName = orderedDishItem.findViewById(R.id.ordered_dish_name_textview);
            orderedDishName.setText(dishOrder.getDishName());

            //Dish Quantity
            TextView orderedDishQuantity = orderedDishItem.findViewById(R.id.ordered_dish_quantity);
            int q = dishOrder.getQuantity();
            if (q > 1) {
                orderedDishQuantity.setText(dishOrder.getQuantity() + " x ");
            }

            //Order price
            TextView orderedDishPrice = orderedDishItem.findViewById(R.id.ordered_dish_price);
            orderedDishPrice.setText( "$" + Utilities.doubleToStringWithTwoDigits( dishOrder.getOneDishPrice() ));

            //Dish options linear layout
            LinearLayout dishOptionsLinearLayout = orderedDishItem.findViewById(R.id.ordered_dish_options_linear_layout);

            if (dishOrder.hasOptions()) {
                TextView addedOptionsTextView = new TextView(this);
                addedOptionsTextView.setText("Opciones agregadas");
                dishOptionsLinearLayout.addView(addedOptionsTextView);

                for (DishOption dishOption : dishOrder.getDishOptions() ) {
                    TextView optionTextView = new TextView(this);
                    optionTextView.setText( dishOption.getName() + " ($" + Utilities.doubleToStringWithTwoDigits(dishOption.getPrice() ) + ")" );
                    dishOptionsLinearLayout.addView(optionTextView);
                }

            }
            else {
                TextView noOptionsTextView = new TextView(this);
                noOptionsTextView.setText("No agregaste opciones a este plato");
                dishOptionsLinearLayout.addView(noOptionsTextView);
            }

            orderedDishesLinearLayout.addView(orderedDishLayout);
        }


        RelativeLayout order_buttons = this.findViewById(R.id.order_buttons);


        String orderState = OrderActivity.getOrder().getState();
        if (orderState.equals("ordering")){
            View confirmOrderButtons = inflater.inflate(R.layout.order_send_buttons, null, false);
            order_buttons.addView(confirmOrderButtons);
        }
        else if (orderState.equals("waiting")){
            View cancelOrderButton = inflater.inflate(R.layout.order_cancel_buttons, null, false);
            order_buttons.addView(cancelOrderButton);
        }
        else if (orderState.equals("delivered-needs-confirmation")) {
            View cancelOrderButton = inflater.inflate(R.layout.order_confirm_delivery_buttons, null, false);
            order_buttons.addView(cancelOrderButton);
        }

        String mode = getIntent().getStringExtra("mode");
        if (mode != null) {
            if (mode.equals("reject")) {
                this.rejectDelivery(null);
            }
            else if (mode.equals("confirm")) {
                this.confirmDelivery(null);
            }

        }
    }


    public void confirmOrder(View view) {
        OrderActivity.setOrderDate( Utilities.getCurrentDateAndTime() );


        //HoyComoDatabase.sendOrder(HoyComoUserProfile.getUserId(), CommerceActivity.getSelectedCommerceId(),OrderActivity.getOrder());
        //Intent myIntent = new Intent(this, NavigationMenuActivity.class);

        Intent myIntent = new Intent(this, ConfirmOrderActivity.class);
        this.startActivity(myIntent);
    }

    private static void setOrderDate(String currentDateAndTime) {
        OrderActivity.getOrder().setDate(currentDateAndTime);
    }

    public void addMoreDishesToOrder(View view) {
        Intent myIntent = new Intent(this, CommerceActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
    }

    public void cancelOrder(View view) {
        HoyComoDatabase.changeOrderState(OrderActivity.getOrderId(), "canceled", null);
        Intent myIntent = new Intent(this, NavigationMenuActivity.class);
        this.startActivity(myIntent);
    }

    public void confirmDelivery(View view) {
        this.showStarsDialog("delivered-confirmed");
    }

    public void rejectDelivery(View view) {
        this.showStarsDialog("delivered-rejected");
    }

    private void showStarsDialog(String newState) {
        CommerceStarsDialogFragment dialog = new CommerceStarsDialogFragment();
        dialog.setNewOrderState( newState, OrderActivity.getOrder().getDeliveredTime() );
        dialog.show(this.getFragmentManager(), "CommerceStarsDialogFragment");
    }


}
