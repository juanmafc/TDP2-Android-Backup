package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public abstract class ConfigurationFragment extends Fragment implements DeliveryAddressMap.OnMapCompleteListener {



    protected EditText myAddressEditText;
    protected DeliveryAddressMap deliveryAddressMap;
    protected FragmentManager fragmentManager;
    protected Geocoder geocoder;
    protected EditText myAddressDetailsEdditText;

    protected ImageButton searchAddressImageButton;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.configuration, container, false);

        this.myAddressEditText = rootView.findViewById(R.id.my_address_edittext);

        this.searchAddressImageButton = rootView.findViewById(R.id.search_address_imagebutton);

        this.searchAddressImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myAddressName = myAddressEditText.getText().toString();
                LatLng myLatLng = ConfigurationFragment.this.searchAddress( myAddressName );
                showAddressInMap( myLatLng );
            }
        });

        this.myAddressDetailsEdditText = rootView.findViewById(R.id.configuration_details_eddittext);


        Button useMyCurrentPositionButton = rootView.findViewById(R.id.use_my_current_position_button);
        useMyCurrentPositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                useMyCurrentPosition();
            }
        });


        Button acceptConfigurationButton = rootView.findViewById(R.id.accept_configuration_button);
        acceptConfigurationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeliveryAddress deliveryAddress = null;
                String addressName = myAddressEditText.getText().toString();
                LatLng addressLatLng = deliveryAddressMap.getDeliveryLatLng();
                if ( ( !addressName.equals("") ) && (addressLatLng != null) ) {
                    deliveryAddress = new DeliveryAddress(addressName, myAddressDetailsEdditText.getText().toString(), addressLatLng);
                    acceptConfiguration( deliveryAddress );
                }
                else {
                    Toast.makeText(getActivity(), "La dirección y la posicion de entrega son obligatorios", Toast.LENGTH_SHORT).show();
                }
            }
        });





        this.fragmentManager = this.getFragmentManager();
        this.deliveryAddressMap = new DeliveryAddressMap();
        this.fragmentManager.beginTransaction().replace(R.id.configuration_map_container, this.deliveryAddressMap).commit();

        this.geocoder = new Geocoder(this.getActivity());


        if (HoyComoUserProfile.myAddressIsConfigurated()) {
            this.deliveryAddressMap.setOnCompleteListener(this);
        }


        return rootView;
    }


    private void showAddressInMap(LatLng myLatLng) {
        this.deliveryAddressMap.showDeliveryAddress(myLatLng);
    }


    private LatLng searchAddress(String address) {
        address += ", Buenos Aires, Argentina";
        Address myAddress = null;
        try {
            //TODO: add check if it is empty
            myAddress = geocoder.getFromLocationName(address, 1).get(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new LatLng(myAddress.getLatitude(), myAddress.getLongitude());

    }

    public void useMyCurrentPosition() {
        DeliveryAddress myCurrentDeliveryAddress = Utilities.getMyCurrentDeliveryAddress(this.getActivity());
        this.myAddressEditText.setText(myCurrentDeliveryAddress.getDeliveryAddressName());
        this.showAddressInMap( myCurrentDeliveryAddress.getLatLng() );
    }



    abstract protected void acceptConfiguration(DeliveryAddress myAddress );


    @Override
    public void onDeliveryAddressMapReady() {
        DeliveryAddress myAddress = HoyComoUserProfile.getMyAddress();
        this.myAddressEditText.setText( myAddress.getDeliveryAddressName() );
        this.myAddressDetailsEdditText.setText( myAddress.getDeliveryAddressDetails() );
        this.deliveryAddressMap.showDeliveryAddress(myAddress.getLatLng());
    }
}
