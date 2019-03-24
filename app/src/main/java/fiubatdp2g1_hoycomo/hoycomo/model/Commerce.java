package fiubatdp2g1_hoycomo.hoycomo.model;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;

public class Commerce {

    private final double latitude;
    private final double longitude;
    private String id;
    private String name;
    private String address;
    private ArrayList<String> categories;
    private ArrayList<String> dishCategories;
    private String telephone;
    private int deliveryDistanceKm;
    //private Drawable image;
    private ArrayList<OpenDay> openDays;
    private String imageUrl;
    private Double rating;
    static final int SECONDSINADAY = 86400;
    private String averageLeadTime;

    public Commerce(String id, String name, String streetAddress, double latitude, double longitude, String telephone, int deliveryDistanceKm, Double stars) {
        this.id = id;
        this.name = name;
        this.address = streetAddress;
        this.latitude = latitude;
        this.longitude = longitude;
        this.telephone = telephone;
        this.deliveryDistanceKm = deliveryDistanceKm;
        this.rating = stars;
    }

    public String getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public String getTelephone() {
        return this.telephone;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public ArrayList<String> getCategories() {
        return this.categories;
    }

    public String getCategoriesString(String delimiter) {
        String listString = "";
        return android.text.TextUtils.join(delimiter, this.categories);

    }

    public ArrayList<OpenDay> getOpenDays() {
        return this.openDays;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }

    public void setImageUrl(String url) {
        this.imageUrl = url;
    }

    public void setOpenDays(ArrayList<OpenDay> openDays) {
        this.openDays = openDays;
    }

    public LatLng getLanLongPosition() {
        return new LatLng(this.latitude, this.longitude);
    }

    public String getOpenHours(String dayOfTheWeek) {
        boolean isClose = true;
        StringBuilder text = new StringBuilder(dayOfTheWeek);
        for(OpenDay day : this.openDays) {
            if (daysAreEqual(day, dayOfTheWeek)) {
                text.append(": ").append(day.getFrom()).append(" hs - ").append(day.getTo()).append(" hs");
                isClose = false;
                break;
            }
        }
        if (isClose) text.append(": Cerrado");
        return text.toString();
    }

    private boolean daysAreEqual(OpenDay day, String dayOfTheWeek) {
        return (day.getDay().toLowerCase()).equals(dayOfTheWeek.toLowerCase());
    }


    private int timeInSeconds(String time) {
        String[] units = time.split(":");
        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        return 60*(60*hours+minutes);
    }

    public boolean isClosed(String dayOfTheWeek, String currentHourAndMinutes) {
        boolean isClosed = true;
        int current = this.timeInSeconds(currentHourAndMinutes);
        for (OpenDay day : this.openDays) {
            if (daysAreEqual(day, dayOfTheWeek)) {
                int from = this.timeInSeconds(day.getFrom());
                int to = this.timeInSeconds(day.getTo());
                if ( from < to ) {
                    if (from <= current && current < to) {
                        isClosed = false;
                        break;
                    }
                } else {
                    if (from <= current && current < (to + SECONDSINADAY)) {
                        isClosed = false;
                        break;
                    }
                }
            }
        }
        return isClosed;
    }

    public void setDeliveryDistanceKm(int deliveryDistanceKm) {
        this.deliveryDistanceKm = deliveryDistanceKm;
    }

    public int getDeliveryDistanceKm() {
        return deliveryDistanceKm;
    }

    public void setDishCategories(ArrayList<String> dishCategories) {
        this.dishCategories = dishCategories;
    }

    public ArrayList<String> getDishCategories() {
        return dishCategories;
    }


    public String getImageUrl() {
        return this.imageUrl;
    }

    public boolean haveCategory(String categoryName) {
        for (String cat: this.categories) {
            if (cat.equals(categoryName)) {
                return true;
            }
        }
        return false;
    }

    public Commerce getNewCopy(){
        Commerce copy= new Commerce(this.id, this.name, this.address, this.latitude, this.longitude, this.telephone, this.deliveryDistanceKm, this.rating);
        copy.setCategories(this.categories);
        copy.setDishCategories(this.dishCategories);
        copy.setOpenDays(this.openDays);
        copy.setImageUrl(this.imageUrl);
        copy.setAverageLeadTime(this.averageLeadTime);
        return copy;
    }

    public boolean hasCategory(String categoria) {
        return this.categories.contains(categoria);
    }


    public double getRating() {
        if (!this.hasRating()) {
            return -1;
        }
        return this.rating;
    }

    public String getRatingAsString() {
        if (!this.hasRating()) {
            return "XXX";
        }
        return Utilities.doubleToStringWithTwoDigits( this.rating );
    }

    public boolean hasRating() {
        return this.rating != null;
    }


    public void setAverageLeadTime(String averageLeadTime) {
        this.averageLeadTime = averageLeadTime;
    }

    public String getAverageLeadTimeString() {
        if (this.averageLeadTime == null) {
            return  "-";
        }
        else {
            int averageLeadTimeNumber = Utilities.stringToInt(averageLeadTime);
            if (averageLeadTimeNumber > 60) {
                return "60+ min";
            }
            else {
                return this.averageLeadTime + " min";
            }

        }
    }

    public String getDistanceStringInKmFromMyGPS(Activity activity) {
        LatLng GPSPosition = Utilities.getMyPosition(activity);
        double distanceInKm = Utilities.calculateDistanceInKm(GPSPosition.latitude, GPSPosition.longitude, this.getLatitude(), this.getLongitude());
        return Utilities.kmToString(distanceInKm) + " Km";
    }
}

