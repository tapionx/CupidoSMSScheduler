package it.tapion.cupidosmsscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class SettingsActivity extends ActionBarActivity {

    String DEBUG_TAG = "MERCURIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SharedPreferences settings = getSharedPreferences("it.tapion.cupidosmscheduler", MODE_PRIVATE);

        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        timePicker.setCurrentHour(settings.getInt("sendhour", 2));
        timePicker.setCurrentMinute(settings.getInt("sendminute", 2));

        TextView recipientName = (TextView) findViewById(R.id.recipientNameTextView);
        TextView recipientPhone = (TextView) findViewById(R.id.recipientPhoneTextView);

        recipientName.setText(settings.getString("recipientname", "Nessuno"));
        recipientPhone.setText(settings.getString("recipientphone", ""));
    }

    public void changeSendTime(View view) {
        TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);
        int hour = timePicker.getCurrentHour();
        int minute = timePicker.getCurrentMinute();

        SharedPreferences settings = getSharedPreferences("it.tapion.cupidosmscheduler", MODE_PRIVATE);

        settings.edit().putInt("sendhour", hour).commit();
        settings.edit().putInt("sendminute", minute).commit();
        Log.d(DEBUG_TAG, "Ora di invio modificata "+hour+" "+minute);
        Toast.makeText(this, "Ora modificata", Toast.LENGTH_SHORT).show();
    }

    public void changeRecipientButton(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utility.changeRecipient(this, requestCode, resultCode, data);
        finish();
    }
}
