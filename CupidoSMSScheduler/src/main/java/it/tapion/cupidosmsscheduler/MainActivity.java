package it.tapion.cupidosmsscheduler;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends ActionBarActivity {

    SharedPreferences settings = null;

    String DEBUG_TAG = "MERCURIO";

    DatabaseHandler db = new DatabaseHandler(this);

    Context context = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        settings = getSharedPreferences("it.tapion.cupidosmscheduler", MODE_PRIVATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // check if the app runs for the first time
        if(settings.getBoolean("firstrun", true)) {
            // set defaults
            settings.edit().putInt("sendhour", 1).commit();
            settings.edit().putInt("sendminute", 0).commit();
            // show the tutorial
            Log.d(DEBUG_TAG, "FIRST RUN");
            startActivity(new Intent(this, FirstRun.class));
        }

        // reload the list view
        reloadSMSList();

        // update Alarm Manager
        Utility.setCronSMS(this);
    }

    // CONTEXT MENU

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        View view = info.targetView;
        ListView parent = (ListView) view.getParent();
        final SMS selected = (SMS) parent.getItemAtPosition(info.position);
        Log.d(DEBUG_TAG, "" + selected.get_id());
        switch (item.getItemId()) {
            case R.id.context_send:
                sendNow(selected);
                return true;
            case R.id.context_edit:
                editSMS(selected);
                return true;
            case R.id.context_delete:
                deleteSMS(selected);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    // ACTION BAR

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.action_add) {
            startActivity(new Intent(this, NewSMSActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Utilities

    public void sendSMS(String text) {
        settings = getSharedPreferences("it.tapion.cupidosmscheduler", MODE_PRIVATE);
        String recipient = settings.getString("recipientphone", "error");

        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(recipient, null, text, null, null);
    }

    public void sendNow(final SMS sms) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Conferma invio")
                .setMessage("Vuoi inviare il messaggio ora?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendSMS(sms.get_text());
                        db.setSMSSent(sms.get_id());
                        reloadSMSList();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void editSMS(final SMS sms) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        input.setText(sms.get_text());
        alert.setView(input);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString().trim();
                if(value.equals("")) {
                    Toast.makeText(getApplicationContext(), "SMS vuoto", Toast.LENGTH_SHORT).show();
                    // Launch edit dialog again
                    editSMS(sms);
                } else {
                    db.editText(sms.get_id(), value);
                    reloadSMSList();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    public void deleteSMS(final SMS sms) {
        new AlertDialog.Builder(this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Conferma eliminazione")
            .setMessage("Vuoi eliminare il messaggio?")
            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    db.deleteSMS(sms.get_id());
                    reloadSMSList();
                }
            })
            .setNegativeButton("No", null)
            .show();
    }

    public void reloadSMSList() {

        TextView welcomeTextView = (TextView)findViewById(R.id.welcomeTextView);
        ListView listView = (ListView)findViewById(R.id.SMSListView);

        List<SMS> list = db.getAllSMS_toSend();

        if(list != null) {

            welcomeTextView.setVisibility(View.INVISIBLE);

            listView.setAdapter(new ArrayAdapter<SMS>(this, android.R.layout.simple_list_item_1, list));

            // abilita il menu contestuale per gli elementi della lista
            registerForContextMenu(listView);
        } else {
            // mostra il messaggio di benvenuto
            listView.setVisibility(View.INVISIBLE);
            welcomeTextView.setVisibility(View.VISIBLE);
        }
    }
}