package it.tapion.cupidosmsscheduler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsManager;
import android.util.Log;

public class SMSBrodcastReceiver extends BroadcastReceiver {

    String DEBUG_TAG = "MERCURIO";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        SharedPreferences settings = context.getSharedPreferences("it.tapion.cupidosmscheduler", context.MODE_PRIVATE);
        DatabaseHandler db = new DatabaseHandler(context);

        String recipientPhone = settings.getString("recipientphone", "ERROR");

        SMS sms = db.getSMS_toSend();

        if(sms == null) {
            Log.d(DEBUG_TAG, "NON CI SONO MESSAGGI DA INVIARE ------");
            return;
        }

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipientPhone, null, sms.get_text(), null, null);

        db.setSMSSent(sms.get_id());

        Log.d(DEBUG_TAG, "-------------");
        Log.d(DEBUG_TAG, "INVIATO SMS");
        Log.d(DEBUG_TAG, "NUMERO "+recipientPhone);
        Log.d(DEBUG_TAG, "MESSAGGIO "+sms.get_text());
        Log.d(DEBUG_TAG, "-------------");

        // SEND NOTIFICATION
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("SMS Inviato")
                        .setContentText(sms.get_text());

        mBuilder.setContentIntent(PendingIntent.getActivity(context, 0, new Intent(), 0));

        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1, mBuilder.build());
    }
}

