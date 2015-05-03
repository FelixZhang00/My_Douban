package felixzhang.project.my_douban.dao;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/5/3.
 */
public class DataProvider extends ContentProvider {

    static final String TAG = DataProvider.class.getSimpleName();

    static final Object DBLock = new Object();

    public static final String AUTHORITY = "felixzhang.project.my_douban.provider";
    public static final String SCHEME = "content://";

    // messages
    public static final String PATH_SEARCHED_BOOKS = "/searched_books";

    public static final Uri SEARCHED_BOOKS_CONTENT_URI = Uri.parse(SCHEME + AUTHORITY + PATH_SEARCHED_BOOKS);

    private static final int SEARCHED_BOOKS = 0;

    /*
    * MIME type definitions
    */
    public static final String SEARCHED_BOOKS_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.felixzhang.project.my_douban.searched_books";

    private static final UriMatcher sUriMatcher;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, "searched_books", SEARCHED_BOOKS);
    }

    /**
     * *************引用一个操作数据库的实例 单例***************************************************
     */

    private static DBHelper mDBHelper;

    public static DBHelper getDBHelper() {
        if (mDBHelper == null) {
            mDBHelper = new DBHelper(MyApp.getContext());
        }
        return mDBHelper;
    }

    /**
     * ****************************************************************
     */


    @Override
    public boolean onCreate() {
        return true;  //成功loaded
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        synchronized (DBLock) {
            SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
            String table = matchTable(uri);
            queryBuilder.setTables(table);
            SQLiteDatabase db = getDBHelper().getReadableDatabase();

            Cursor cursor = queryBuilder.query(db, // The database to
                    // queryFromDB
                    projection, // The columns to return from the queryFromDB
                    selection, // The columns for the where clause
                    selectionArgs, // The values for the where clause
                    null, // don't group the rows
                    null, // don't filter by row groups
                    sortOrder // The sort order
            );

            cursor.setNotificationUri(getContext().getContentResolver(), uri);

            return cursor;
        }
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SEARCHED_BOOKS:

                return SEARCHED_BOOKS_CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI" + uri);
        }

    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        synchronized (DBLock) {
            String table = matchTable(uri);
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            long rowId = 0;
            db.beginTransaction();
            try {
                rowId = db.insert(table, null, values);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Logger.i(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }
            if (rowId > 0) {
                Uri resultUri = ContentUris.withAppendedId(uri, rowId);
                getContext().getContentResolver().notifyChange(uri, null);
                return resultUri;
            }

            throw new SQLException("Failed to insert row into " + uri);
        }

    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            int count = 0;
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            String table = matchTable(uri);
            db.beginTransaction();

            try {
                count = db.delete(table, selection, selectionArgs);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                Logger.i(TAG, e.getMessage());
            } finally {
                db.endTransaction();
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        synchronized (DBLock) {
            int count = 0;
            SQLiteDatabase db = getDBHelper().getWritableDatabase();
            String table = matchTable(uri);

            db.beginTransaction();
            try {
                count = db.delete(table, selection, selectionArgs);
            } finally {
                db.endTransaction();
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return count;
        }
    }

    /**
     * 根据Uri找到合适的表名
     *
     * @return
     */
    private String matchTable(Uri uri) {
        String table = null;
        switch (sUriMatcher.match(uri)) {
            case SEARCHED_BOOKS:
                table = SearchedBookDataHelper.SearchedBookDBInfo.TABLE_NAME;
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        return table;

    }
}
