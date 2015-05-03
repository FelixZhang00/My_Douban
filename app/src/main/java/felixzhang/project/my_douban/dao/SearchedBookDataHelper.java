package felixzhang.project.my_douban.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.content.CursorLoader;

import java.util.ArrayList;
import java.util.List;

import felixzhang.project.my_douban.model.Book;
import felixzhang.project.my_douban.util.database.Column;
import felixzhang.project.my_douban.util.database.SQLiteTable;

/**
 * Created by felix on 15/5/3.
 * <p/>
 * 被搜索到的book，只是临时存放
 */
public class SearchedBookDataHelper extends BaseDataHelper {

    public SearchedBookDataHelper(Context context) {
        super(context);
    }

    @Override
    protected Uri getContentUri() {
        return DataProvider.SEARCHED_BOOKS_CONTENT_URI;
    }


    private ContentValues getContentValues(Book book) {
        ContentValues values = new ContentValues();
        values.put(SearchedBookDBInfo.COLUMN_BOOKID, book.id);
        values.put(SearchedBookDBInfo.COLUMN_JSON, book.toJson());
        return values;
    }


    public Book query(long id) {
        Book book = null;
        Cursor cursor = query(null, SearchedBookDBInfo.COLUMN_BOOKID + "= ?",
                new String[]{
                        String.valueOf(id)
                }, null);
        if (cursor.moveToFirst()) {
            book = Book.fromCursor(cursor);
        }
        cursor.close();
        return book;
    }


    public void bulkInsert(List<Book> books) {
        ArrayList<ContentValues> values = new ArrayList<>();
        for (Book book : books) {
            ContentValues contentValues = getContentValues(book);
            values.add(contentValues);
        }
        ContentValues[] array = new ContentValues[values.size()];
        bulkInsert(values.toArray(array));

    }

    public int deleteAll() {
        synchronized (DataProvider.DBLock) {  //删除操作时，所有被这把锁控制的插入查询操作都将等待
            DBHelper mDBHelper = DataProvider.getDBHelper();
            SQLiteDatabase db = mDBHelper.getWritableDatabase();
            int row = db.delete(SearchedBookDBInfo.TABLE_NAME, null, null);
            return row;
        }
    }


    public CursorLoader getCursorLoader() {
        return new CursorLoader(getContext(), getContentUri(), null, null, null, SearchedBookDBInfo._ID + "  ASC");

    }

    /**
     * SearchedBook数据表信息
     */
    public static final class SearchedBookDBInfo implements BaseColumns {
        private SearchedBookDBInfo() {
        }

        public static final String TABLE_NAME = "searched_books";
        public static final String COLUMN_BOOKID = "bookid";
        public static final String COLUMN_JSON = "json";     //方便cursor向bean转化

        public static final SQLiteTable TABLE = new SQLiteTable(TABLE_NAME).addColumn(COLUMN_BOOKID,
                Column.DataType.TEXT).addColumn(COLUMN_JSON, Column.DataType.TEXT);

    }
}
