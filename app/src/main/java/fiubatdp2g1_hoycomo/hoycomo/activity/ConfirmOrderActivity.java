package fiubatdp2g1_hoycomo.hoycomo.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.fragment.ConfirmOrderFragment;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;

public class ConfirmOrderActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_order);

        this.fragmentManager = this.getSupportFragmentManager();

        this.startConfirmOrderFragment();
    }

    private void startConfirmOrderFragment() {
        ConfirmOrderFragment confirmOrderFragment = new ConfirmOrderFragment();
        confirmOrderFragment.setDeliveryAddress(HoyComoUserProfile.getMyAddress());
        this.fragmentManager.beginTransaction()
                .replace(R.id.confirm_order_content, confirmOrderFragment) //If there is a fragment inside content_nav_menu, it is replaced with this new Fragment
                .commit(); //commit the transaction
    }










}
