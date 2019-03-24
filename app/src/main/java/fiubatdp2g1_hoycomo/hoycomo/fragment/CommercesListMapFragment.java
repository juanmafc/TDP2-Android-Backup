package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.content.Intent;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;

public class CommercesListMapFragment extends CommercesMapFragment {

    public void updateMap(final List<Commerce> commerces) {
        mMapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap mMap) {
                googleMap = mMap;
                googleMap.clear();
                CommercesListMapFragment.this.showCommercesInMap(commerces);
                LatLng myPosition = CommercesListMapFragment.this.getMyPosition();
                CommercesListMapFragment.this.shoyMyPositionIn( myPosition );
                CommercesListMapFragment.this.centerCameraInLatLngWithAnimation( myPosition );

                CommercesListMapFragment.this.setOnInfoWindowClickListenerOnMap();
            }
        });
    }

    private void setOnInfoWindowClickListenerOnMap() {
        //When the commerce title is pressed, CommerceActivity is opened with the selected Commerce
        googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                CommerceActivity.SetSelectedCommerce( (Commerce) marker.getTag() );
                Intent mIntent = new Intent(CommercesListMapFragment.this.getActivity(), CommerceActivity.class);
                mIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(mIntent);
            }
        });
    }

    private void showCommercesInMap(List<Commerce> commerces) {
        //TODO It might be useful at some point: GoogleMap.InfoWindowAdapter dummyAdapter = new GoogleMap.InfoWindowAdapter();
        for(Commerce currentCommerce : commerces) {
            this.showCommercePositionInMap(currentCommerce);
        }
    }

}
