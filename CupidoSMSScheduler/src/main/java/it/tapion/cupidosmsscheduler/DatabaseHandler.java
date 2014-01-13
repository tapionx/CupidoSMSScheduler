package it.tapion.cupidosmsscheduler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {


    // versione del database
    private static final int DATABASE_VERSION = 7;

    // nome del database
    private static final String DATABASE_NAME = "SMS";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
    SMS SCHEMA
    CREATE TABLE SMS
        ID INTEGER PRIMARY KEY
        SMS_TEXT TEXT
        SENT INTEGER
    */

    // creazione della tabella SMS
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE SMS (" +
                "ID INTEGER PRIMARY KEY," +
                "SMS_TEXT TEXT," +
                "SENT INTEGER)";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // upgrade del database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // elimina la vecchia tabella
        db.execSQL("DROP TABLE IF EXISTS SMS");

        // crea la tabella aggiornata
        onCreate(db);
    }

    // aggiungi nuovo sms
    void addSMS(SMS sms) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("SMS_TEXT", sms.get_text());
        values.put("SENT", sms.get_sent());

        db.insert("SMS", null, values);
        db.close();
    }

    // SELECT generica di SMS
    // restituisce null se non trova elementi
    private List<SMS> querySMS(String query) {
        List<SMS> SMSList = new ArrayList<SMS>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0){
            cursor.close();
            db.close();
            return null;
        }
        if (cursor.moveToFirst()) {
            do {
                if(cursor.isNull(0)) {
                    cursor.close();
                    db.close();
                    return null;
                }
                SMS sms = new SMS();
                sms.set_id(Integer.parseInt(cursor.getString(cursor.getColumnIndex("ID"))));
                sms.set_text(cursor.getString(cursor.getColumnIndex("SMS_TEXT")));
                sms.set_sent(Integer.parseInt(cursor.getString(cursor.getColumnIndex("SENT"))));
                SMSList.add(sms);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return SMSList;
    }

    // restituisce tutti gli SMS da inviare
    public List<SMS> getAllSMS_toSend() {
        return querySMS("SELECT * FROM SMS WHERE SENT=0 ORDER BY ID ASC");
    }

    // restituisce il primo SMS da inviare
    public SMS getSMS_toSend() {
        List<SMS> list = querySMS("SELECT MAX(ID) AS ID, SMS_TEXT, SENT FROM SMS WHERE SENT=0");
        if(list == null) {
            return null;
        } else {
            return list.get(0);
        }
    }

    // imposta un SMS come inviato
    public void setSMSSent(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE SMS SET SENT=1 WHERE ID="+ID;
        db.execSQL(query);
        db.close();
    }

    // elimina un SMS
    public void deleteSMS(int ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM SMS WHERE ID="+ID;
        db.execSQL(query);
        db.close();
    }

    // modifica il testo di un SMS
    public void editText(int ID, String newText) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE SMS SET SMS_TEXT='"+newText+"' WHERE ID="+ID;
        db.execSQL(query);
        db.close();
    }
}
