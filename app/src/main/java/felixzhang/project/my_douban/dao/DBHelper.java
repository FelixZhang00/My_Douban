package felixzhang.project.my_douban.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import felixzhang.project.my_douban.model.NewBook;

/**
 * Created by felix on 15/4/27.
 * FIXME 暂时没有保存 评分字段
 */
public class DBHelper extends SQLiteOpenHelper {

    // 数据库名
    private static final String DB_NAME = "my_douban.db";
    private static final int DB_VERSION = 1;


    //book数据表
    private static final String TABLE_BOOK = "books";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_BOOKID = "bookid";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_IMGURL = "imgurl";
    private static final String COLUMN_SUMMARY = "summary";


    //为避免字段名重复，searched_books表的字段定义，采用封装的形式

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建book表
        String create_book_sql = "CREATE TABLE \"books\" (\n" +
                "\t \"_id\" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                "\t \"bookid\" INTEGER,\n" +
                "\t \"title\" TEXT,\n" +
                "\t \"description\" TEXT,\n" +
                "\t \"imgurl\" TEXT,\n" +
                "\t \"summary\" TEXT\n" +
                ");";
        db.execSQL(create_book_sql);

        //创建searched_books表
        SearchedBookDataHelper.SearchedBookDBInfo.TABLE.create(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertNewBook(NewBook newBook) {
        String bookID = newBook.getId();
        if (!isExitBook(bookID)) {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_BOOKID, bookID);
            cv.put(COLUMN_TITLE, newBook.getTitle());
            cv.put(COLUMN_DESCRIPTION, newBook.getDescription());
            cv.put(COLUMN_IMGURL, newBook.getImgurl());
            cv.put(COLUMN_SUMMARY, newBook.getSummary());
            return getWritableDatabase().insert(TABLE_BOOK, null, cv);
        } else {
            return -1;
        }
    }

    /**
     * 数据表中是否存在指定bookid的书
     */
    private boolean isExitBook(String bookID) {
        Cursor cursor = getReadableDatabase().rawQuery("select count(*) from books WHERE bookid=?", new String[]{bookID});

        int count = 0;
        if (cursor.moveToNext()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        if (count != 0) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * 根据bookid查询新书
     *
     * @return
     */
    public NewBook queryNewBook(String bookID) {
        Cursor cursor = getReadableDatabase().query(TABLE_BOOK,
                null,
                COLUMN_BOOKID + "=?",
                new String[]{bookID},
                null,
                null,
                null
        );
        NewBook newBook = new NewBook();
        if (cursor.moveToNext()) {
            newBook.setId(cursor.getString(cursor.getColumnIndex(COLUMN_BOOKID)));
            newBook.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            newBook.setSummary(cursor.getString(cursor.getColumnIndex(COLUMN_SUMMARY)));
            newBook.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            newBook.setImgurl(cursor.getString(cursor.getColumnIndex(COLUMN_IMGURL)));
        }

        cursor.close();
        return newBook;
    }


}
