package fiubatdp2g1_hoycomo.hoycomo.service.firebase;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ActionReceiver extends BroadcastReceiver {

    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context = context;

        //Toast.makeText(context,"recieved",Toast.LENGTH_SHORT).show();
        String action=intent.getStringExtra("action");
        if(action.equals("confirmOrderAction")){
            sendConfirmationMessage();
        }
        else if(action.equals("action2")){
            //TODO
            performAction2();

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void sendConfirmationMessage(){
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(0);
    }

    public void performAction2(){

    }

}
