package it.tapion.cupidosmsscheduler;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class NewSMSActivity extends ActionBarActivity {

    String DEBUG_TAG = "MERCURIO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_sms);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.new_sms, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_save_new_sms) {
            EditText newSMSEditText = (EditText) findViewById(R.id.newSMSEditText);
            String newSMS = newSMSEditText.getText().toString().trim();

            if(newSMS.equals("")) {
                Toast.makeText(this, "SMS vuoto", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseHandler db = new DatabaseHandler(this);
                db.addSMS(new SMS(newSMS));
                Toast.makeText(this, "SMS aggiunto", Toast.LENGTH_SHORT).show();
                newSMSEditText.setText("");
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
