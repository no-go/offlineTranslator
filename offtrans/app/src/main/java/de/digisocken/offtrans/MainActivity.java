package de.digisocken.offtrans;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private EntryAdapter entryAdapterDe;
    private EntryAdapter resultEntryAdapterDe;
    private EntryAdapter entryAdapterEn;
    private EntryAdapter resultEntryAdapterEn;
    private EntryAdapter entryAdapterKur;
    private EntryAdapter resultEntryAdapterKur;
    private ListView entryList;
    private EditText searchView;
    private String lang = "de";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_de_en:
                    lang = "de";
                    entryList.setAdapter(entryAdapterDe);
                    entryAdapterDe.notifyDataSetChanged();
                    return true;
                case R.id.navigation_en_de:
                    lang = "en";
                    entryList.setAdapter(entryAdapterEn);
                    entryAdapterEn.notifyDataSetChanged();
                    return true;
                case R.id.navigation_kur_tur:
                    lang = "kur";
                    entryList.setAdapter(entryAdapterKur);
                    entryAdapterKur.notifyDataSetChanged();
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
        entryAdapterKur = new EntryAdapter(this);
        entryList = (ListView) findViewById(R.id.dicList);
        entryList.setEmptyView(findViewById(android.R.id.empty));
        entryList.setAdapter(entryAdapterDe);

        resultEntryAdapterDe = new EntryAdapter(this);
        resultEntryAdapterEn = new EntryAdapter(this);
        resultEntryAdapterKur = new EntryAdapter(this);
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

        } else if (lang.equals("kur")) {
            resultEntryAdapterKur.filter(searchView.getText().toString(), entryAdapterKur);
            entryList.setAdapter(resultEntryAdapterKur);
            resultEntryAdapterKur.notifyDataSetChanged();

        }
    }

    class RetrieveFeedTask extends AsyncTask<String, Void, Void> {
        protected Void doInBackground(String... dummy) {
            try {

                // @todo just repeat the code is a bit strange!?

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
                            entryAdapterDe.addItem(dicEntry);
                        }
                    }
                    eventType = xpp.next();
                }
                entryAdapterDe.sort();

                ins = getResources().openRawResource(R.raw.eng_deu);
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

                ins = getResources().openRawResource(R.raw.kur_tur);
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
                            entryAdapterKur.addItem(dicEntry);
                        }
                    }
                    eventType = xpp.next();
                }
                entryAdapterKur.sort();

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
            entryAdapterKur.notifyDataSetChanged();
        }
    }
}
