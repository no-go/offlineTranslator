package de.digisocken.offtrans;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EntryContract {

    public static final String AUTHORITY = "de.digisocken.offtrans.contentprovider";

    public static class DbEntry implements BaseColumns {
        public static final String TABLE_NAME = "entries";

        public static final String COLUMN_Title = "e_title";
        public static final String COLUMN_Body = "e_body";
        public static final String COLUMN_Hint = "e_hint";
    }

    // Useful SQL query parts
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    public static final String DEFAULT_SELECTION = DbEntry.COLUMN_Hint +"=?";
    public static final String[] DEFAULT_SELECTION_ARGS = {"de"};
    public static final String DEFAULT_SORTORDER = DbEntry.COLUMN_Title +" ASC";

    // Useful SQL queries
    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + DbEntry.TABLE_NAME + " (" +
                    DbEntry._ID + INTEGER_TYPE + " PRIMARY KEY" + COMMA_SEP +
                    DbEntry.COLUMN_Title + TEXT_TYPE + COMMA_SEP +
                    DbEntry.COLUMN_Body + TEXT_TYPE + COMMA_SEP +
                    DbEntry.COLUMN_Hint + TEXT_TYPE + " )";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + DbEntry.TABLE_NAME;

    public static final String[] projection = {
            DbEntry._ID,
            DbEntry.COLUMN_Title,
            DbEntry.COLUMN_Body,
            DbEntry.COLUMN_Hint
    };

    public static final String SELECTION_SEARCH =
            "(" + DbEntry.COLUMN_Title + " LIKE ? OR " +
                    DbEntry.COLUMN_Body + " LIKE ? ) AND ("+
            DbEntry.COLUMN_Hint + " = ? )";

    public static String[] searchArgs(String query, String hint) {
        return new String[]{"%"+query+"%", "%"+query+"%", hint};
    }
}
