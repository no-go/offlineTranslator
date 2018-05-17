package de.digisocken.offtrans;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class EntryCursorAdapter extends CursorAdapter {
    Activity activity;

    public EntryCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        activity = (Activity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.entry_line, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) { ;
        TextView tt = (TextView) view.findViewById(R.id.line_title);
        TextView tb = (TextView) view.findViewById(R.id.line_body);
        String title = cursor.getString(cursor.getColumnIndexOrThrow(EntryContract.DbEntry.COLUMN_Title));
        String body = cursor.getString(cursor.getColumnIndexOrThrow(EntryContract.DbEntry.COLUMN_Body));
        if (MainActivity.query != null) {
            tt.setText(highlight(MainActivity.query, title));
            tb.setText(highlight(MainActivity.query, body));
        } else {
            tt.setText(title);
            tb.setText(body);
        }
        if (cursor.getPosition()%2==0) {
            view.setBackgroundColor(ContextCompat.getColor(
                    activity.getApplicationContext(),
                    R.color.evenCol
            ));
        } else {
            view.setBackgroundColor(ContextCompat.getColor(
                    activity.getApplicationContext(),
                    R.color.oddCol
            ));
        }
    }

    public Spanned highlight(String key, String msg) {
        msg = msg.replaceAll(
                "((?i)"+key+")",
                "<b><font color='"
                        + ContextCompat.getColor(activity.getApplicationContext(),
                        R.color.colorAccent) +
                        "'>$1</font></b>"
        );
        return fromHtml(msg);
    }

    static public Spanned fromHtml(String str) {
        Spanned sp;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sp = Html.fromHtml(str, Html.FROM_HTML_MODE_COMPACT, null, null);
        } else {
            sp = Html.fromHtml(str, null, null);
        }
        return sp;
    }
}