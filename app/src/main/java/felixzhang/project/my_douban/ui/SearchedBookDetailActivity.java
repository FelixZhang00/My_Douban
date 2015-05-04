package felixzhang.project.my_douban.ui;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.dao.SearchedBookDataHelper;
import felixzhang.project.my_douban.engine.ThumbnailDownLoader;
import felixzhang.project.my_douban.model.Book;
import felixzhang.project.my_douban.util.Logger;
import felixzhang.project.my_douban.view.ParallaxListView;


/**
 * Created by felix on 15/5/3.
 * 网络访问的方式加载数据 {@link felixzhang.project.my_douban.ui.NewBookDetailActivity }
 */
public class SearchedBookDetailActivity extends BaseActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_BOOKID = "felixzhang.project.my_douban.bookid";
    private static final String TAG = SearchedBookDetailActivity.class.getSimpleName();


    @InjectView(R.id.listview)
    ParallaxListView mListView;
    private View mHeadView;     //listview的头部


    private ProgressDialog mProgressDialog;
    private SearchedBookDataHelper mDataHelper;
    private String mBookId;
    private Book mBook;

    private ArrayList<Book> mSingleList;  //在头部视差中用的


    private MyAdapter mAdapter;


    private ThumbnailDownLoader<ImageView> mThumbnailDownLoader;   //异步加载图片的多线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_detail2);
        ButterKnife.inject(this);

        parseArgument();


        mThumbnailDownLoader = new ThumbnailDownLoader<ImageView>(new Handler());
        mThumbnailDownLoader.setListener(new ThumbnailDownLoader.Listener<ImageView>() {

            @Override
            public void onThumbnailDownloaded(ImageView imageview,
                                              Bitmap thumbnail) {

                //TODO 做模糊图片阴影 类似豆瓣
//                BitmapDrawable bd = new BitmapDrawable(getResources(), thumbnail);
//                imageview.setImageDrawable(bd);

                if (thumbnail!=null){
                    imageview.setImageBitmap(thumbnail);
                }


            }

        });
        mThumbnailDownLoader.start();
        mThumbnailDownLoader.getLooper();


        mSingleList = new ArrayList<>();
        mDataHelper = new SearchedBookDataHelper(this);
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_BOOKID, mBookId);
        getSupportLoaderManager().initLoader(0, bundle, this);


    }

    private void parseArgument() {
        mBookId = getIntent().getStringExtra(EXTRA_BOOKID);

    }

    private void updateUI() {
        mAdapter = new MyAdapter(this, R.layout.book_detail2_item, mSingleList);
        mListView.setAdapter(mAdapter);
        addHeadView();

    }

    private void addHeadView() {
        mHeadView = View.inflate(this, R.layout.head, null);            //异步解析xml中的布局
        mListView.addHeaderView(mHeadView);
        mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);        //设置滑到顶部和底部的效果
        final ImageView parallaxImageView = (ImageView) mHeadView.findViewById(R.id.imageView);

        //当从xml中加载完成后，才能知道imageview的长高
        mHeadView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {
                mListView.setParallaxImageView(parallaxImageView);

                mHeadView.getViewTreeObserver().removeOnGlobalLayoutListener(this);  //取消当前的观察者
            }
        });

        //检查是否有大图
        String imgurl = mBook.images.large;
        Logger.i(TAG,"imgurl: "+imgurl);
        if (imgurl == null || "".equals(imgurl.trim())) {
        } else {
            mThumbnailDownLoader.queueThumbnail(parallaxImageView, mBook.images.large);
        }
    }


    /**
     * *******数据库异步加载**********************************************
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String bookid = args.getString(EXTRA_BOOKID);
        return mDataHelper.getCursorLoader(bookid);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mBook = Book.fromCursor(data);
            Logger.i(TAG, mBook.toJson());
            mSingleList.add(mBook);
            updateUI();
        }
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        Logger.i(TAG, "onLoaderReset");
    }


    /**
     * *********************************************************************
     */

    class MyAdapter extends ArrayAdapter<Book> {
        Context mContext;
        int mResource;

        public MyAdapter(Context context, int resource, List<Book> objects) {
            super(context, resource, objects);
            mContext = context;
            mResource = resource;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(mContext, mResource, null);
            }
            TextView tvPublish = (TextView) convertView.findViewById(R.id.tv_publish);
            TextView tvSummary = (TextView) convertView.findViewById(R.id.tv_summary);

            Book item = getItem(position);

            tvPublish.setText(item.getDescription());
            tvSummary.setText(item.summary);

            return convertView;
        }
    }


}
