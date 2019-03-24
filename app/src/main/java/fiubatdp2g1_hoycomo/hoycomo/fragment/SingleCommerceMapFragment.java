package fiubatdp2g1_hoycomo.hoycomo.fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;

import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

public class SingleCommerceMapFragment extends CommercesMapFragment {


    public void updateMap(final Commerce commerce) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();
                SingleCommerceMapFragment.this.shoyMyPositionIn( SingleCommerceMapFragment.this.getMyPosition() );
                SingleCommerceMapFragment.this.showCommercePositionInMap(commerce);

                SingleCommerceMapFragment.this.centerCameraInLatLng(commerce.getLanLongPosition());

                googleMap.getUiSettings().setScrollGesturesEnabled(false);
            }
        });
    }


}
