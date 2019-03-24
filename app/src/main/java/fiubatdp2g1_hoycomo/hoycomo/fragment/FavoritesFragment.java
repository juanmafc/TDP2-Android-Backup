package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.model.LeadTimeStatistics;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class FavoritesFragment extends Fragment {

    private Map<Integer, LeadTimeStatistics> leadTimeMap = new HashMap<>();

    private class FavoritesViewHolder extends RecyclerView.ViewHolder {

        private final ImageView commerceImage;
        private final TextView commerceName;
        private final Activity activity;
        private final TextView categories;
        private final TextView score;


        private final TextView leadTime;
        private final TextView distance;


        private Commerce commerce;

        public FavoritesViewHolder(View itemView, Activity activity) {
            super(itemView);

            this.commerceImage = itemView.findViewById(R.id.commerce_list_item_information_logo);
            this.commerceName =  itemView.findViewById(R.id.commerce_list_item_information_name);
            this.categories = itemView.findViewById(R.id.commerce_list_item_information_categories);
            this.score = itemView.findViewById(R.id.commerce_list_item_information_score);

            this.distance = itemView.findViewById(R.id.commerce_list_item_information_distance);
            this.leadTime = itemView.findViewById(R.id.lead_time_textview);

            // hide promotion
            ImageView promotionImage = (ImageView) itemView.findViewById(R.id.commerce_list_item_information_promotion_img);
            TextView promotion = (TextView) itemView.findViewById(R.id.commerce_list_item_information_promotion_text);
            promotionImage.setVisibility(View.INVISIBLE);
            promotion.setText("");

            this.activity = activity;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommerceActivity.SetSelectedCommerce(FavoritesViewHolder.this.commerce);
                    Intent intent = new Intent(FavoritesViewHolder.this.activity, CommerceActivity.class);
                    startActivity(intent);
                }
            });
        }

        public void updateFrom(Commerce commerce) {
            this.commerce = commerce;
            ImagesLoader.Load( commerce.getImageUrl(), this.commerceImage );
            this.commerceName.setText(commerce.getName());
            this.categories.setText(commerce.getCategoriesString(", "));
            if (commerce.hasRating()){
               this.score.setText(commerce.getRatingAsString());
            }
            else {
               this.score.setText("-");
            }

            final String commerceId = commerce.getId();
            final ImageView favoriteImageLike = (ImageView) itemView.findViewById(R.id.commerce_list_item_information_like);
            final ImageView favoriteImageNotLike = (ImageView) itemView.findViewById(R.id.commerce_list_item_information_not_like);

            favoriteImageNotLike.setVisibility(View.VISIBLE);
            favoriteImageLike.setVisibility(View.INVISIBLE);


            this.distance.setText( commerce.getDistanceStringInKmFromMyGPS(activity));




            this.leadTime.setText(commerce.getAverageLeadTimeString());


            final String userId = HoyComoUserProfile.getUserId();

            HoyComoDatabase.getOneUserFavoritesCommerces(userId, commerceId, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    int cId = -1;
                    try {
                        cId = response.getInt("restaurant_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (String.valueOf(cId).equals(commerceId)) {
                        favoriteImageLike.setVisibility(View.VISIBLE);
                        favoriteImageNotLike.setVisibility(View.INVISIBLE);
                    } else {
                        favoriteImageLike.setVisibility(View.INVISIBLE);
                        favoriteImageNotLike.setVisibility(View.VISIBLE);
                    }
                }
            });

            favoriteImageLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteImageLike.setVisibility(View.INVISIBLE);
                    favoriteImageNotLike.setVisibility(View.VISIBLE);
                    HoyComoDatabase.deleteUserFavoriteCommerce(HoyComoUserProfile.getUserId(), commerceId);
                }
            });

            favoriteImageNotLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteImageNotLike.setVisibility(View.INVISIBLE);
                    favoriteImageLike.setVisibility(View.VISIBLE);
                    HoyComoDatabase.postUserFavoriteCommerce(HoyComoUserProfile.getUserId(), commerceId);
                }
            });

        }
    }

    private class FavoritesAdapter extends RecyclerView.Adapter<FavoritesFragment.FavoritesViewHolder> {
        private final List<Commerce> commerces;
        private final Activity activity;

        public FavoritesAdapter(List<Commerce> commerces, Activity activity) {
            this.commerces = commerces;
            this.activity = activity;
        }

        @Override
        public FavoritesViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorites_list_item, viewGroup, false);
            FavoritesViewHolder orderViewHolder = new FavoritesViewHolder( v , this.activity);
            return orderViewHolder;
        }

        @Override
        public void onBindViewHolder(FavoritesViewHolder holder, int position) {
            holder.updateFrom( this.commerces.get(position) );
        }

        @Override
        public int getItemCount() {
            return this.commerces.size();
        }
    }

    private RecyclerView myFavoritesRecyclerView;
    private ArrayList<Commerce> myFavoritesCommerces;
    public static FavoritesAdapter favoritesAdapter;
    private FragmentManager fragmentManager;
    private LinearLayoutManager layoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.favorites, container, false);
        setHasOptionsMenu(false);

        this.myFavoritesCommerces = new ArrayList<>();
        this.myFavoritesRecyclerView = view.findViewById(R.id.favorites_recyclerView);

        this.layoutManager = new LinearLayoutManager(this.getActivity());
        this.myFavoritesRecyclerView.setLayoutManager(this.layoutManager);

        this.favoritesAdapter = new FavoritesAdapter(this.myFavoritesCommerces, this.getActivity());
        this.myFavoritesRecyclerView.setAdapter( this.favoritesAdapter );




        HoyComoDatabase.getOrders(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //TODO: if you are readinig this, yes this is horribly rushed copied pasted code
                    JSONArray ordersJsonArray = response.getJSONArray("orders");
                    for (int i = 0; i < ordersJsonArray.length(); i++ ){
                        JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
                        if ( orderJsonObject.getString("state").equals("delivered-confirmed") ) {
                            int commerceId = orderJsonObject.getInt("restaurant_id");
                            String orderCreationTime = orderJsonObject.getString("time");
                            String deliveryTime = orderJsonObject.getString("update_time");
                            long leadTime = Utilities.getTimeDifferencesInMinutes(deliveryTime, orderCreationTime);

                            if ( !FavoritesFragment.this.leadTimeMap.containsKey(commerceId) ) {
                                FavoritesFragment.this.leadTimeMap.put(commerceId, new LeadTimeStatistics());
                            }
                            LeadTimeStatistics leadTimeStatisticsForCurrentCommerce = FavoritesFragment.this.leadTimeMap.get(commerceId);
                            leadTimeStatisticsForCurrentCommerce.accummulatedLeadTime += leadTime;
                            leadTimeStatisticsForCurrentCommerce.completedOrders++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }




                HoyComoDatabase.getUserFavoritesCommercesList(HoyComoUserProfile.getUserId(), new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        ArrayList<String> commercesIds = HoyComoDatabaseParser.ParseFavorites(response);

                        for (final String cId : commercesIds) {
                            HoyComoDatabase.getCommerce(cId, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Commerce favoriteCommerce = HoyComoDatabaseParser.ParseCommerce(response);
                                    int commerceIdInt = Utilities.stringToInt(cId);
                                    if (FavoritesFragment.this.leadTimeMap.containsKey( commerceIdInt )){
                                        favoriteCommerce.setAverageLeadTime(  FavoritesFragment.this.leadTimeMap.get( commerceIdInt ).getAverageLeadTimeString()   );
                                    }
                                    else {
                                        favoriteCommerce.setAverageLeadTime(null);
                                    }
                                    FavoritesFragment.this.myFavoritesCommerces.add( favoriteCommerce );
                                }
                            });
                        }

                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FavoritesFragment.this.sortAlphabetically();
                                FavoritesFragment.favoritesAdapter.notifyDataSetChanged();
                            }
                        }, 2000);

                    }
                });






            }
        });







        //this.updateList();
        return view;
    }

    public static void updateList() {
        FavoritesFragment.favoritesAdapter.notifyDataSetChanged();
    }

    private void sortAlphabetically() {
        Collections.sort(this.myFavoritesCommerces, new Comparator<Commerce>() {

            public String cleanString(String texto) {
                texto = Normalizer.normalize(texto, Normalizer.Form.NFD);
                texto = texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
                return texto;
            }

            @Override
            public int compare (Commerce s1, Commerce s2){
                String name1 = this.cleanString(s1.getName());
                String name2 = this.cleanString(s2.getName());
                return name1.toLowerCase().compareToIgnoreCase(name2);
            }
        });
    }

}
