package felixzhang.project.my_douban.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.dao.DBHelper;
import felixzhang.project.my_douban.model.NewBook;
import felixzhang.project.my_douban.util.Logger;


/**
 * Created by felix on 15/4/27.
 * 根据intent中传递的书的id在数据库中查找
 */
public class BookDetailActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String BOOKID = "book_id";
    private static final String TAG = "BookDetailActivity";
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.tv_publish)
    TextView publishTextView;

    @InjectView(R.id.tv_summary)
    TextView summaryTextView;


    private ProgressDialog mProgressDialog;
    private DBHelper mDBHelper;

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

        fillData();
    }

    private void fillData() {
        String book_id = getIntent().getStringExtra(BOOKID);

        NewBook newBook = mDBHelper.queryNewBook(book_id);
        Logger.i(TAG, newBook.toString());

    }

    @Override
    public void onRefresh() {

    }
}
