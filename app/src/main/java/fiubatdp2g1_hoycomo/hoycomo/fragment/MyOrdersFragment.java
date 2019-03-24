package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.OrderActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;


public class MyOrdersFragment extends Fragment {

    private class OrderViewHolder extends RecyclerView.ViewHolder {

        private final ImageView commerceImage;
        private final TextView commerceName;
        private final TextView orderState;
        private final TextView orderPrice;
        private final TextView orderDate;
        //private final LinearLayout orderedDishesLinearLayout;
        private final Activity activity;
        private Order order;


        public OrderViewHolder(View itemView, Activity activity) {
            super(itemView);

            this.commerceImage = itemView.findViewById(R.id.order_commerce_imageview);
            this.commerceName =  itemView.findViewById(R.id.order_commerce_name_textview);
            this.orderState = itemView.findViewById(R.id.order_state_textview);
            //this.orderedDishesLinearLayout = itemView.findViewById(R.id.ordered_dishes_linearlayout);
            this.orderPrice = itemView.findViewById(R.id.order_price_textview);
            this.orderDate = itemView.findViewById(R.id.order_date_textview);


            this.activity = activity;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommerceActivity.SetSelectedCommerce(OrderViewHolder.this.order.getCommerce());
                    OrderActivity.setOrder(OrderViewHolder.this.order);
                    Intent intent = new Intent(OrderViewHolder.this.activity, OrderActivity.class);
                    startActivity(intent);
                }
            });
        }


        public void updateFrom(Order order) {
            Commerce commerce = order.getCommerce();
            this.order = order;
            if (commerce != null) {
                ImagesLoader.Load( commerce.getImageUrl(), this.commerceImage );
                this.commerceName.setText(commerce.getName());
                this.orderState.setText( order.getDisplayableState() );

                this.orderPrice.setText( "$" + Utilities.doubleToStringWithTwoDigits(order.getPrice()));
                this.orderDate.setText( order.getDate() );

                /*
                for (Order.DishOrder dishOrder : order.getDisheOrders()) {
                    TextView myOrderTextView = new TextView( this.activity );

                    //TODO: cambiar esto
                    String textoDelPedido = dishOrder.getDishName();

                    if (dishOrder.hasOptions()) {
                        textoDelPedido += " con";
                    }
                    for (DishOption option : dishOrder.getDishOptions()) {
                        textoDelPedido += " " + option.getName();
                    }
                    myOrderTextView.setText( textoDelPedido );

                    this.orderedDishesLinearLayout.addView(myOrderTextView);
                }
                */
            }
        }
    }

    private class OrderAdapter extends RecyclerView.Adapter<MyOrdersFragment.OrderViewHolder> {
        private final List<Order> orders;
        private final Activity activity;

        public OrderAdapter(List<Order> orders, Activity activity) {
            this.orders = orders;
            this.activity = activity;
        }

        @Override
        public OrderViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.order_list_item, viewGroup, false);
            OrderViewHolder orderViewHolder = new OrderViewHolder( v , this.activity);
            return orderViewHolder;
        }

        @Override
        public void onBindViewHolder(OrderViewHolder holder, int position) {
            holder.updateFrom( this.orders.get(position) );
        }

        @Override
        public int getItemCount() {
            return this.orders.size();
        }
    }

    private RecyclerView myOrdersRecyclerView;
    private List<Order> myOrders;
    public static OrderAdapter orderAdapter;
    private LinearLayoutManager layoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_orders, container, false);
        setHasOptionsMenu(false);

        this.myOrders = new ArrayList<>();
        this.myOrdersRecyclerView = view.findViewById(R.id.my_orders_recyclerView);

        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.myOrdersRecyclerView.setLayoutManager(this.layoutManager);

        this.orderAdapter = new OrderAdapter(this.myOrders, this.getActivity());
        this.myOrdersRecyclerView.setAdapter( this.orderAdapter );

        HoyComoDatabase.getOrders(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                /*Hasta acá tengo:
                    -order_id
                    -dishes ids
                        -options_id
                    -price
                    -state
                    -restaurant_id
                    -user_id
                 */

                MyOrdersFragment.this.myOrders.addAll( HoyComoDatabaseParser.ParseOrdersForUser(HoyComoUserProfile.getUserId(), response) );

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MyOrdersFragment.this.sortByDate();
                        MyOrdersFragment.orderAdapter.notifyDataSetChanged();
                    }
                }, 4000);


            }
        });
        return view;
    }

    private void sortByDate() {
        Collections.sort(this.myOrders, new Comparator<Order>() {
            @Override
            public int compare (Order o1, Order o2){
                int leftId = Integer.valueOf(o1.getId());
                int rightId = Integer.valueOf(o2.getId());
                return leftId < rightId ? 1 : (leftId == rightId ? 0 : -1);
            }
        });
    }

    public static void updateList() {
        MyOrdersFragment.orderAdapter.notifyDataSetChanged();
    }

}
