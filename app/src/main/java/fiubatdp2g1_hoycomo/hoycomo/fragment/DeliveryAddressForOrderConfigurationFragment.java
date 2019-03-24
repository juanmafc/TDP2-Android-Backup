package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.support.v4.app.FragmentManager;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;

public class DeliveryAddressForOrderConfigurationFragment extends ConfigurationFragment{

    @Override
    protected void acceptConfiguration(DeliveryAddress myAddress ) {

        FragmentManager fragmentManager = getFragmentManager();
        ConfirmOrderFragment confirmOrderFragment  = new ConfirmOrderFragment();
        confirmOrderFragment.setDeliveryAddress(myAddress);

        //It pops itself
        fragmentManager.popBackStack();

        fragmentManager.beginTransaction()
                .replace(R.id.confirm_order_content, confirmOrderFragment) //If there is a fragment inside content_nav_menu, it is replaced with this new Fragment
                //.addToBackStack(null) //So the user can use the back button
                .commit(); //commit the transaction

    }


}
