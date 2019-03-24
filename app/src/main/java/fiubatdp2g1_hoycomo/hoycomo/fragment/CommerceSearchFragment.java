package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.activity.CategorySearchActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.PriceRangeSearchActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.RatingsSearchActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.adapter.CommerceSearchTabAdapter;
import fiubatdp2g1_hoycomo.hoycomo.interfaces.CommercesListLoadingListener;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.model.LeadTimeStatistics;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;
import toan.android.floatingactionmenu.FloatingActionButton;
import toan.android.floatingactionmenu.FloatingActionsMenu;

public class CommerceSearchFragment extends Fragment implements CommercesListLoadingListener {

    private static final String LOG_TAG = CommerceSearchFragment.class.getSimpleName();

    ArrayList<Commerce> commercesList = new ArrayList<>();

    private View rootView;
    private FragmentActivity myContext;
    private CommerceSearchTabAdapter commerceSearchTabAdapter;
    private ViewPager viewPager;

    private SearchView searchView;
    private TabLayout tabLayout;
    private Map<Integer, LeadTimeStatistics> leadTimeMap = new HashMap<>();
    private boolean filterIsApplied;


    @Override
    public void onAttach(Activity activity) {
        this.myContext=(FragmentActivity) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rootView = inflater.inflate(R.layout.content_commerce_search, container, false);
        setHasOptionsMenu(true);


        this.tabLayout = (TabLayout) rootView.findViewById(R.id.commerce_search_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Restaurants"));
        tabLayout.addTab(tabLayout.newTab().setText("Mapa"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);

        this.viewPager = (ViewPager) rootView.findViewById(R.id.commerce_search_pager);
        FragmentManager fragManager = this.myContext.getSupportFragmentManager(); //If using fragments from support v4

        this.commerceSearchTabAdapter = new CommerceSearchTabAdapter(fragManager, tabLayout.getTabCount());
        this.viewPager.setAdapter(this.commerceSearchTabAdapter);
        this.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));


        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();
                viewPager.setCurrentItem(tabPosition);
                CommerceSearchFragment.this.commerceSearchTabAdapter.updateTab(tabPosition);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        this.initFloatingActionButtonMenu();

        HoyComoDatabase.getOrders(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray ordersJsonArray = response.getJSONArray("orders");
                    for (int i = 0; i < ordersJsonArray.length(); i++ ){
                        JSONObject orderJsonObject = ordersJsonArray.getJSONObject(i);
                        if ( orderJsonObject.getString("state").equals("delivered-confirmed") ) {
                            int commerceId = orderJsonObject.getInt("restaurant_id");
                            String orderCreationTime = orderJsonObject.getString("time");
                            String deliveryTime = orderJsonObject.getString("update_time");
                            long leadTime = Utilities.getTimeDifferencesInMinutes(deliveryTime, orderCreationTime);

                            if ( !CommerceSearchFragment.this.leadTimeMap.containsKey(commerceId) ) {
                                CommerceSearchFragment.this.leadTimeMap.put(commerceId, new LeadTimeStatistics());
                            }
                            LeadTimeStatistics leadTimeStatisticsForCurrentCommerce = CommerceSearchFragment.this.leadTimeMap.get(commerceId);
                            leadTimeStatisticsForCurrentCommerce.accummulatedLeadTime += leadTime;
                            leadTimeStatisticsForCurrentCommerce.completedOrders++;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                HoyComoDatabase.getCommercesList( CommerceSearchFragment.this );
            }
        });


        return rootView;
    }

    public void updateCommercesByRating(double minimumRating) {
        this.commerceSearchTabAdapter.updateCommercesByRating(minimumRating);
    }

    public void updateCommercesByPriceRange(int lowLimit, int highLimit) {
        this.commerceSearchTabAdapter.updateCommercesByPriceRange(lowLimit, highLimit);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.navigation_menu, menu);
//        inflater.inflate(R.menu.navigation_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        this.searchView = (SearchView) searchItem.getActionView();
        this.searchView.setSubmitButtonEnabled(true);
        this.searchView.setQueryHint("Bucar Comercio");

        CommercesListFragment listaDeComercios = (CommercesListFragment) this.commerceSearchTabAdapter.getItem(0);
        listaDeComercios.setUpSearchView(this.searchView);
    }



    public void updateCommercesNearMyLocation() {
        this.commerceSearchTabAdapter.updateCommercesNearBy(this.viewPager.getCurrentItem());
    }




    @Override
    public void updateCommercesList(ArrayList<Commerce> commercesList) {

        for ( Commerce  commerce: commercesList ) {
            int commerceId = Utilities.stringToInt(commerce.getId());
            if (this.leadTimeMap.containsKey(commerceId)){
                commerce.setAverageLeadTime(  this.leadTimeMap.get(commerceId).getAverageLeadTimeString()   );
            }
            else {
                commerce.setAverageLeadTime(null);
            }
        }

        this.commercesList.addAll(commercesList);
        this.commerceSearchTabAdapter.updateCommerces(this.viewPager.getCurrentItem(), this.commercesList);
    }


    public ArrayList<Commerce> getCommercesList() {
        return this.commercesList;
    }

    protected void initFloatingActionButtonMenu() {
        final FloatingActionsMenu floatingActionsMenu = (FloatingActionsMenu) rootView.findViewById(R.id.multiple_actions_menu);


        final FloatingActionButton actionPriceRange = (FloatingActionButton) rootView.findViewById(R.id.filter_action_price_range);
        actionPriceRange.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), PriceRangeSearchActivity.class);
                PriceRangeSearchActivity.comerceSearch = CommerceSearchFragment.this;
                startActivity(myIntent);
                filterIsApplied = true;
                floatingActionsMenu.collapse();
            }
        });

        final FloatingActionButton actionRaiting = (FloatingActionButton) rootView.findViewById(R.id.filter_action_rating);
        actionRaiting.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), RatingsSearchActivity.class);
                RatingsSearchActivity.comerceSearch = CommerceSearchFragment.this;
                startActivity(myIntent);
                filterIsApplied = true;
                floatingActionsMenu.collapse();
            }
        });

        final FloatingActionButton actionDistance = (FloatingActionButton) rootView.findViewById(R.id.filter_action_distance);
        actionDistance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                CommerceSearchFragment.this.updateCommercesNearMyLocation();
                filterIsApplied = true;
                floatingActionsMenu.collapse();
            }
        });

        final FloatingActionButton actionCategory = (FloatingActionButton) rootView.findViewById(R.id.filter_action_category);
        actionCategory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(getActivity(), CategorySearchActivity.class);
                CategorySearchActivity.comerceSearchAdapter = CommerceSearchFragment.this.commerceSearchTabAdapter;
                startActivity(myIntent);
                filterIsApplied = true;
                floatingActionsMenu.collapse();
            }
        });

        final View backgroundOpac = rootView.findViewById(R.id.floating_menu_background);
        backgroundOpac.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                floatingActionsMenu.collapse();
            }
        });

        floatingActionsMenu.setOnFloatingActionsMenuUpdateListener(new FloatingActionsMenu.OnFloatingActionsMenuUpdateListener() {

            @Override
            public void onMenuExpanded() {
                backgroundOpac.setVisibility(View.VISIBLE);
            }

            @Override
            public void onMenuCollapsed() {
                backgroundOpac.setVisibility(View.GONE);
            }
        });

    }

    public boolean filterIsApllied() {
        return this.filterIsApplied;
    }

    public void removeFilter() {
        this.filterIsApplied = false;
        this.commerceSearchTabAdapter.showOriginalList();
    }
}
