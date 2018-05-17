package de.digisocken.offtrans;

import android.app.ListActivity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String query = "";
    private EntryCursorAdapter entryCursorAdapterDe;
    private EntryCursorAdapter entryCursorAdapterEn;
    private ListView entryList;
    private EditText searchView;
    private String lang = "de";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_de:
                    lang = "de";
                    entryList.setAdapter(entryCursorAdapterDe);
                    entryCursorAdapterDe.notifyDataSetChanged();
                    return true;
                case R.id.navigation_de_en:
                    lang = "en";
                    entryList.setAdapter(entryCursorAdapterEn);
                    entryCursorAdapterEn.notifyDataSetChanged();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                ab.setDisplayShowHomeEnabled(true);
                ab.setHomeButtonEnabled(true);
                ab.setDisplayUseLogoEnabled(true);
                ab.setLogo(R.mipmap.ic_launcher);
                ab.setTitle("  " + getString(R.string.app_name));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        searchView = (EditText) findViewById(R.id.searchView);

        getLoaderManager().initLoader(0, null, this);
        entryCursorAdapterDe = new EntryCursorAdapter(this, null, 0);

        entryList = (ListView) findViewById(R.id.dicList);
        entryList.setEmptyView(findViewById(android.R.id.empty));
        entryList.setAdapter(entryCursorAdapterDe);

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                /**
                 * @TODO !!!
                 */
                /*
                DbEntry item = (DbEntry) adapterView.getItemAtPosition(i);
                String msg = item.title + "\n\n" + item.body;
                Intent myIntent = new Intent(MainActivity.this, EditActivity.class);
                myIntent.putExtra("msg", msg);
                startActivity(myIntent);
                */
            }
        });
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPreferences.contains("initial_read")) {
            mPreferences.edit().putBoolean("initial_read", true).commit();
            new RetrieveFeedTask().execute();
        }
    }

    public void search(View view) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        query = searchView.getText().toString();
        if (!query.equals("")) {
            return new CursorLoader(
                    this,
                    EntryContentProvider.CONTENT_URI,
                    EntryContract.projection,
                    EntryContract.SELECTION_SEARCH,
                    EntryContract.searchArgs(query),
                    EntryContract.DEFAULT_SORTORDER
            );
        }
        return new CursorLoader(
                this,
                EntryContentProvider.CONTENT_URI,
                EntryContract.projection,
                EntryContract.DEFAULT_SELECTION,
                EntryContract.DEFAULT_SELECTION_ARGS,
                EntryContract.DEFAULT_SORTORDER
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (lang.equals("en")) {
            entryCursorAdapterEn.swapCursor(data);
        } else if (lang.equals("de")) {
            entryCursorAdapterDe.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        entryCursorAdapterDe.swapCursor(null);
        entryCursorAdapterEn.swapCursor(null);
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... dummy) {
            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                ContentValues values = null;
                int eventType = -1;

                InputStream ins = getResources().openRawResource(R.raw.deu_eng);
                xpp.setInput(ins, null);
                eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("orth")) {
                            values = new ContentValues();
                            values.put(EntryContract.DbEntry.COLUMN_Title, xpp.nextText());
                        }
                        if (xpp.getName().equalsIgnoreCase("quote")) {
                            values.put(EntryContract.DbEntry.COLUMN_Body, xpp.nextText());
                            // @todo de-en database
                            getContentResolver().insert(EntryContentProvider.CONTENT_URI, values);
                        }
                    }
                    eventType = xpp.next();
                }

                ins = getResources().openRawResource(R.raw.openthesaurus);
                String[] str = readTextFile(ins).split(getString(R.string.rowsplit));
                for (int i=0; i<str.length; i++) {
                    if (str[i].trim().length() == 0) continue;
                    if (str[i].startsWith(getString(R.string.ignoreline))) continue;
                    String[] line = str[i].split(getString(R.string.columnsplit));
                    str[i] = str[i].replace(line[0]+getString(R.string.columnsplit) , "");
                    values = new ContentValues();
                    values.put(EntryContract.DbEntry.COLUMN_Title, line[0]);
                    values.put(EntryContract.DbEntry.COLUMN_Body, str[i].replace(getString(R.string.columnsplit), getString(R.string.columnsplitReplace)));
                    // @todo de database
                    getContentResolver().insert(EntryContentProvider.CONTENT_URI, values);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            entryCursorAdapterDe.notifyDataSetChanged();
            //entryCursorAdapterEn.notifyDataSetChanged();
            Toast.makeText(getApplicationContext(), "import done", Toast.LENGTH_LONG).show();
        }
    }

    public String readTextFile(InputStream inputStream) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        byte buf[] = new byte[1024];
        int len;
        try {
            while ((len = inputStream.read(buf)) != -1) {
                outputStream.write(buf, 0, len);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {}
        return outputStream.toString();
    }
}
