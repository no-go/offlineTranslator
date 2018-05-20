package de.digisocken.offtrans;

import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.OperationApplicationException;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static String query = "";
    private EntryCursorAdapter entryCursorAdapter;
    private ListView entryList;
    private EditText searchView;
    public static String lang = "de";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_de:
                    lang = "de";
                    search(searchView);
                    return true;
                case R.id.navigation_de_en:
                    lang = "deu_eng";
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        searchView = (EditText) findViewById(R.id.searchView);

        getLoaderManager().initLoader(0, null, this);
        entryList = (ListView) findViewById(R.id.dicList);
/*
        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DbEntry item = (DbEntry) adapterView.getItemAtPosition(i);
                String msg = item.title + "\n\n" + item.body;
                Intent myIntent = new Intent(MainActivity.this, EditActivity.class);
                myIntent.putExtra("msg", msg);
                startActivity(myIntent);
            }
        });
*/
        SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!mPreferences.contains("initial_read")) {
            mPreferences.edit().putBoolean("initial_read", true).commit();
            new RetrieveFeedTask().execute();
        } else {
            entryCursorAdapter = new EntryCursorAdapter(this, null, 0);
            entryList.setEmptyView(findViewById(android.R.id.empty));
            entryList.setAdapter(entryCursorAdapter);
            entryCursorAdapter.notifyDataSetChanged();
        }
    }

    public void search(View view) {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        query = searchView.getText().toString();
        //if (!query.equals("")) {
            return new CursorLoader(
                    this,
                    EntryContentProvider.CONTENT_URI,
                    EntryContract.projection,
                    EntryContract.SELECTION_SEARCH,
                    EntryContract.searchArgs(query, lang),
                    EntryContract.DEFAULT_SORTORDER
            );
        //}
        /*
        return new CursorLoader(
                this,
                EntryContentProvider.CONTENT_URI,
                EntryContract.projection,
                EntryContract.DEFAULT_SELECTION,
                EntryContract.DEFAULT_SELECTION_ARGS,
                EntryContract.DEFAULT_SORTORDER
        );
        */
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (entryCursorAdapter != null) {
            entryCursorAdapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        if (entryCursorAdapter != null) {
            entryCursorAdapter.swapCursor(null);
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... dummy) {
            long inserts = 0;
            ContentValues values = null;
            InputStream ins = null;
            ArrayList<ContentProviderOperation> ops = null;

            ops = new ArrayList<ContentProviderOperation>();
            ins = getResources().openRawResource(R.raw.openthesaurus);
            String[] str = readTextFile(ins).split(getString(R.string.rowsplit));
            try {

                for (int i=0; i<str.length; i++) {
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
                    inserts++;
                    if (inserts % 2000 == 0) {
                        Log.v("w saurus", "importing " + Long.toString(inserts) + " ...");
                    }
                }
                getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }

            inserts = 0;
            ops = new ArrayList<ContentProviderOperation>();
            VTDGen vtd = new VTDGen();
            ins = getResources().openRawResource(R.raw.deu_eng);
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
                                        "deu_eng"
                                );
                                ops.add(ContentProviderOperation.newInsert(
                                        EntryContentProvider.CONTENT_URI).withValues(values).build()
                                );
                                inserts++;
                                if (inserts % 2000 == 0) {
                                    Log.v("w trans", "importing " + Long.toString(inserts) + " ...");
                                }
                            }
                        }
                    }
                    values = null;
                    ap2.resetXPath();
                    vn.pop();
                }
            } catch (XPathParseException e) {
                e.printStackTrace();
            } catch (PilotException e) {
                e.printStackTrace();
            } catch (NavException e) {
                e.printStackTrace();
            } catch (XPathEvalException e) {
                e.printStackTrace();
            }

            try {
                getContentResolver().applyBatch(EntryContract.AUTHORITY, ops);
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            entryCursorAdapter = new EntryCursorAdapter(getApplicationContext(), null, 0);
            entryList.setEmptyView(findViewById(android.R.id.empty));
            entryList.setAdapter(entryCursorAdapter);

            entryCursorAdapter.notifyDataSetChanged();
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
