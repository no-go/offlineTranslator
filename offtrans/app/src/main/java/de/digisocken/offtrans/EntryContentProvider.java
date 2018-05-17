package de.digisocken.offtrans;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public class EntryContentProvider extends ContentProvider {

    public static final String AUTHORITY = "de.digisocken.offtrans.contentprovider";
    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/entries";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/entry";

    private EntryHelper _database;

    // used for the UriMacher
    private static final int ENTRIES = 10;
    private static final int ENTRY_ID = 20;

    private static final String BASE_PATH = "entries";

    public static final Uri CONTENT_URI = Uri.parse(
            "content://" + AUTHORITY
            + "/" + BASE_PATH
    );

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, ENTRIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", ENTRY_ID);
    }

    @Override
    public boolean onCreate() {
        _database = new EntryHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(
            Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder
    ) {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(EntryContract.DbEntry.TABLE_NAME);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {

            case ENTRIES:
                break;

            case ENTRY_ID:
                queryBuilder.appendWhere(
                        EntryContract.DbEntry._ID + "=" + uri.getLastPathSegment()
                );
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = _database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(
                db, projection, selection, selectionArgs, null, null, sortOrder
        );
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = _database.getWritableDatabase();
        long id = 0;
        switch (uriType) {

            case ENTRIES:
                id = sqlDB.insert(EntryContract.DbEntry.TABLE_NAME, null, contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = _database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {

            case ENTRIES:
                rowsDeleted = sqlDB.delete(EntryContract.DbEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case ENTRY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(
                            EntryContract.DbEntry.TABLE_NAME,
                            EntryContract.DbEntry._ID + "=" + id,
                            null
                    );
                } else {
                    rowsDeleted = sqlDB.delete(
                            EntryContract.DbEntry.TABLE_NAME,
                            EntryContract.DbEntry._ID + "=" + id + " and " + selection,
                            selectionArgs
                    );
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = _database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {

            case ENTRIES:
                rowsUpdated = sqlDB.update(
                        EntryContract.DbEntry.TABLE_NAME,
                        contentValues,
                        selection,
                        selectionArgs
                );
                break;

            case ENTRY_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(
                            EntryContract.DbEntry.TABLE_NAME,
                            contentValues,
                            EntryContract.DbEntry._ID + "=" + id,
                            null
                    );
                } else {
                    rowsUpdated = sqlDB.update(
                            EntryContract.DbEntry.TABLE_NAME,
                            contentValues,
                            EntryContract.DbEntry._ID + "=" + id + " and " + selection,
                            selectionArgs
                    );
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
}
