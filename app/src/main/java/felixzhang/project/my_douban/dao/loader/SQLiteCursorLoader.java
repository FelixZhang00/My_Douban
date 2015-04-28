package felixzhang.project.my_douban.dao.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.database.Cursor;

/**
 * Created by felix on 15/4/28.
 */
public abstract class SQLiteCursorLoader extends AsyncTaskLoader<Cursor> {

    private Cursor mCursor;

    public SQLiteCursorLoader(Context context) {
        super(context);
    }

    protected abstract Cursor loadCursor();

    @Override
    public Cursor loadInBackground() {
        Cursor cursor = loadCursor();
        if (cursor != null) {
            cursor.getCount(); //保证数据加载到内存中
        }
        return cursor;
    }

    @Override
    public void deliverResult(Cursor data) {
        Cursor oldCursor = mCursor;
        mCursor = data;

        if (isStarted()) {
            super.deliverResult(data);
        }

        if (oldCursor != null && oldCursor != data && !oldCursor.isClosed()) {
            oldCursor.close(); //释放旧的资源
        }

    }


    @Override
    protected void onStartLoading() {
        if (mCursor != null) {
            deliverResult(mCursor);
        }
        if (takeContentChanged() || mCursor == null) {
            forceLoad();
        }

    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor data) {
        if (data != null && !data.isClosed()) {
            data.close();
        }
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();


        if (mCursor != null && !mCursor.isClosed()) {
            mCursor.close();
        }
        mCursor = null;
    }
}
