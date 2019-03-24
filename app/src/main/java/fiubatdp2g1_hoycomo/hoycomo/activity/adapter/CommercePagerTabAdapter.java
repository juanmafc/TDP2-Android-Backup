package fiubatdp2g1_hoycomo.hoycomo.activity.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceInformationFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.CommerceStartsFragment;
import fiubatdp2g1_hoycomo.hoycomo.fragment.SingleCommerceMapFragment;

public class CommercePagerTabAdapter extends FragmentStatePagerAdapter {

    private SingleCommerceMapFragment singleCommerceMapFragment;

    public CommercePagerTabAdapter(FragmentManager fm) {
        super(fm);
        this.singleCommerceMapFragment = new SingleCommerceMapFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }


    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                return singleCommerceMapFragment;
            case 1:
                CommerceInformationFragment tab2 = new CommerceInformationFragment();
                return tab2;
            case 2:
                CommerceStartsFragment tab3 = new CommerceStartsFragment();
                return tab3;
            default:
                return null;
        }
    }

    public void updateTab(int tabPosition) {
        if (tabPosition == 0) {
            this.singleCommerceMapFragment.updateMap( CommerceActivity.getSelectedCommerce() );
        }
    }



}