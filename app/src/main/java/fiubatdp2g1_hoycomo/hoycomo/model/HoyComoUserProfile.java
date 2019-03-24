package fiubatdp2g1_hoycomo.hoycomo.model;

import android.app.Activity;

import java.util.HashMap;
import java.util.Map;

public class HoyComoUserProfile {

    private static String userFacebookId;
    private static String HoyComoServerToken;
    private static String firebaseToken;
    //private static Set<Integer> pendingNotifications = new HashSet<>();
    private static Map<String, Order> pendingNotifications = new HashMap<>();
    private static String userName;

    private static DeliveryAddress myAddress = null;
    private static boolean useMyCurrentPosition = true;


    public static String getHoyComoServerToken() {
        return HoyComoUserProfile.HoyComoServerToken;
    }

    public static void setHoyComoServerToken(String hoyComoServerToken) {
        HoyComoUserProfile.HoyComoServerToken = hoyComoServerToken;
    }

    public static String getUserId() {
        return HoyComoUserProfile.userFacebookId;
    }

    public static void setUserFacebookId(String userFacebookId) {
        HoyComoUserProfile.userFacebookId = userFacebookId;
    }

    public static void setFirebaseToken(String firebaseToken) {
        HoyComoUserProfile.firebaseToken = firebaseToken;
    }

    public static void addPendingNotifications(String notificationId, Order order) {
        HoyComoUserProfile.pendingNotifications.put(notificationId, order);
    }

    public static boolean notificationIsPending(String notificationId) {
        return HoyComoUserProfile.pendingNotifications.containsKey(notificationId);
    }

    public static void removePendingOrder(String notificationId) {
        if (HoyComoUserProfile.pendingNotifications.containsKey(notificationId)) {
            HoyComoUserProfile.pendingNotifications.remove(notificationId);
        }
    }

    public static void setUserName(String userName) {
        HoyComoUserProfile.userName = userName;
    }

    public static Order getPendingOrder(String orderId) {
        return HoyComoUserProfile.pendingNotifications.get(orderId);
    }

    public static void setMyAddress(DeliveryAddress myAddress) {
        HoyComoUserProfile.myAddress = myAddress;
    }

    public static DeliveryAddress getDeliveryAddress(Activity activity) {

        /*
        if ( HoyComoUserProfile.useMyCurrentPosition ) {
            return Utilities.getMyCurrentDeliveryAddress(activity);
        }
        else {
            return HoyComoUserProfile.myAddress;
        }
        */
        return HoyComoUserProfile.myAddress;
    }

    public static void useMyGivenAddress() {
        HoyComoUserProfile.useMyCurrentPosition = false;
    }

    public static void useMyCurrentAddress() {
        HoyComoUserProfile.useMyCurrentPosition = true;
    }

    public static boolean myAddressIsConfigurated() {
        return HoyComoUserProfile.myAddress != null;
    }

    public static DeliveryAddress getMyAddress() {
        return HoyComoUserProfile.myAddress;
    }

    public static boolean isUsingMyAddress() {
        return !HoyComoUserProfile.useMyCurrentPosition;
    }

    public static String getUserName() {
        return HoyComoUserProfile.userName;
    }
}
