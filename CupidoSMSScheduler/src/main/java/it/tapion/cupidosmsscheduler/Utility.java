package it.tapion.cupidosmsscheduler;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.Toast;
import android.app.Activity;

import java.util.Calendar;

/*
    questa classe contiene metodi utilizzati da diverse activity
*/

public class Utility {

    // imposta l'Alarm Manager che si occupa di inviare l'sms all'ora stabilita
    public static void setCronSMS(Context context) {

        SharedPreferences settings = context.getSharedPreferences("it.tapion.cupidosmscheduler", context.MODE_PRIVATE);

        int hour = settings.getInt("sendhour", 2);
        int minute = settings.getInt("sendminute", 2);

        //long now = (System.currentTimeMillis() + 10000);
        long millisecondInADay = 1000 * 60 *60 * 24;
        long tenSeconds = 1000 * 10;

        Calendar now = Calendar.getInstance();
        Calendar cron = Calendar.getInstance();

        cron.set(Calendar.HOUR_OF_DAY, hour);
        cron.set(Calendar.MINUTE, minute);
        cron.set(Calendar.SECOND, 0);

        long millNow = now.getTimeInMillis();
        long millCron = cron.getTimeInMillis();

        if(millNow > millCron) {
            millCron = millCron + millisecondInADay;
        }

        Log.d("MERCURIO", "ALLARME IMPOSTATO");

        Intent myIntent = new Intent(context, SMSBrodcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, myIntent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, millCron, millisecondInADay, pendingIntent);
        
    }

    // preleva il contatto selezionato ed imposta il numero di telefono
    public static void changeRecipient(Context context, int requestCode, int resultCode, Intent data) {
        String DEBUG_TAG = "MERCURIO";
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case 1:
                    SharedPreferences settings = context.getSharedPreferences("it.tapion.cupidosmscheduler", context.MODE_PRIVATE);
                    Cursor cursor = null;
                    String phone = "";
                    String name = "";
                    try {
                        Uri result = data.getData();
                        String id = result.getLastPathSegment();
                        // query for every phone
                        cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id },
                                null);
                        int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA);
                        int phoneName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                        // let's just get the first phone
                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(phoneIdx);
                            name =  cursor.getString(phoneName);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get phone data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        if (phone.length() == 0) {
                            Toast.makeText(context, "Nessun numero di telefono.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, "Contatto selezionato: "+name, Toast.LENGTH_SHORT).show();
                            // set the tutorial off
                            settings.edit().putBoolean("firstrun", false).commit();
                            // save the contact in the application settings
                            settings.edit().putString("recipientphone", phone).commit();
                            settings.edit().putString("recipientname", name).commit();
                        }
                    }
                    break;
            }
        } else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }
}
