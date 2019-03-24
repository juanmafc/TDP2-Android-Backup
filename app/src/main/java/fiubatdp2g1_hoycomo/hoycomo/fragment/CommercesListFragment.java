package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.NavigationMenuActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CommerceSearchAdapter;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CommerceSearchTabAdapter;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class CommercesListFragment extends Fragment {

    private static final String LOG_TAG = CommercesListFragment.class.getSimpleName();
    private ArrayList<Commerce> commerces = new ArrayList<>();
    private ArrayList<Commerce> originalCommerceList = new ArrayList<>();
    HashMap<String, Double> commercesAvgs = new HashMap<>();
    private CommerceSearchAdapter commercesAdapter;

    private NavigationMenuActivity myContext;
    private CommerceSearchFragment parentFragment;
    private CommerceSearchTabAdapter commercesFilteredByNameListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.comerce_list_search, container, false);
        setHasOptionsMenu(false);

        ListView listOfCommercesView = rootView.findViewById(R.id.content_commerce_search_commerces_list);

        this.commercesAdapter = new CommerceSearchAdapter(this.getActivity(), this.commerces);
        this.commercesAdapter.setCommercesFilteredByNameListener(this.commercesFilteredByNameListener);

        listOfCommercesView.setAdapter(this.commercesAdapter);

        listOfCommercesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Commerce selectedCommerce = commerces.get(position);
                Toast.makeText(CommercesListFragment.this.getActivity(), selectedCommerce.getName() + ", " + selectedCommerce.getAddress(), Toast.LENGTH_SHORT).show();

                Log.i(LOG_TAG, "CommerseSelected:" + selectedCommerce.getName());

                CommerceActivity.SetSelectedCommerce(selectedCommerce);
                Intent mIntent = new Intent(CommercesListFragment.this.getActivity(), CommerceActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });

        return rootView;
    }

    public List<Commerce> getDisplayedCommerces() {
        return this.commercesAdapter.getDisplayedCommerces();
    }



    public void initCommerces(ArrayList<Commerce> commerceList) {
        this.commerces.clear(); /* para no tener repetidos */
        for(Commerce c : commerceList ) {
            this.commerces.add(c.getNewCopy());
        }
        this.sortCommercesAlphabetically();
        this.commercesAdapter.notifyDataSetChanged();

        this.originalCommerceList.addAll(this.commerces);
    }

    private void sortCommercesAlphabetically() {
        Collections.sort(this.commerces, new Comparator<Commerce>() {

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

    public void filterByCategory(String categoryName) {
        //this.commercesAdapter.getFilterType("category").filter(categoryName);
        this.commerces.clear();
        for (Commerce currentCommerce : this.originalCommerceList ) {
            if ( currentCommerce.hasCategory(categoryName) ) {
                this.commerces.add(currentCommerce);
            }
        }
        this.sortCommercesAlphabetically();
        this.commercesAdapter.notifyDataSetChanged();
    }

    public void filterByRating(double minimumRating) {
        this.commerces.clear();
        for (Commerce currentCommerce : this.originalCommerceList ) {
            if ( minimumRating <= currentCommerce.getRating()  ) {
                this.commerces.add(currentCommerce);
            }
        }
        this.sortCommercesAlphabetically();
        this.commercesAdapter.notifyDataSetChanged();
    }


    public void filterByDistance() {
        this.commerces.clear();
        LatLng myPosition = Utilities.getMyPosition(this.getActivity());
        double currentlongitude = myPosition.longitude;
        double currentlatitude = myPosition.latitude;

        for (Commerce currentCommerce : this.originalCommerceList ) {
            float distanceFromPlace = Utilities.calculateDistanceInKm(currentlatitude, currentlongitude, currentCommerce.getLatitude(), currentCommerce.getLongitude());
            if (distanceFromPlace <= currentCommerce.getDeliveryDistanceKm()) {
                this.commerces.add(currentCommerce);
            }
        }
        this.commercesAdapter.notifyDataSetChanged();
    }



    public void filterByPriceRange(final int lowLimit, final int highLimit) {
        this.commerces.clear();
        HoyComoDatabase.getAveragesOfACommerce(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                HashMap<String, Double> commercesAvgs = new HashMap<>();
                commercesAvgs = HoyComoDatabaseParser.ParseAverageOfCommerce(response);
                for (Commerce currentCommerce : CommercesListFragment.this.originalCommerceList ) {
                    String cid = currentCommerce.getId();
                    if ( commercesAvgs.get(cid) != null && lowLimit <= commercesAvgs.get(cid) && commercesAvgs.get(cid) <= highLimit ) {
                        CommercesListFragment.this.commerces.add(currentCommerce);
                    }
                }
                CommercesListFragment.this.sortCommercesAlphabetically();
                CommercesListFragment.this.commercesAdapter.notifyDataSetChanged();
            }
        });
    }



    public Set<String> getUsedCategories() {
        HashSet<String> usedCategories = new HashSet<>();
        for (Commerce commerce : this.originalCommerceList) {
            usedCategories.addAll(commerce.getCategories());
        }
        return usedCategories;
    }

    public void setUpSearchView(SearchView upSearchView) {
        upSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                commercesAdapter.getFilterType("name").filter(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.equals("")) {
                    commercesAdapter.getFilterType("name").filter("");
                }
                return true;
            }
        });
    }

    public void setCommercesFilteredByNameListener(CommerceSearchTabAdapter commercesFilteredByNameListener) {
        this.commercesFilteredByNameListener = commercesFilteredByNameListener;
    }

    public void showOriginalList() {
        this.commerces.clear();
        this.commerces.addAll( this.originalCommerceList );
        this.commercesAdapter.notifyDataSetChanged();
    }
}



