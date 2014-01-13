package it.tapion.cupidosmsscheduler;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/*
    questo brodcast receiver viene lanciato quando il cellulare
    viene riavviato, per impostare l'AlarmManager che invia l'SMS ogni notte
 */

public class AlarmSetter extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Utility.setCronSMS(context);
    }

}
