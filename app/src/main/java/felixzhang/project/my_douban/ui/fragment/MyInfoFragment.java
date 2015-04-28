package felixzhang.project.my_douban.ui.fragment;

import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.engine.DoubanFetcher;
import felixzhang.project.my_douban.engine.ThumbnailDownLoader;
import felixzhang.project.my_douban.model.User;

/**
 * Created by felix on 15/4/28.
 */
public class MyInfoFragment extends BaseFragment implements SwipeRefreshLayout.OnRefreshListener {

    @InjectView(R.id.swipe_container)
    SwipeRefreshLayout mSwipeLayout;

    @InjectView(R.id.iv_user_photo)
    ImageView mUserPhoto;

    @InjectView(R.id.tv_user_name)
    TextView mUserName;

    private MenuItem mRefreshItem;

    private SharedPreferences mPrefs;
    private ThumbnailDownLoader<ImageView> mThumbnailDownLoader;

    public static MyInfoFragment newInstance() {
        MyInfoFragment fragment = new MyInfoFragment();
        Bundle bundle = new Bundle();

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mPrefs = getActivity().getSharedPreferences(MyApp.PREFS_FILE, Context.MODE_PRIVATE);

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
        View contentView = inflater.inflate(R.layout.myinfo, container, false);
        ButterKnife.inject(this, contentView);

        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        updateUI();
        return contentView;
    }

    private void updateUI() {
        String name = mPrefs.getString(User.USER_NAME, "");
        String userID = mPrefs.getString(User.USER_ID, "");
        mUserName.setText(name);
        new FetchUserTask().execute(userID);
    }

    @Override
    public void loadData() {
        if (isFirstRefresh || !mSwipeLayout.isRefreshing()) {  //刚刷新才加载数据 or 对于actionbar上的按钮来说。。。
            updateUI();
        }
    }

    private boolean isFirstRefresh = true;  //连续按刷新中的第一次

    @Override
    public void onRefresh() {
        if (isFirstRefresh) {
            loadData();
            isFirstRefresh = false;
        }
    }

    class FetchUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setRefreshing(true);
        }

        @Override
        protected String doInBackground(String... params) {
            String userID = params[0];
            try {
                return new DoubanFetcher().getUserPhotoUrl(userID);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String imgurl) {
            super.onPostExecute(imgurl);
            mThumbnailDownLoader.queueThumbnail(mUserPhoto, imgurl);
            if (mSwipeLayout != null && mSwipeLayout.isRefreshing()) {
                setRefreshing(false);
                isFirstRefresh = true;
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
