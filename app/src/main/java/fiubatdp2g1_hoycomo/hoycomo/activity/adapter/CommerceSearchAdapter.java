package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.ImagesLoader;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class CommerceSearchAdapter extends BaseAdapter implements Filterable {

    protected Activity activity;
    private CommerceFilter comFilter;
    protected ArrayList<Commerce> items;
    protected ArrayList<Commerce> notFilteredItems;
    protected ArrayList<Commerce> comercesInRange = new ArrayList<Commerce>();
    protected String selectedCategory;
    protected boolean filtered = false;
    private CommerceSearchTabAdapter commercesFilteredByNameListener;
    private Map<String, Integer> discounts = new HashMap<>();


    public CommerceSearchAdapter(Activity activity, ArrayList<Commerce> items) {
        this.activity = activity;
        this.items = items;
        this.notFilteredItems = items;
        getFilter();
        HoyComoDatabase.getMaxDiscountPerCommerce(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                CommerceSearchAdapter.this.discounts = HoyComoDatabaseParser.ParseMaxDiscountPerCommerce(response);
            }
        });
    }

    @Override
    public int getCount() {
        return items.size();
    }

    public void clear() {
        items.clear();
    }

    public void addAll(ArrayList<Commerce> category) {
        items.addAll(category);
    }

    @Override
    public Object getItem(int arg0) {
        return items.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (convertView == null) {
            LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (inf != null) v = inf.inflate(R.layout.commerce_list_item_information, null);
        }

        Commerce commerce = items.get(position);
        final String commerceId = commerce.getId();
        final String userId = HoyComoUserProfile.getUserId();
        final ImageView favoriteImageLike = (ImageView) v.findViewById(R.id.commerce_list_item_information_like);
        final ImageView favoriteImageNotLike = (ImageView) v.findViewById(R.id.commerce_list_item_information_not_like);

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

        ImageView image = (ImageView) v.findViewById(R.id.commerce_list_item_information_logo);
        ImagesLoader.Load(commerce.getImageUrl(), image);

        TextView title = (TextView) v.findViewById(R.id.commerce_list_item_information_name);
        title.setText(commerce.getName());

        TextView categories = (TextView) v.findViewById(R.id.commerce_list_item_information_categories);
        categories.setText(commerce.getCategoriesString(", "));

        TextView score = v.findViewById(R.id.commerce_list_item_information_score);
        if (commerce.hasRating()){
            score.setText(commerce.getRatingAsString());
        }
        else {
            score.setText("-");
        }

        TextView distanceFromGPSToCommerceTextView = (TextView) v.findViewById(R.id.commerce_list_item_information_distance);

        distanceFromGPSToCommerceTextView.setText( commerce.getDistanceStringInKmFromMyGPS(activity) );

        TextView leadTimeTextView = (TextView) v.findViewById(R.id.lead_time_textview);
        leadTimeTextView.setText( commerce.getAverageLeadTimeString()  );

        favoriteImageNotLike.setVisibility(View.VISIBLE);
        favoriteImageLike.setVisibility(View.INVISIBLE);

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

        this.setDiscount(v, commerceId);
        return v;
    }

    private void setDiscount(View v, String commerceId) {
        ImageView promotionImage = (ImageView) v.findViewById(R.id.commerce_list_item_information_promotion_img);
        TextView promotion = (TextView) v.findViewById(R.id.commerce_list_item_information_promotion_text);

        Integer discount = this.discounts.get(commerceId);
        if (discount != null && discount != 0) {
            promotionImage.setVisibility(View.VISIBLE);
            promotion.setText(String.valueOf(discount) + "%");
        } else {
            promotionImage.setVisibility(View.INVISIBLE);
            promotion.setText("");
        }
    }

    public LatLng getMyPosition() {

        LocationManager locationManager = (LocationManager) this.activity.getSystemService(Context.LOCATION_SERVICE);
        double myLattiude = 0;
        double myLongitude = 0;
        //The parameter of getLastKnownLocation is the location provider
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

        if (location != null) {
            myLattiude = location.getLatitude();
            myLongitude = location.getLongitude();
        } else  if (location1 != null) {
            myLattiude = location.getLatitude();
            myLongitude = location.getLongitude();
        } else  if (location2 != null) {
            myLattiude = location.getLatitude();
            myLongitude = location.getLongitude();
        }else{
            Toast.makeText(this.activity,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
        }

        return new LatLng(myLattiude, myLongitude);
    }

    public void updateCommercesInRange() {
        if (!this.filtered) {
            this.filtered = true;
            ArrayList<Commerce> commerces = this.items;
            LatLng myPosition = this.getMyPosition();
            double currentlongitude = myPosition.longitude;
            double currentlatitude = myPosition.latitude;

            for (Commerce comerce : commerces) {
                float distanceFromPlace = this.calculateDistance(currentlatitude, currentlongitude, comerce.getLatitude(), comerce.getLongitude()) / 1000;
                if (distanceFromPlace <= comerce.getDeliveryDistanceKm()) {
                    this.comercesInRange.add(comerce);
                }
            }
            this.items = this.comercesInRange;
            notifyDataSetChanged();
        }
        Toast.makeText(activity.getApplicationContext(), "Cantidad " +  this.comercesInRange.size(),Toast.LENGTH_SHORT).show();
    }

    private float calculateDistance(double currentLatitude, double currentLongitude, double commerceLatitude, double commerceLongitude) {
        float[] results = new float[1];
        Location.distanceBetween(currentLatitude, currentLongitude, commerceLatitude, commerceLongitude, results);
        Log.d("DISTANCE", String.valueOf(results[0]));
        return results[0];
    }

    public List<Commerce> getDisplayedCommerces() {
        return this.items;
    }

    public Filter getFilterType(String type) {
        this.getFilter();
        this.comFilter.setTypeOfFilter(type);
        return this.comFilter;
    }

    @Override
    public Filter getFilter() {
        if (comFilter == null) {
            comFilter = new CommerceFilter();
        }
        return comFilter;
    }

    public void setCommercesFilteredByNameListener(CommerceSearchTabAdapter commercesFilteredByNameListener) {
        this.commercesFilteredByNameListener = commercesFilteredByNameListener;
    }

    public class CommerceFilter extends Filter {

        private String typeOfFilter;
        private ArrayList<Commerce> resultsFilteredByName;

        public void setTypeOfFilter(String typeOfFilter){
            this.typeOfFilter = typeOfFilter;
        }

        private FilterResults filterByName(FilterResults filterResults, CharSequence searchText) {
            if (searchText!=null && searchText.length()>0) {
                ArrayList<Commerce> tempList = new ArrayList<Commerce>();
                for (Commerce commerce : items) {
                    if (commerce.getName().toLowerCase().contains(searchText.toString().toLowerCase())) {
                        tempList.add(commerce);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }
            return filterResults;
        }


        private FilterResults filterByCategory(FilterResults filterResults, CharSequence searchText) {
            if (searchText!=null && searchText.length()>0) {
                ArrayList<Commerce> tempList = new ArrayList<Commerce>();
                for (Commerce commerce : items) {
                    if (commerce.haveCategory(searchText.toString())) {
                        tempList.add(commerce);
                    }
                }
                filterResults.count = tempList.size();
                filterResults.values = tempList;
            }
            return filterResults;
        }

        @Override
        protected FilterResults performFiltering(CharSequence queryText) {
            FilterResults filterResults = new FilterResults();
            filterResults.count = 0;
            filterResults.values = items;
            switch (this.typeOfFilter) {
                case "name":
                    filterResults = this.filterByName(filterResults, queryText);
                    break;
                case "category":
                    filterResults = this.filterByCategory(filterResults, queryText);
                    break;
                default:
                    filterResults = this.filterByName(filterResults, queryText);
                    break;
            }
            this.resultsFilteredByName = (ArrayList<Commerce>) filterResults.values;
            return filterResults;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0) {
                items = notFilteredItems;
            }
            else {
                items = (ArrayList<Commerce>) results.values;
            }
            notifyDataSetChanged();
            commercesFilteredByNameListener.updateCommercesByName();
        }

        public ArrayList<Commerce> getFilteredResults() {
            return this.resultsFilteredByName;
        }
    }
}
