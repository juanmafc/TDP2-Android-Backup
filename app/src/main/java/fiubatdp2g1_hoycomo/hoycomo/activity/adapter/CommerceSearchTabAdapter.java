package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.Set;

import fiubatdp2g1_hoycomo.hoycomo.fragment.CommercesListFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommercesListMapFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

public class CommerceSearchTabAdapter extends FragmentStatePagerAdapter {

    int mNumOfTabs;
    private CommercesListFragment commerceListFragment;
    private CommercesListMapFragment commercesListMapFragment;

    public CommerceSearchTabAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.commerceListFragment = new CommercesListFragment();

        this.commerceListFragment.setCommercesFilteredByNameListener(this);

        this.commercesListMapFragment = new CommercesListMapFragment();
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return this.commerceListFragment;
            case 1:
                return this.commercesListMapFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }

    public void updateTab(int tabPosition) {
        if (tabPosition == 1) {
            this.updateMap();
        }
    }

    public void updateCommerces(int tabPosition, ArrayList<Commerce> commerceList) {
        this.commerceListFragment.initCommerces(commerceList);
        if (tabPosition == 1) {
            this.updateMap();
        }
    }

    public void filterByCategoryAndUpdateFragments(String categoryName)  {
        this.commerceListFragment.filterByCategory(categoryName);
        this.updateMap();
    }

    public void updateCommercesByRating(double minimumRating) {
        this.commerceListFragment.filterByRating(minimumRating);

    }

    public void updateCommercesByPriceRange(int lowLimit, int highLimit) {
        this.commerceListFragment.filterByPriceRange(lowLimit, highLimit);
    }

    public void updateCommercesNearBy(int tabPosition) {
        this.commerceListFragment.filterByDistance();
        if ( tabPosition == 1) {
            this.updateMap();
        }
    }

    private void updateMap() {
        this.commercesListMapFragment.updateMap( this.commerceListFragment.getDisplayedCommerces() );
    }

    public Set<String> getUsedCategories() {
        return this.commerceListFragment.getUsedCategories();
    }

    public void updateCommercesByName() {
        //this.commercesListMapFragment.updateMap( filteredResults );
        this.updateMap();
    }

    public void showOriginalList() {
        this.commerceListFragment.showOriginalList();
        this.updateMap();
    }
}
