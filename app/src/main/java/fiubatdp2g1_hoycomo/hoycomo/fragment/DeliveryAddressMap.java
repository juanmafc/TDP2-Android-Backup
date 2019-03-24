package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

//TODO: DeliveryAddressMap is not a CommercesMapFragment, but whatever
public class DeliveryAddressMap extends CommercesMapFragment {


    private LatLng deliveryLatLng;
    private OnMapCompleteListener onCompleteListener = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = super.onCreateView(inflater, container, savedInstanceState);


        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();

                configurateGoogleMapLongClickListener();
            }
        });


        if (this.onCompleteListener != null) {
            this.onCompleteListener.onDeliveryAddressMapReady();
        }

        return rootView;
    }

    private void configurateGoogleMapLongClickListener() {
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // TODO Auto-generated method stub
                googleMap.clear();
                DeliveryAddressMap.this.shoyMyPositionIn( latLng ) ;
                DeliveryAddressMap.this.centerCameraInLatLngWithAnimation( latLng, 18 );
                DeliveryAddressMap.this.deliveryLatLng = latLng;
            }
        });
    }


    public void showDeliveryAddress(final LatLng myLatLng) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();

                configurateGoogleMapLongClickListener();

                DeliveryAddressMap.this.shoyMyPositionIn( myLatLng ) ;
                DeliveryAddressMap.this.centerCameraInLatLng( myLatLng, 18 );
                deliveryLatLng = myLatLng;
            }
        });
    }






    public void showDeliveryAddressAndCommerceAddress(final LatLng myLatLng, final Commerce commerce) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();


                googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                    @Override
                    public void onMapLongClick(LatLng latLng) {
                        // TODO Auto-generated method stub
                        googleMap.clear();
                        DeliveryAddressMap.this.shoyMyPositionIn( latLng ) ;
                        DeliveryAddressMap.this.centerCameraInLatLngWithAnimation( latLng, 18 );
                        DeliveryAddressMap.this.deliveryLatLng = latLng;

                        showCommercePositionInMap(commerce);
                    }
                });





                DeliveryAddressMap.this.shoyMyPositionIn( myLatLng ) ;
                DeliveryAddressMap.this.centerCameraInLatLng( myLatLng, 18 );
                deliveryLatLng = myLatLng;

                showCommercePositionInMap(commerce);
            }
        });
    }


    public LatLng getDeliveryLatLng() {
        return this.deliveryLatLng;
    }

    public void setOnCompleteListener(OnMapCompleteListener onCompleteListener) {
        this.onCompleteListener = onCompleteListener;
    }

    public interface OnMapCompleteListener {
        public void onDeliveryAddressMapReady();
    }

}
