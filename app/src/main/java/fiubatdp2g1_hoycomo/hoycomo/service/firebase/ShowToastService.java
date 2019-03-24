package fiubatdp2g1_hoycomo.hoycomo.service.firebase;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.Toast;

import fiubatdp2g1_hoycomo.hoycomo.activity.CommerceActivity;
import fiubatdp2g1_hoycomo.hoycomo.activity.OrderActivity;
import fiubatdp2g1_hoycomo.hoycomo.model.HoyComoUserProfile;
import fiubatdp2g1_hoycomo.hoycomo.model.Order;
import fiubatdp2g1_hoycomo.hoycomo.service.Utilities;
import fiubatdp2g1_hoycomo.hoycomo.service.api.HoyComoDatabase;

public class ShowToastService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        String orderId = intent.getStringExtra("orderId");
        String deliveredTime = intent.getStringExtra("deliveredTime");

        if ( HoyComoUserProfile.notificationIsPending(orderId) ) {


            String action = intent.getStringExtra("action");

            if(action.equals("openMyOrderAction")){
                this.openOrderActivity( HoyComoUserProfile.getPendingOrder(orderId) );
            }
            else if(action.equals("confirmOrderAction")){
                this.openOrderActivityOnConfirmMode(HoyComoUserProfile.getPendingOrder(orderId));
            }
            else if(action.equals("denyDeliveryAction")){
                this.openOrderActivityOnRejectMode(HoyComoUserProfile.getPendingOrder(orderId));
            }
            else if (action.equals("timeoutConfirmOrderAction")){
                this.sendConfirmationMessage(orderId, deliveredTime);
            }

            //HoyComoUserProfile.removePendingOrder(orderId);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.cancel(Utilities.stringToInt(orderId));
            stopSelf();



            //This is used to close the notification tray
            Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(it);

        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void openOrderActivity(Order pendingOrder) {
        CommerceActivity.SetSelectedCommerce(pendingOrder.getCommerce());
        OrderActivity.setOrder(pendingOrder);
        Intent openMyOrderIntent = new Intent(this, OrderActivity.class);
        openMyOrderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(openMyOrderIntent);

    }

    private void openOrderActivityOnRejectMode(Order pendingOrder) {
        CommerceActivity.SetSelectedCommerce(pendingOrder.getCommerce());
        OrderActivity.setOrder(pendingOrder);

        Intent openMyOrderIntent = new Intent(this, OrderActivity.class);
        openMyOrderIntent.putExtra("mode", "reject");

        openMyOrderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(openMyOrderIntent);
    }

    private void openOrderActivityOnConfirmMode(Order pendingOrder) {
        CommerceActivity.SetSelectedCommerce(pendingOrder.getCommerce());
        OrderActivity.setOrder(pendingOrder);

        Intent openMyOrderIntent = new Intent(this, OrderActivity.class);
        openMyOrderIntent.putExtra("mode", "confirm");

        openMyOrderIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(openMyOrderIntent);
    }




    private void sendConfirmationMessage(String orderId, String deliveredTime) {
        Toast.makeText(this, "Se confirma el pedido numero " + orderId, Toast.LENGTH_SHORT).show();
        //HoyComoDatabase.postRatingForCommerce(HoyComoUserProfile.getUserId(), "6", 5, "CONFIRMAR ENTREGA. Comentario enviado desde una notificacion para testear enviar mensajes http desde acá", "1");
        //TODO: add here the time in which is was delivered
        HoyComoDatabase.changeOrderState(orderId, "delivered-confirmed", deliveredTime);
    }


}