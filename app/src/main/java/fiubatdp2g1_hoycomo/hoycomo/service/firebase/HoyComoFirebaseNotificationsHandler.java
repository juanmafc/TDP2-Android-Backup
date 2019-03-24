package fiubatdp2g1_hoycomo.hoycomo.service.firebase;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import fiubatdp2g1_hoycomo.hoycomo.R;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabaseParser;

public class HoyComoFirebaseNotificationsHandler extends FirebaseMessagingService {




    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.e("FIREBASE_NOTIFICATION", remoteMessage.getFrom());


        Map<String, String> data = remoteMessage.getData();
        if (remoteMessage.getData().size() > 0) {
            Log.e("FIREBASE", remoteMessage.getData().toString());
            try {
                JSONObject orderJsonObject = new JSONObject(data.get("order"));
                JSONObject messageJsonObject = new JSONObject(data.get("message"));
                Order order = HoyComoDatabaseParser.ParseOrder(orderJsonObject.getJSONObject("order"));
                this.createNotification(order, messageJsonObject.getString("message"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void createNotification(Order order, String message) {

        String notificationId = order.getId();
        String deliveredTime = order.getDeliveredTime();

        String channelId = "fiubatdp2g1Hoycomo";

        Intent openMyOrderIntent = new Intent(this, ShowToastService.class);
        openMyOrderIntent.putExtra("action","openMyOrderAction");
        openMyOrderIntent.putExtra("orderId",notificationId);
        PendingIntent openMyOrderPendingIntent = PendingIntent.getService(this, 0, openMyOrderIntent, PendingIntent.FLAG_ONE_SHOT);


        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Hoy Como")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setVibrate(new long[]{0, 300, 300, 300})
                .setLights(Color.WHITE, 1000, 5000)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(openMyOrderPendingIntent);
                //.setDeleteIntent(sendConfirmationPendingIntent);

        if (order.getState().equals("delivered-needs-confirmation")) {

            Intent denyDeliveryIntent = new Intent(this, ShowToastService.class);
            denyDeliveryIntent.putExtra("action","denyDeliveryAction");
            denyDeliveryIntent.putExtra("orderId",notificationId);
            denyDeliveryIntent.putExtra("deliveredTime", deliveredTime);
            PendingIntent denyDeliveryPendingIntent = PendingIntent.getService(this, 1, denyDeliveryIntent, PendingIntent.FLAG_ONE_SHOT);


            Intent sendConfirmationIntent = new Intent(this, ShowToastService.class);
            sendConfirmationIntent.putExtra("action","confirmOrderAction");
            sendConfirmationIntent.putExtra("orderId",notificationId);
            sendConfirmationIntent.putExtra("deliveredTime", deliveredTime);
            PendingIntent sendConfirmationPendingIntent = PendingIntent.getService(this, 2, sendConfirmationIntent, PendingIntent.FLAG_ONE_SHOT);

            notificationBuilder.addAction(R.mipmap.ic_launcher, "Rechazar", denyDeliveryPendingIntent)
                                .addAction(R.mipmap.ic_launcher, "Confirmar", sendConfirmationPendingIntent);

            Intent confirmDeliveryByTimeout = new Intent(this, ShowToastService.class);
            confirmDeliveryByTimeout.putExtra("action","timeoutConfirmOrderAction");
            confirmDeliveryByTimeout.putExtra("orderId",notificationId);
            confirmDeliveryByTimeout.putExtra("deliveredTime", deliveredTime);
            PendingIntent confirmDeliveryByTimeoutPendingIntent = PendingIntent.getService(this, 3, confirmDeliveryByTimeout, PendingIntent.FLAG_ONE_SHOT);

            int secondsToAutomaticallyConfirmOrder = 60*15;
            AlarmManager confirmOrderAlarm = (AlarmManager)getSystemService(ALARM_SERVICE);
            confirmOrderAlarm.set(AlarmManager.RTC,System.currentTimeMillis() + secondsToAutomaticallyConfirmOrder * 1000, confirmDeliveryByTimeoutPendingIntent);
        }

        HoyComoUserProfile.addPendingNotifications(notificationId, order);




        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(Utilities.stringToInt(notificationId), notificationBuilder.build());

    }

}
