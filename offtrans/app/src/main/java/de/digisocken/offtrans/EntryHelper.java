package de.digisocken.offtrans;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Diese Klasse stellt die Verbindung zur Datenbank her.
 */
public class EntryHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "dataentries.db";
    private static final int DATABASE_VERSION = 1;

    public EntryHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(EntryContract.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.w(
                EntryHelper.class.getName(),
                "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data"
        );
        sqLiteDatabase.execSQL(EntryContract.SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
