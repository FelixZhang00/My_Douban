package felixzhang.project.my_douban.dao.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Created by felix on 15/4/28.
 *
 * 参考 《Android编程权威指南》中第35章的做法
 */
public abstract class DataLoader<D> extends AsyncTaskLoader<D> {

    private D mData;

    public DataLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (mData != null) {
            deliverResult(mData);
        } else {
            forceLoad();
        }

    }

    @Override
    public void deliverResult(D data) {
        mData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
    }
}
