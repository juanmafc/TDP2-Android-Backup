package fiubatdp2g1_hoycomo.hoycomo.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.Commerce;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class CommercesMapFragment extends Fragment {


    MapView mMapView;
    protected GoogleMap googleMap = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.commerce_map, container, false);

        mMapView = rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        mMapView.onResume(); // needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }


    protected void centerCameraInLatLngWithAnimation(LatLng center) {
        centerCameraInLatLngWithAnimation(center,12);
    }

    public void centerCameraInLatLngWithAnimation(LatLng center, float zoom) {
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = this.buildCameraPosition(center,zoom);
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    protected void centerCameraInLatLng(LatLng center) {
        this.centerCameraInLatLng(center, 12);
    }

    protected void centerCameraInLatLng(LatLng center, float zoom) {
        // For zooming automatically to the location of the marker
        CameraPosition cameraPosition = this.buildCameraPosition(center, zoom);
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    private CameraPosition buildCameraPosition(LatLng center, float zoom) {
        return new CameraPosition.Builder().target( center ).zoom( zoom ).build();
    }

    public void showCommercePositionInMap(Commerce commerce) {
        MarkerOptions commerceMarkerOptions = new MarkerOptions();
        commerceMarkerOptions.position(commerce.getLanLongPosition());
        commerceMarkerOptions.title(commerce.getName());
        commerceMarkerOptions.snippet(commerce.getAddress());

        Marker commerceMarker = this.googleMap.addMarker(commerceMarkerOptions);
        commerceMarker.setTag(commerce);
    }




    public LatLng getMyPosition() {

        return Utilities.getMyPosition(this.getActivity());
    }


    protected void shoyMyPositionIn(LatLng myPosition) {
        MarkerOptions myPositionMarkerOptions = new MarkerOptions();
        myPositionMarkerOptions.position( myPosition );
        myPositionMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

        Marker myPositionMarker = this.googleMap.addMarker(myPositionMarkerOptions);
        myPositionMarker.setTag(myPosition);
    }


















    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }


}
