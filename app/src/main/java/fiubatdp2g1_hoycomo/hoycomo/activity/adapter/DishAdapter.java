package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.DishActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Dish;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishViewHolder>{

    List<Dish> dishes;

    public static class DishViewHolder extends RecyclerView.ViewHolder {
        public CardView cv;
        public Dish dish;
        public TextView dishName;
        public TextView dishDescription;
        public ImageView dishPhoto;
        public TextView dishPrice;
        public TextView dishPriceTextNoDiscount;
        public TextView dishDiscountText;
        public ImageView dishDiscountImage;

        DishViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv_dish);
            dishName = (TextView)itemView.findViewById(R.id.dish_name);
            dishDescription = (TextView)itemView.findViewById(R.id.dish_description);
            dishDiscountText = (TextView)itemView.findViewById(R.id.dish_discount_text);
            dishDiscountImage = (ImageView)itemView.findViewById(R.id.dish_discount);
            dishPrice = (TextView)itemView.findViewById(R.id.dish_price);
            dishPriceTextNoDiscount = (TextView)itemView.findViewById(R.id.dish_price_no_discount);
            //dishPhoto = (ImageView)itemView.findViewById(R.id.image_view_dish_icon);

            cv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DishActivity.setSelectedDish(DishViewHolder.this.dish);
                    cv.getContext().startActivity(new Intent(cv.getContext(),DishActivity.class));
                }
            });
        }

    }

    public DishAdapter(List<Dish> dishes){
        this.dishes = dishes;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public DishViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.dishes_list_item_information, viewGroup, false);
        DishViewHolder pvh = new DishViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(DishViewHolder dishViewHolder, int i) {
        dishViewHolder.dish = dishes.get(i);
        dishViewHolder.dishName.setText(dishes.get(i).getName());
        dishViewHolder.dishDescription.setText(dishes.get(i).getDescription());
        //dishViewHolder.dishPhoto.setImageResource(dishes.get(i).getPhotoId());
        if (dishes.get(i).getDiscount() > 0) {
            dishViewHolder.dishDiscountImage.setVisibility(View.VISIBLE);
            dishViewHolder.dishDiscountText.setVisibility(View.VISIBLE);
            dishViewHolder.dishDiscountText.setText(String.valueOf(dishes.get(i).getDiscount()) + "%");
            dishViewHolder.dishPrice.setText("$" + String.valueOf(dishes.get(i).getPriceWithDiscount()));
            dishViewHolder.dishPriceTextNoDiscount.setVisibility(View.VISIBLE);
            dishViewHolder.dishPriceTextNoDiscount.setText("$" + String.valueOf(dishes.get(i).getPrice()));
            dishViewHolder.dishPriceTextNoDiscount.setPaintFlags(dishViewHolder.dishPriceTextNoDiscount.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            dishViewHolder.dishPrice.setText("$" + String.valueOf(dishes.get(i).getPrice()));
        }
    }

    @Override
    public int getItemCount() {
        return dishes.size();
    }

}
