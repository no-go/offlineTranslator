package de.digisocken.offtrans;

import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.ximpleware.AutoPilot;
import com.ximpleware.NavException;
import com.ximpleware.ParseException;
import com.ximpleware.PilotException;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XPathEvalException;
import com.ximpleware.XPathParseException;

import org.apache.commons.io.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String query = "";
    private SharedPreferences mPreferences;
    private EntryCursorAdapter entryCursorAdapter;
    private ListView entryList;
    private EditText searchView;
    private LinearLayout hintview;
    public static String lang = "de";
    private long inserts = 0;
    private float maxEntries = 37583+52996+81621+20000;

    private Handler handler = new Handler();

    private final Runnable updateHintThread = new Runnable() {
        public void run() {
            try {
                ActionBar ab = getSupportActionBar();
                if (ab != null && inserts >= 0) {
                    String dots = "";
                    if (inserts % 100 == 0) dots = "...";

                    ab.setTitle(String.format(Locale.GERMAN,
                            "  %s - import: %.0f%% %s",
                            getString(R.string.app_name),
                            (100*(float)inserts/maxEntries),
                            dots
                    ));
                    handler.postDelayed(this, 500);
                } else {
                    ab.setTitle("  " + getString(R.string.app_name));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_deu:
                    lang = "de";
                    search(searchView);
                    return true;
                case R.id.navigation_eng:
                    lang = "deu_eng";
                    search(searchView);
                    return true;
                case R.id.navigation_ara:
                    lang = "ara_eng";
                    search(searchView);
                    return true;
                case R.id.navigation_kur:
                    lang = "kur_deu";
                    search(searchView);
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
                ab.setElevation(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        searchView = (EditText) findViewById(R.id.searchView);
        hintview = (LinearLayout) findViewById(R.id.hintview);

        getLoaderManager().initLoader(0, null, this);
        entryList = (ListView) findViewById(R.id.dicList);
        entryCursorAdapter = new EntryCursorAdapter(this, null, 0);
        entryList.setAdapter(entryCursorAdapter);


        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor c = (Cursor) entryCursorAdapter.getItem(position);
                String title = c.getString(c.getColumnIndex(EntryContract.DbEntry.COLUMN_Title));
                String body = c.getString(c.getColumnIndex(EntryContract.DbEntry.COLUMN_Body));

                String msg = title + "\n\n" + body;
                Intent myIntent = new Intent(MainActivity.this, EditActivity.class);
                myIntent.putExtra("msg", msg);
                startActivity(myIntent);
            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPreferences.contains("reads")) {
            mPreferences.edit().putLong("reads", 0).commit();
            handler.postDelayed(updateHintThread, 500);
            new RetrieveFeedTask().execute();
            // @todo check, if not enough is imported
        } else {
            entryCursorAdapter.notifyDataSetChanged();
        }
    }

    public void search(View view) {
        entryList.setVisibility(View.GONE);
        hintview.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        query = searchView.getText().toString();
        hintview.setVisibility(View.VISIBLE);
        return new CursorLoader(
                this,
                EntryContentProvider.CONTENT_URI,
                EntryContract.projection,
                EntryContract.SELECTION_SEARCH,
                EntryContract.searchArgs(query, lang),
                EntryContract.DEFAULT_SORTORDER
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (entryCursorAdapter != null) {
            entryCursorAdapter.swapCursor(data);
            entryList.setVisibility(View.VISIBLE);
            hintview.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (entryCursorAdapter != null) {
            entryCursorAdapter.swapCursor(null);
            entryList.setVisibility(View.VISIBLE);
            hintview.setVisibility(View.INVISIBLE);
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... dummy) {
            ContentValues values = null;
            InputStream ins = null;
            ArrayList<ContentProviderOperation> ops = null;

            long lastinserts = mPreferences.getLong("reads", 0);


            ins = getResources().openRawResource(R.raw.openthesaurus);
            ops = new ArrayList<ContentProviderOperation>();
            String[] str = readTextFile(ins).split(getString(R.string.rowsplit));
            try {

                for (int i=0; i<str.length; i++) {
                    if (inserts < lastinserts) {
                        inserts++;
                        continue;
                    }
                    inserts++;
                    if (str[i].trim().length() == 0) continue;
                    if (str[i].startsWith(getString(R.string.ignoreline))) continue;
                    String[] line = str[i].split(getString(R.string.columnsplit));
                    str[i] = str[i].replace(line[0] + getString(R.string.columnsplit), "");
                    values = new ContentValues();
                    values.put(EntryContract.DbEntry.COLUMN_Title, line[0]);
                    values.put(EntryContract.DbEntry.COLUMN_Body, str[i].replace(getString(R.string.columnsplit), getString(R.string.columnsplitReplace)));
                    values.put(EntryContract.DbEntry.COLUMN_Hint, "de");
                    ops.add(ContentProviderOperation.newInsert(
                            EntryContentProvider.CONTENT_URI).withValues(values).build()
                    );
                    if (inserts % 3000 == 0) {
                        Log.v("w saurus", "importing " + Long.toString(inserts) + " ...");
                        getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
                        ops = new ArrayList<ContentProviderOperation>();
                        mPreferences.edit().putLong("reads", inserts).apply();
                    }
                }
                getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
                mPreferences.edit().putLong("reads", inserts).apply();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }


            ArrayList<Pair<Integer, String>> teis = new ArrayList<>();
            teis.add(new Pair<>(R.raw.deu_eng, "deu_eng"));
            teis.add(new Pair<>(R.raw.ara_eng, "ara_eng"));
            teis.add(new Pair<>(R.raw.kur_deu, "kur_deu"));

            for (Pair<Integer,String> tei : teis) {
                if (inserts < lastinserts) {
                    inserts++;
                    continue;
                }
                ins = getResources().openRawResource(tei.first);
                ops = new ArrayList<ContentProviderOperation>();
                VTDGen vtd = new VTDGen();
                try {
                    vtd.setDoc(IOUtils.toByteArray(ins));
                    vtd.parse(true);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                VTDNav vn = vtd.getNav();
                AutoPilot ap = new AutoPilot(vn);
                AutoPilot ap1 = new AutoPilot(vn);
                AutoPilot ap2 = new AutoPilot(vn);
                try {
                    ap.selectXPath("/TEI/text/body/entry");
                    ap1.selectXPath("form");
                    ap2.selectXPath("sense/cit");
                    while (ap.evalXPath() != -1) {
                        vn.push();
                        while (ap1.evalXPath() != -1) {
                            if (vn.toElement(VTDNav.FIRST_CHILD, "orth")) {
                                values = new ContentValues();
                                values.put(
                                        EntryContract.DbEntry.COLUMN_Title,
                                        vn.toNormalizedString(vn.getText())
                                );
                            }
                        }
                        ap1.resetXPath();
                        vn.pop();
                        vn.push();
                        while (ap2.evalXPath() != -1) {
                            if (vn.toElement(VTDNav.FIRST_CHILD, "quote")) {
                                if (values != null && values.size() == 1) {
                                    values.put(
                                            EntryContract.DbEntry.COLUMN_Body,
                                            vn.toNormalizedString(vn.getText())
                                    );
                                    values.put(
                                            EntryContract.DbEntry.COLUMN_Hint,
                                            tei.second
                                    );
                                    ops.add(ContentProviderOperation.newInsert(
                                            EntryContentProvider.CONTENT_URI).withValues(values).build()
                                    );
                                    inserts++;
                                    if (inserts % 3000 == 0) {
                                        Log.v(tei.second, "importing " + Long.toString(inserts) + " ...");
                                        getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
                                        mPreferences.edit().putLong("reads", inserts).apply();
                                        ops = new ArrayList<ContentProviderOperation>();
                                    }
                                }
                            }
                        }
                        values = null;
                        ap2.resetXPath();
                        vn.pop();
                    }
                    getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
                    mPreferences.edit().putLong("reads", inserts).apply();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            entryCursorAdapter.notifyDataSetChanged();
            inserts = -1;
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
