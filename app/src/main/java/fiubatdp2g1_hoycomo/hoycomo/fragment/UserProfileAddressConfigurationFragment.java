package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.widget.Toast;

import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

public class UserProfileAddressConfigurationFragment extends ConfigurationFragment {

    @Override
    protected void acceptConfiguration(DeliveryAddress myAddress ) {
        HoyComoUserProfile.setMyAddress( myAddress );
        HoyComoDatabase.sendMyAddress(myAddress, HoyComoUserProfile.getUserId());

        Toast.makeText(this.getActivity(), "Se ha guardado su dirección", Toast.LENGTH_SHORT).show();
    }
}
