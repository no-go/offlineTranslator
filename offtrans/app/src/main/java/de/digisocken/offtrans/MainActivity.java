package de.digisocken.offtrans;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EntryAdapter entryAdapterDe;
    private EntryAdapter resultEntryAdapterDe;
    private EntryAdapter entryAdapterEn;
    private EntryAdapter resultEntryAdapterEn;
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
                    entryList.setAdapter(entryAdapterDe);
                    entryAdapterDe.notifyDataSetChanged();
                    return true;
                case R.id.navigation_de_en:
                    lang = "en";
                    entryList.setAdapter(entryAdapterEn);
                    entryAdapterEn.notifyDataSetChanged();
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

        entryAdapterDe = new EntryAdapter(this);
        entryAdapterEn = new EntryAdapter(this);
        entryList = (ListView) findViewById(R.id.dicList);
        entryList.setEmptyView(findViewById(android.R.id.empty));
        entryList.setAdapter(entryAdapterDe);

        entryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DicEntry item = (DicEntry) adapterView.getItemAtPosition(i);
                String msg = item.title + "\n\n" + item.body;
                Intent myIntent = new Intent(MainActivity.this, EditActivity.class);
                myIntent.putExtra("msg", msg);
                startActivity(myIntent);
            }
        });

        resultEntryAdapterDe = new EntryAdapter(this);
        resultEntryAdapterEn = new EntryAdapter(this);
        new RetrieveFeedTask().execute();
    }

    public void search(View view) {
        if (lang.equals("en")) {
            resultEntryAdapterEn.filter(searchView.getText().toString(), entryAdapterEn);
            entryList.setAdapter(resultEntryAdapterEn);
            resultEntryAdapterEn.notifyDataSetChanged();

        } else if (lang.equals("de")) {
            resultEntryAdapterDe.filter(searchView.getText().toString(), entryAdapterDe);
            entryList.setAdapter(resultEntryAdapterDe);
            resultEntryAdapterDe.notifyDataSetChanged();
        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... dummy) {
            try {

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser xpp = factory.newPullParser();
                DicEntry dicEntry = null;
                int eventType = -1;

                InputStream ins = getResources().openRawResource(R.raw.deu_eng);
                xpp.setInput(ins, null);
                eventType = xpp.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {

                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equalsIgnoreCase("orth")) {
                            dicEntry = new DicEntry();
                            dicEntry.title = xpp.nextText();
                        }
                        if (xpp.getName().equalsIgnoreCase("quote")) {
                            dicEntry.body = xpp.nextText();
                            entryAdapterEn.addItem(dicEntry);
                        }
                    }
                    eventType = xpp.next();
                }
                entryAdapterEn.sort();

                ins = getResources().openRawResource(R.raw.openthesaurus);
                String[] str = readTextFile(ins).split(getString(R.string.rowsplit));
                for (int i=0; i<str.length; i++) {
                    dicEntry = new DicEntry();
                    if (str[i].trim().length() == 0) continue;
                    if (str[i].startsWith(getString(R.string.ignoreline))) continue;
                    String[] line = str[i].split(getString(R.string.columnsplit));
                    dicEntry.title = line[0];
                    str[i] = str[i].replace(line[0]+getString(R.string.columnsplit) , "");
                    dicEntry.body = str[i].replace(getString(R.string.columnsplit), getString(R.string.columnsplitReplace));
                    entryAdapterDe.addItem(dicEntry);
                }
                entryAdapterDe.sort();



            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            entryAdapterDe.notifyDataSetChanged();
            entryAdapterEn.notifyDataSetChanged();
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
