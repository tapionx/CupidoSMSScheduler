package it.tapion.cupidosmsscheduler;

import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;

public class FirstRun extends ActionBarActivity {

    String DEBUG_TAG = "MERCURIO";

    SharedPreferences settings = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_run);
    }

    // seleziona un contatto dalla rubrica
    public void pickContact(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Utility.changeRecipient(this, requestCode, resultCode, data);
        finish();
    }


}
