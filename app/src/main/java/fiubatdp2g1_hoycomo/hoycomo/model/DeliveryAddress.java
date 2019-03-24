package fiubatdp2g1_hoycomo.hoycomo.model;

import com.google.android.gms.maps.model.LatLng;

public class DeliveryAddress {

    String addressName;
    String addressDetails;
    LatLng addressLatLng;

    public DeliveryAddress(String addressName, String addressDetails, LatLng addressLatLng) {
        this.addressName = addressName;
        this.addressDetails = addressDetails;
        this.addressLatLng = addressLatLng;
    }


    public LatLng getLatLng() {
        return this.addressLatLng;
    }

    public String getDeliveryAddressName() {
        return this.addressName;
    }

    public String getDeliveryAddressDetails() {
        return this.addressDetails;
    }
}
