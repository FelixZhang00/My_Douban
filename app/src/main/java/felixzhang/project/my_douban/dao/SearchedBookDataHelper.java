package felixzhang.project.my_douban.dao;

import android.content.Context;
import android.net.Uri;
import android.provider.BaseColumns;

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
        return null;
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
