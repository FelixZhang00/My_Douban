package felixzhang.project.my_douban.ui;

import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Loader;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.dao.DBHelper;
import felixzhang.project.my_douban.dao.loader.NewBookLoader;
import felixzhang.project.my_douban.model.NewBook;
import felixzhang.project.my_douban.util.Logger;


/**
 * Created by felix on 15/4/27.
 * 根据intent中传递的书的id在数据库中查找
 * FIXME 这里的数据不全，想要更多的数据需要apikey，而且有访问限制
 */
public class BookDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String BOOKID = "mBookId";
    private static final String TAG = "BookDetailActivity";
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.tv_publish)
    TextView publishTextView;

    @InjectView(R.id.tv_summary)
    TextView summaryTextView;


    private ProgressDialog mProgressDialog;
    private DBHelper mDBHelper;
    private String mBookId;
    private NewBook mNewBook;
    private NewBookLoaderCallbacks mLoaderCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail);
        ButterKnife.inject(this);
        mDBHelper = new DBHelper(this);

        mProgressDialog = new ProgressDialog(this);  //ProgressDialog只在第一次打开此页面时启用
        mProgressDialog.setCancelable(false);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mLoaderCallbacks = new NewBookLoaderCallbacks();
        fillData();
    }

    private void fillData() {
        mBookId = getIntent().getStringExtra(BOOKID);

        LoaderManager lm = getLoaderManager();

        Bundle bundle = new Bundle();
        bundle.putString(BOOKID, mBookId);

        lm.initLoader(0, bundle, mLoaderCallbacks);

    }

    private boolean isFirstRefresh = true;  //连续按刷新中的第一次

    @Override
    public void onRefresh() {
        if (isFirstRefresh) {
            LoaderManager lm = getLoaderManager();
            Bundle bundle = new Bundle();
            bundle.putString(BOOKID, mBookId);
            lm.restartLoader(0, bundle, mLoaderCallbacks);
            isFirstRefresh = false;
        }
    }

    private void updateUI() {
        publishTextView.setText(mNewBook.getDescription());
        summaryTextView.setText(mNewBook.getSummary());
    }

    /**
     * Loader的回调
     */
    private class NewBookLoaderCallbacks implements LoaderManager.LoaderCallbacks<NewBook> {


        @Override
        public Loader<NewBook> onCreateLoader(int id, Bundle args) {
            Logger.i(TAG, "onCreateLoader");
            if (mSwipeLayout != null && !mSwipeLayout.isRefreshing()) {
                mSwipeLayout.setRefreshing(true);
            }
            return new NewBookLoader(BookDetailActivity.this, args.getString(BOOKID));
        }

        @Override
        public void onLoadFinished(Loader<NewBook> loader, NewBook data) {
            if (mSwipeLayout != null && mSwipeLayout.isRefreshing()) {
                mSwipeLayout.setRefreshing(false);
                isFirstRefresh = true;
            }

            mNewBook = data;
            updateUI();
        }

        @Override
        public void onLoaderReset(Loader<NewBook> loader) {

        }
    }


}
