package felixzhang.project.my_douban.ui;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.model.Category;
import felixzhang.project.my_douban.ui.fragment.BaseFragment;
import felixzhang.project.my_douban.ui.fragment.DrawerFragment;
import felixzhang.project.my_douban.ui.fragment.MyInfoFragment;
import felixzhang.project.my_douban.ui.fragment.NewBookFragment;
import felixzhang.project.my_douban.ui.fragment.SearchBookFragment;
import felixzhang.project.my_douban.util.Logger;
import felixzhang.project.my_douban.util.UserUtils;
import felixzhang.project.my_douban.view.BlurFoldingActionBarToggle;
import felixzhang.project.my_douban.view.FoldingDrawerLayout;


public class MainActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOGIN = 0;
    private static final int REQUEST_CODE_SEARCHBOOK = 1;
    private static final String TAG = "MainActivity";

    @InjectView(R.id.drawer_layout)
    FoldingDrawerLayout mDrawerLayout;

    @InjectView(R.id.content_frame)
    FrameLayout contentLayout;

    @InjectView(R.id.blur_image)
    ImageView blurImage;

    private BlurFoldingActionBarToggle mDrawerToggle;

    private BaseFragment mContentFragment;

    private Category mCategory;

    private Menu mMenu;

    private onDrawerListener mOnDrawerListener;

    public void setOnDrawerListener(onDrawerListener onDrawerListener) {
        mOnDrawerListener = onDrawerListener;
    }

    public interface onDrawerListener {
        void onDrawerOpened();

        void onDrawerClosed();
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        actionBar.setIcon(R.drawable.ic_actionbar);
        mDrawerLayout.setScrimColor(Color.argb(100, 255, 255, 255)); //设置打开drawer菜单后，drwaerlayout的颜色为白色透明
        mDrawerToggle = new BlurFoldingActionBarToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View view) {
                super.onDrawerOpened(view);
                setTitle(R.string.app_name);
                mMenu.findItem(R.id.action_refresh).setVisible(false);

                if (mOnDrawerListener!=null){
                    mOnDrawerListener.onDrawerOpened();
                }

            }

            @Override
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                setTitle(mCategory.getDisplayName());
                mMenu.findItem(R.id.action_refresh).setVisible(true);


                blurImage.setVisibility(View.GONE);
                blurImage.setImageBitmap(null);

                if (mOnDrawerListener!=null){
                    mOnDrawerListener.onDrawerClosed();
                }
            }
        };
        mDrawerToggle.setBlurImageAndView(blurImage, contentLayout);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        setCategory(Category.myinfo);
        replaceFragment(R.id.left_drawer, new DrawerFragment());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    protected void replaceFragment(int viewId, BaseFragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(viewId, fragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mMenu = menu;
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        switch (item.getItemId()) {
            case R.id.action_refresh: //TODO
                mContentFragment.loadFirstAndScrollToTop();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setCategory(Category category) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        if (mCategory == category) {
            return;
        }
        mCategory = category;
        setTitle(mCategory.getDisplayName());

        if (category.equals(Category.newbook)) {    //进入新书栏目
            mContentFragment = NewBookFragment.newInstance();
            replaceFragment(R.id.content_frame, mContentFragment);
        } else if (category.equals(Category.myinfo)) {  //进入我的资料栏目
            if (!shouldLogin()) {
                mContentFragment = MyInfoFragment.newInstance();
                replaceFragment(R.id.content_frame, mContentFragment);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LOGIN);
            }
        } else if (category.equals(Category.searchbook)) {   //进入图书搜索模块
            mContentFragment = SearchBookFragment.newInstance();
            replaceFragment(R.id.content_frame, mContentFragment);
        }

//        mContentFragment = FeedsFragment.newInstance(category);
//        replaceFragment(R.id.content_frame, mContentFragment);
    }

    /**
     * 判断用户是否需要登录
     */
    private boolean shouldLogin() {
        boolean userAuthoroized = UserUtils.isUserAuthoroized();
        return !userAuthoroized;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_LOGIN) {

            if (resultCode == RESULT_OK) {
                Logger.i(TAG, "用户已登录");
                mCategory = Category.myinfo;
            } else {
                mCategory = Category.newbook;
            }
            setCategoryForResult(mCategory);
        } else if (requestCode == REQUEST_CODE_SEARCHBOOK) {
            mCategory = Category.newbook;
            setCategoryForResult(mCategory);
        }

    }

    private void setCategoryForResult(Category category) {
        setTitle(mCategory.getDisplayName());

        if (category.equals(Category.newbook)) {    //进入新书栏目
            mContentFragment = NewBookFragment.newInstance();
            replaceFragment(R.id.content_frame, mContentFragment);
        } else if (category.equals(Category.myinfo)) {  //进入我的资料栏目
            if (!shouldLogin()) {
                mContentFragment = MyInfoFragment.newInstance();
                replaceFragment(R.id.content_frame, mContentFragment);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, REQUEST_CODE_LOGIN);
            }
        } else if (category.equals(Category.searchbook)) {

        }
    }


    @Override
    protected void onNewIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String searchQuery = intent.getStringExtra(SearchManager.QUERY);
            Logger.i(TAG, searchQuery);

            //保存搜索信息
            SharedPreferences sp = PreferenceManager
                    .getDefaultSharedPreferences(getApplicationContext());
            sp.edit().putString(MyApp.PREF_SEARCHQUERY, searchQuery).commit();

            SearchBookFragment searchBookFragment = (SearchBookFragment) getSupportFragmentManager().findFragmentById(R.id.content_frame);
            searchBookFragment.updateQuery();
            searchBookFragment.loadFirstAndScrollToTop();
        }
    }
}
