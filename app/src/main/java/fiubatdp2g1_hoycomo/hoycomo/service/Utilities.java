package fiubatdp2g1_hoycomo.hoycomo.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import fiubatdp2g1_hoycomo.hoycomo.model.DeliveryAddress;

public class Utilities {

    public static String doubleToStringWithTwoDigits(double number) {
        NumberFormat nf = new DecimalFormat("##.##");
        return nf.format(number);
    }

    public static String kmToString(double kilometers) {
        NumberFormat nf = new DecimalFormat("##.#");
        String kmString = nf.format(kilometers);
        if (kmString.equals("0.0")){
            kmString = "0";
        }
        return kmString;
    }

    public static int  stringToInt(String restaurantId) {
        return Integer.valueOf(restaurantId);
    }

    public static String getCurrentDateAndTime() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String currentDate = df.format(c);
        return currentDate;
    }



    public static float calculateDistanceInKm(double latitude1, double longitude1, double latitude2, double longitude2) {
        float[] results = new float[1];
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results);
        return ( results[0] / 1000 );
    }


    public static LatLng getMyPosition(Activity activity) {
        final int REQUEST_LOCATION = 1;
        LocationManager locationManager = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        double myLattiude = 0;
        double myLongitude = 0;
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //The parameter of getLastKnownLocation is the location provider
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                myLattiude = location.getLatitude();
                myLongitude = location.getLongitude();
            } else  if (location1 != null) {
                myLattiude = location.getLatitude();
                myLongitude = location.getLongitude();
            } else  if (location2 != null) {
                myLattiude = location.getLatitude();
                myLongitude = location.getLongitude();
            }else{
                Toast.makeText(activity,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
            }
        }

        return new LatLng(myLattiude, myLongitude);
    }

    public static DeliveryAddress getMyCurrentDeliveryAddress(Activity activity) {
        LatLng myCurrentLatLng = Utilities.getMyPosition(activity);
        String currentAddress = "HARDCODED DIRECCION";
        try {
            Geocoder geocoder = new Geocoder(activity);
            List<Address> addresses = geocoder.getFromLocation(myCurrentLatLng.latitude, myCurrentLatLng.longitude, 1);
            Address tmpAddress = addresses.get(0);
            currentAddress = tmpAddress.getThoroughfare() + " " + tmpAddress.getFeatureName();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO: handle non existance address
        return new DeliveryAddress(currentAddress, "", myCurrentLatLng);

    }

    public static long getTimeDifferencesInMinutes(String firstTime, String secondTime) {

        long timeDifferenceInMinutes = 0;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date firstDate = format.parse(firstTime);
            Date secondDate = format.parse(secondTime);

            long millis = firstDate.getTime() - secondDate.getTime();
            timeDifferenceInMinutes = millis/(1000*60);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeDifferenceInMinutes;
    }

    public static boolean dateHasPassed(String dateText) {
        try {
            Date date = new SimpleDateFormat("MM/yy").parse(dateText);


            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 1);

            date = calendar.getTime();
            return ( date.before(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }



    //To see a printable HH:mm
    /*
    int hours = millis/(1000 * 60 * 60);
    int mins = (mills/(1000*60)) % 60;
     */
}
