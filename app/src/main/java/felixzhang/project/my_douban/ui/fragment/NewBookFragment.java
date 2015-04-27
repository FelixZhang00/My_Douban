package felixzhang.project.my_douban.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.engine.DoubanFetcher;
import felixzhang.project.my_douban.engine.ThumbnailDownLoader;
import felixzhang.project.my_douban.engine.cacheload.ImageLoader;
import felixzhang.project.my_douban.model.NewBook;
import felixzhang.project.my_douban.ui.BookDetailActivity;


/**
 * Created by felix on 15/4/27.
 */
public class NewBookFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "NewBookFragment";
    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.gridView)
    GridView mGridView;

    private MenuItem mRefreshItem;

    private ProgressDialog mProgressDialog;

    private ArrayList<NewBook> mNewBooks;

    private ImageLoader imageLoader;   //三层缓存图片工具
    private ThumbnailDownLoader<ImageView> mThumbnailDownLoader;   //异步加载图片的多线程
    private DoubanFetcher mFetcher;

    public static NewBookFragment newInstance() {
        NewBookFragment fragment = new NewBookFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        imageLoader = new ImageLoader(getActivity());
        mFetcher = new DoubanFetcher();

        mThumbnailDownLoader = new ThumbnailDownLoader<ImageView>(new Handler());
        mThumbnailDownLoader.setListener(new ThumbnailDownLoader.Listener<ImageView>() {

            @Override
            public void onThumbnailDownloaded(ImageView imageview,
                                              Bitmap thumbnail) {
                if (isVisible()) {
                    imageview.setImageBitmap(thumbnail);
                }
            }

        });
        mThumbnailDownLoader.start();
        mThumbnailDownLoader.getLooper();


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_newbook, container, false);
        ButterKnife.inject(this, contentView);

        mProgressDialog = new ProgressDialog(getActivity());  //ProgressDialog只在第一次打开此页面时启用
        mProgressDialog.setCancelable(false);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), BookDetailActivity.class);
                NewBook newBook=mNewBooks.get(position);

                intent.putExtra(BookDetailActivity.BOOKID,newBook.getId());
                getActivity().startActivity(intent);
            }
        });

        return contentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        new FetchItemsTask().execute();
        setupAdapter();
    }

    private void parseArgument() {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mThumbnailDownLoader != null) {
            mThumbnailDownLoader.quit();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mThumbnailDownLoader.clearQueue();
    }


    private void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mNewBooks != null) {
            mGridView.setAdapter(new NewBookAdapter(mNewBooks));
        } else {
            mGridView.setAdapter(null);
        }
    }

    private boolean isFirstRefresh = true;  //连续按刷新中的第一次

    /**
     * SwipeLayout刷新时的回调方法
     */
    @Override
    public void onRefresh() {
        if (isFirstRefresh) {
            loadData();
            isFirstRefresh = false;
        }
    }

    @Override
    public void loadData() {
        if (isFirstRefresh || !mSwipeLayout.isRefreshing()) {  //刚刷新才加载数据 or 对于actionbar上的按钮来说。。。
            new FetchItemsTask().execute();
        }

    }

    private boolean isFirstStart = true;  //是否第一次启动此页面

    class FetchItemsTask extends AsyncTask<Void, Void, ArrayList<NewBook>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (isFirstStart) {
                mProgressDialog.show();
            } else {
                setRefreshing(true);
            }
        }

        @Override
        protected ArrayList<NewBook> doInBackground(Void... params) {
            try {
                return (ArrayList<NewBook>) mFetcher.getNewBooks();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<NewBook> newBooks) {
            super.onPostExecute(newBooks);
            if (isFirstStart) {
                mProgressDialog.dismiss();
                isFirstStart = false;
            } else {

                if (mSwipeLayout != null && mSwipeLayout.isRefreshing()) {
                    setRefreshing(false);
                    isFirstRefresh = true;
                }
            }

            mNewBooks = newBooks;

            setupAdapter();
        }

    }

    private class NewBookAdapter extends ArrayAdapter<NewBook> {

        public NewBookAdapter(ArrayList<NewBook> newBooks) {
            super(getActivity(), 0, newBooks);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = View.inflate(getActivity(), R.layout.newbook_item, null);
            }
            NewBook item = getItem(position);

            mHolder = getHolder(convertView);
            mHolder.bookname.setText(item.getTitle());
            mHolder.grade.setText(item.getRating() + "");
            mHolder.ratingBar.setRating((float) item.getRating() / 2);


            // 方式一：
//            mThumbnailDownLoader.queueThumbnail(mHolder.imageView, item.getImgurl());

            // 方式二： 用开源项目实现的缓存加载图片
            imageLoader.DisplayImage(item.getImgurl(), mHolder.imageView);
            return convertView;
        }

        private ViewHolder getHolder(View convertView) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (holder == null) {
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            }
            return holder;
        }

        private ViewHolder mHolder;

        class ViewHolder {
            ImageView imageView;
            TextView bookname;
            RatingBar ratingBar;
            TextView grade;

            public ViewHolder(View convertView) {
                imageView = (ImageView) convertView.findViewById(R.id.imageview);
                bookname = (TextView) convertView.findViewById(R.id.bookname);
                ratingBar = (RatingBar) convertView.findViewById(R.id.ratingbar);
                grade = (TextView) convertView.findViewById(R.id.grade);
            }
        }

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        mRefreshItem = menu.findItem(R.id.action_refresh);
        super.onCreateOptionsMenu(menu, inflater);
    }


    private void setRefreshing(boolean refreshing) {
        mSwipeLayout.setRefreshing(refreshing);
        if (mRefreshItem == null) return;

        if (refreshing)
            mRefreshItem.setActionView(R.layout.actionbar_refresh_progress);
        else
            mRefreshItem.setActionView(null);
    }

}
