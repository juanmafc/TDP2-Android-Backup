package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.DishGarnishesDialogFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.DishOptionsDialogFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;
import fiubatdp2g1_hoycomo.hoycomo.model.DishOption;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class DishActivity extends AppCompatActivity implements DishOptionsDialogFragment.onOptionsSelectedListener, DishGarnishesDialogFragment.onGarnishesSelectedListener {


    private HashMap<View, DishOption> selectedOptionsViewMap = new HashMap<>();

    static private Dish selectedDish = null;
    private List<DishOption> selectedOptions= null;

    private CollapsingToolbarLayout collapsingToolbar;

    private LinearLayout selectedExtrasLinearLayout;
    private ScrollView selectedExtrasScrollview;
    private Button addExtrasButton;
    private TextView addExtrasTextView;
    private Button addMoreExtrasButton;

    private View separatorFirst;
    private View separatorSecond;
    private View separatorThird;

    private Button addDishToOrderButton;
    private double orderPrice;
    private LinearLayout selectedGarnishesLinearLayout;
    private ScrollView selectedGarnishesScrollview;
    private Button addGarnishesButton;
    private TextView addGarnishesTextView;
    private Button addMoreGarnishesButton;
    private TextView quantity;

    public static void setSelectedDish(Dish selectedDish) {
        DishActivity.selectedDish = selectedDish;
    }

    public static Dish getSelectedDish() {
        return DishActivity.selectedDish;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        this.selectedOptions = new ArrayList<>();

        this.addDishToOrderButton = findViewById(R.id.add_dish_to_order_button);

        this.orderPrice = DishActivity.selectedDish.getPriceWithDiscount();
        this.updatePriceOnAddToOrderButton();

        ImageView commerceImage = findViewById(R.id.dish_activity_dish_image);
        ImagesLoader.Load(CommerceActivity.getSelectedCommerce().getImageUrl(), commerceImage);

        this.collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.dish_activity_collapsing_toolbar_dish_info);
        this.collapsingToolbar.setTitle(" "); // no title bar in the dish

        TextView dishNameText = (TextView) findViewById(R.id.content_dish_name);
        dishNameText.setText(selectedDish.getName());

        TextView priceText = (TextView) findViewById(R.id.content_dish_price_number);
        priceText.setText("$" + String.valueOf(selectedDish.getPrice()));

        if (selectedDish.getDiscount() > 0) {
            ImageView discountImage = findViewById(R.id.dish_activity_dish_discount);
            discountImage.setVisibility(View.VISIBLE);
            TextView discountText = findViewById(R.id.dish_activity_dish_discount_text);
            discountText.setVisibility(View.VISIBLE);
            discountText.setText(String.valueOf(selectedDish.getDiscount())+ "%");
            priceText.setText("$" + String.valueOf(selectedDish.getPriceWithDiscount()));
            TextView priceTextNoDiscount = (TextView) findViewById(R.id.content_dish_price_number_no_discount);
            priceTextNoDiscount.setVisibility(View.VISIBLE);
            priceTextNoDiscount.setText("$" + String.valueOf(selectedDish.getPrice()));
            priceTextNoDiscount.setPaintFlags(priceTextNoDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        TextView descriptionText = (TextView) findViewById(R.id.content_dish_description_text);
        descriptionText.setMovementMethod(new ScrollingMovementMethod());
        descriptionText.setText(selectedDish.getDescription());

        //Extras
        this.selectedExtrasLinearLayout = findViewById(R.id.selected_options_linearlayout);
        this.selectedExtrasScrollview = findViewById(R.id.selected_options_scrollview);
        this.addExtrasButton = (Button) findViewById(R.id.content_dish_option_button);
        this.addExtrasTextView = (TextView) findViewById(R.id.content_dish_option_text);

        //Separators
        this.separatorFirst = (View) findViewById(R.id.separator_first);
        this.separatorSecond = (View) findViewById(R.id.separator_second);
        this.separatorThird = (View) findViewById(R.id.separator_third);

        this.addMoreExtrasButton = (Button) findViewById(R.id.add_more_options_button);

        addExtrasTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishOptionsDialogFragment dialog = new DishOptionsDialogFragment();
                dialog.show(getFragmentManager(), "DishOptionsDialogFragment");
            }
        });

        addExtrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishOptionsDialogFragment dialog = new DishOptionsDialogFragment();
                dialog.show(getFragmentManager(), "DishOptionsDialogFragment");
            }
        });

        addMoreExtrasButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishOptionsDialogFragment dialog = new DishOptionsDialogFragment();
                dialog.show(getFragmentManager(), "DishOptionsDialogFragment");
            }
        });

        if ( !DishActivity.getSelectedDish().hasExtras() ) {
            this.hideAddExtrasViews();
            this.separatorThird.setVisibility(View.INVISIBLE);
        }


        //Garnishes
        this.selectedGarnishesLinearLayout = findViewById(R.id.selected_garnishes_linearlayout);
        this.selectedGarnishesScrollview = findViewById(R.id.selected_garnishes_scrollview);
        this.addGarnishesButton = (Button) findViewById(R.id.add_garnishes_button);
        this.addGarnishesTextView = (TextView) findViewById(R.id.add_garnishes_textview);
        this.addMoreGarnishesButton = (Button) findViewById(R.id.add_more_garnishes_button);

        addGarnishesTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishGarnishesDialogFragment dialog = new DishGarnishesDialogFragment();
                dialog.show(getFragmentManager(), "DishGarnishesDialogFragment");
            }
        });

        addGarnishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishGarnishesDialogFragment dialog = new DishGarnishesDialogFragment();
                dialog.show(getFragmentManager(), "DishGarnishesDialogFragment");
            }
        });

        addMoreGarnishesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishGarnishesDialogFragment dialog = new DishGarnishesDialogFragment();
                dialog.show(getFragmentManager(), "DishGarnishesDialogFragment");
            }
        });

        if ( !DishActivity.getSelectedDish().hasGarnishes() ) {
            this.hideAddGarnishesViews();
            this.separatorFirst.setVisibility(View.INVISIBLE);
            this.separatorSecond.setVisibility(View.INVISIBLE);
        }

        this.quantity = (TextView) findViewById(R.id.content_dish_quantity_number);
        this.quantity.setFocusable(false);
        Button plusOneButton = (Button) findViewById(R.id.content_dish_quantity_more);
        Button lessOneButton = (Button) findViewById(R.id.content_dish_quantity_less);

        lessOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishActivity.this.decreaseOne();
            }
        });

        plusOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DishActivity.this.increaseOne();
            }
        });
    }

    private void decreaseOne() {
        int number = Integer.valueOf(this.quantity.getText().toString());
        if ( number >= 2 ) {
            this.quantity.setText(String.valueOf(number - 1));
        }
        this.updatePriceOnAddToOrderButton();
    }

    private void increaseOne() {
        int number = Integer.valueOf(this.quantity.getText().toString());
        this.quantity.setText(String.valueOf(number + 1));
        this.updatePriceOnAddToOrderButton();
    }

    private void showAddGarnishesViews() {
        addGarnishesButton.setVisibility(View.VISIBLE);
        addGarnishesTextView.setVisibility(View.VISIBLE);
    }

    private void showAddExtrasViews() {
        addExtrasButton.setVisibility(View.VISIBLE);
        addExtrasTextView.setVisibility(View.VISIBLE);
    }

    private void hideAddExtrasViews() {
        addExtrasButton.setVisibility(View.GONE);
        addExtrasTextView.setVisibility(View.GONE);
    }

    private void hideAddGarnishesViews() {
        addGarnishesButton.setVisibility(View.GONE);
        addGarnishesTextView.setVisibility(View.GONE);
    }

    @Override
    public void sendSelectedExtras(List<DishOption> selectedOptions) {

        if (selectedOptions.size() > 0) {
            this.hideAddExtrasViews();

            this.selectedExtrasScrollview.setVisibility(View.VISIBLE);
            this.addMoreExtrasButton.setVisibility(View.VISIBLE);

            LayoutInflater inflater = LayoutInflater.from(this);

            for (DishOption dishOption : selectedOptions ){
                View selectedOptionLayout = inflater.inflate(R.layout.selected_option, null, false);
                TextView selectedOptionName = selectedOptionLayout.findViewById(R.id.selected_option_textview);
                selectedOptionName.setText(dishOption.getName() + " ($" + Utilities.doubleToStringWithTwoDigits( dishOption.getPrice() ) + ")");
                this.selectedExtrasLinearLayout.addView(selectedOptionLayout);
                this.selectedOptionsViewMap.put( selectedOptionLayout, dishOption );
                this.orderPrice += dishOption.getPrice();
            }

            this.selectedOptions.addAll(selectedOptions);
            this.updatePriceOnAddToOrderButton();
        }
    }

    @Override
    public void sendSelectedGarnishes(List<DishOption> selectedOptions) {

        while ( 0 < this.selectedGarnishesLinearLayout.getChildCount()  ) {
            View selectedGarnishView = this.selectedGarnishesLinearLayout.getChildAt(0);
            DishOption deletedDishOption = this.selectedOptionsViewMap.get( selectedGarnishView );
            this.selectedGarnishesLinearLayout.removeView( selectedGarnishView );

            this.selectedOptionsViewMap.remove( selectedGarnishView );
            this.selectedOptions.remove( deletedDishOption );

            this.orderPrice -= deletedDishOption.getPrice();
        }


        this.updatePriceOnAddToOrderButton();



        if (selectedOptions.size() > 0) {
            this.hideAddGarnishesViews();

            this.selectedGarnishesScrollview.setVisibility(View.VISIBLE);
            this.addMoreGarnishesButton.setVisibility(View.VISIBLE);

            LayoutInflater inflater = LayoutInflater.from(this);
            for (DishOption dishOption : selectedOptions ){
                View selectedOptionLayout = inflater.inflate(R.layout.selected_option, null, false);
                TextView selectedOptionName = selectedOptionLayout.findViewById(R.id.selected_option_textview);
                selectedOptionName.setText(dishOption.getName() + " ($" + Utilities.doubleToStringWithTwoDigits( dishOption.getPrice() ) + ")");
                this.selectedGarnishesLinearLayout.addView(selectedOptionLayout);
                this.selectedOptionsViewMap.put( selectedOptionLayout, dishOption );
                this.orderPrice += dishOption.getPrice();
            }

            this.selectedOptions.addAll(selectedOptions);
            this.updatePriceOnAddToOrderButton();
        }
        else {
            this.selectedGarnishesScrollview.setVisibility(View.GONE);
            this.addMoreGarnishesButton.setVisibility(View.GONE);
            this.showAddGarnishesViews();
        }

    }

    public void removeOption(View removeOptionButton) {
        View parentView = (View) removeOptionButton.getParent();
        LinearLayout linearLayout = (LinearLayout) parentView.getParent();

        DishOption deletedDishOption = this.selectedOptionsViewMap.get( parentView );
        deletedDishOption.selected(false);


        linearLayout.removeView( parentView );
        //this.selectedExtrasLinearLayout.removeView( parentView );

        this.selectedOptionsViewMap.remove( parentView );
        this.selectedOptions.remove( deletedDishOption );

        this.orderPrice -= deletedDishOption.getPrice();
        this.updatePriceOnAddToOrderButton();

        if ( (linearLayout == this.selectedExtrasLinearLayout) && ( this.selectedExtrasLinearLayout.getChildCount() == 0 ) ){
            this.selectedExtrasScrollview.setVisibility(View.GONE);
            this.addMoreExtrasButton.setVisibility(View.GONE);
            this.showAddExtrasViews();
        }

        if  ( (linearLayout == this.selectedGarnishesLinearLayout) && ( this.selectedGarnishesLinearLayout.getChildCount() == 0 ) ){
            this.selectedGarnishesScrollview.setVisibility(View.GONE);
            this.addMoreGarnishesButton.setVisibility(View.GONE);
            this.showAddGarnishesViews();
        }

    }

    public void addDishToOrderActivity(View view) {
        Button addDishToOrderButton = (Button) view;
        OrderActivity.addDish(DishActivity.getSelectedDish(), this.selectedOptions, Integer.valueOf(this.quantity.getText().toString())  );

        Intent myIntent = new Intent(this, OrderActivity.class);
        myIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(myIntent);
    }


    public void updatePriceOnAddToOrderButton() {
        int amountOfDishes = 1;
        if (this.quantity != null) {
            amountOfDishes = Integer.valueOf(this.quantity.getText().toString());
        }
        this.addDishToOrderButton.setText("Agregar a Mi Pedido ($" + Utilities.doubleToStringWithTwoDigits(this.orderPrice * amountOfDishes) + ")" );
    }
}
