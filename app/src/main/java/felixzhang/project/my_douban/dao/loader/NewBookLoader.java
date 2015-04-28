package felixzhang.project.my_douban.dao.loader;

import android.content.Context;

import felixzhang.project.my_douban.dao.DBHelper;
import felixzhang.project.my_douban.model.NewBook;

/**
 * Created by felix on 15/4/28.
 */
public class NewBookLoader extends DataLoader<NewBook> {

    private String mBookID;
    private DBHelper mDBHelper;
    public NewBookLoader(Context context, String bookID) {
        super(context);
        mBookID = bookID;
        mDBHelper=new DBHelper(context);
    }

    @Override
    public NewBook loadInBackground() {
        return mDBHelper.queryNewBook(mBookID);
    }
}
