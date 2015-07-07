package felixzhang.project.my_douban.ui.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.api.DoubanApi;
import felixzhang.project.my_douban.dao.SearchedBookDataHelper;
import felixzhang.project.my_douban.engine.data.GsonRequest;
import felixzhang.project.my_douban.model.Book;
import felixzhang.project.my_douban.ui.MainActivity;
import felixzhang.project.my_douban.ui.NewBookDetailActivity;
import felixzhang.project.my_douban.ui.SearchedBookDetailActivity;
import felixzhang.project.my_douban.ui.adapter.BookRequestDataAdapter;
import felixzhang.project.my_douban.util.CommUtils;
import felixzhang.project.my_douban.util.Logger;
import felixzhang.project.my_douban.util.TaskUtil;
import felixzhang.project.my_douban.view.LoadingFooter;
import felixzhang.project.my_douban.view.OnLoadNextListener;
import felixzhang.project.my_douban.view.PageListView;

/**
 * Created by felix on 15/5/3.
 */
public class SearchBookFragment extends BaseFragment implements MainActivity.onDrawerListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();

    @InjectView(R.id.listView)
    PageListView mListview;

    private MenuItem mRefreshItem;
    private Menu mMenu;
    private ProgressDialog mProgressDialog;

    private SearchView searchView;

    private BookRequestDataAdapter mAdapter;

    private String mStart = "0";    //分页查询的首位置
    private int mTotal = 0;  // 分页总个数
    private boolean isEnd = false;  //是否达到最后一条记录
    private boolean isScrollToTop;  //是否滚到首页

    private SearchedBookDataHelper mDataHelper;

    public static SearchBookFragment newInstance() {
        return new SearchBookFragment();
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_book_searched, container, false);
        ButterKnife.inject(this, contentView);
        ((MainActivity) getActivity()).setOnDrawerListener(this);   //MainActivity中侧滑菜单的监听器

        isEnd = false;

        mDataHelper = new SearchedBookDataHelper(getActivity());
        mAdapter = new BookRequestDataAdapter(getActivity());

        mListview.setAdapter(mAdapter);
        mListview.setLoadNextListener(new OnLoadNextListener() {
            @Override
            public void onLoadNext() {
                if (!isEnd) {
                    loadNext();
                }
            }
        });
        mListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bookid = mAdapter.getItem(position).id;
                Logger.i(TAG, "bookid = " + bookid);
                Intent intent = new Intent(getActivity(), SearchedBookDetailActivity.class);
                intent.putExtra(NewBookDetailActivity.EXTRA_BOOKID, bookid);
                getActivity().startActivity(intent);

            }
        });

        getLoaderManager().initLoader(0, null, this);
        loadFirst();
        return contentView;
    }


    @Override
    public void loadFirstAndScrollToTop() {
        loadFirst();
        mListview.smoothScrollToPosition(0);
    }

    public void updateQuery() {
        isEnd = false;   //更新查询内容时要记得更新此变量
        isScrollToTop = false;
        mListview.setState(LoadingFooter.State.Idle);
        Logger.i(TAG, "isEnd " + isEnd);
    }

    /**
     * *******数据分页加载**********************************************
     */


    private void loadFirst() {
        Logger.i(TAG, "loadFirst");
        mStart = "0";
        loadData(mStart);
    }

    private void loadNext() {
        loadData(mStart);
    }

    private void loadData(String start) {
        Logger.i(TAG, "loadData" + "START =  " + start);
        if ("0".equals(start)) {
            setRefreshing(true);
        }

        String query = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext()).getString(MyApp.PREF_SEARCHQUERY, null);

        if (query != null) {
            String url = getActivity().getString(R.string.booksearch_host) + "?q=" + query.trim() + "&start=" + start + "&apikey=" + DoubanApi.douban_apiKey;
            Logger.i(TAG, "url = " + url);
            executeRequest(new GsonRequest(url, Book.BookRequestData.class, responseListener(), errorListener()));
        } else {  //没有输入搜索关键字的情况
            setRefreshing(false);
        }


    }


    private Response.Listener<Book.BookRequestData> responseListener() {
        Logger.i(TAG, "responseListener");
        final boolean isLoadFirst = ("0".equals(mStart));
        return new Response.Listener<Book.BookRequestData>() {
            @Override
            public void onResponse(final Book.BookRequestData response) {
                TaskUtil.executeAsyncTask(new AsyncTask<Object, Object, Object>() {
                    @Override
                    protected Object doInBackground(Object... params) {
                        if (isLoadFirst) {
                            int deletenum = mDataHelper.deleteAll();
                            Logger.i(TAG, "deletenum = " + deletenum);
                        }
                        mTotal = response.getTotal();
                        mStart = response.getStart() + response.getCount() + "";
                        ArrayList<Book> books = response.books;
                        mDataHelper.bulkInsert(books);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Object o) {
                        super.onPostExecute(o);
                        if (isLoadFirst) {
                            setRefreshing(false);
                        } else {
                            if (Integer.parseInt(mStart) >= mTotal) {
                                isEnd = true;
                                mListview.setState(LoadingFooter.State.TheEnd);
                            } else {
                                mListview.setState(LoadingFooter.State.Idle);
                            }

                        }
                    }
                });

            }
        };
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MyApp.getContext(), R.string.loading_failed, Toast.LENGTH_SHORT).show();
                setRefreshing(false);
                mListview.setState(LoadingFooter.State.Idle, 3000);
            }
        };
    }

    /**
     * *********************************************************************
     */


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.search_book, menu);
        mMenu = menu;
        mRefreshItem = menu.findItem(R.id.action_refresh);


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {

            // Get the SearchView and set the searchable configuration
            SearchManager searchManager = (SearchManager) getActivity()
                    .getSystemService(Context.SEARCH_SERVICE);
            searchView = (SearchView) menu
                    .findItem(R.id.search_book).getActionView();


            // Assumes current activity is the searchable activity
            searchView.setSearchableInfo(searchManager
                    .getSearchableInfo(getActivity().getComponentName()));

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.i(TAG, "menu clicked");
        switch (item.getItemId()) {
            case R.id.search_book:          //由系统捕获，在此无法捕获
//                Logger.i(TAG, "SEARCH CHILCKED");
//                getActivity().onSearchRequested();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    /**
     * MainActivity中侧菜单的监听器
     */

    @Override
    public void onDrawerOpened() {
        Logger.i(TAG, "onDrawerOpened");
        MenuItem searchItem = mMenu.findItem(R.id.search_book);
        if (searchItem != null && searchView != null) {
            CommUtils.hideKeyboard(getActivity(), searchView.getWindowToken());
            searchView.setVisibility(View.GONE);
            searchItem.setVisible(false);

        }
    }

    @Override
    public void onDrawerClosed() {
        Logger.i(TAG, "onDrawerClosed");
        MenuItem searchItem = mMenu.findItem(R.id.search_book);
        if (searchItem != null && searchView != null) {
            Logger.i(TAG, "onDrawerClosed IN searchItem!=null");
            searchItem.setVisible(true);
            searchView.setVisibility(View.VISIBLE);
        }
    }


    private void setRefreshing(boolean refreshing) {

        if (mRefreshItem == null || mProgressDialog == null) return;

        if (refreshing) {
            mRefreshItem.setActionView(R.layout.actionbar_refresh_progress);
            mProgressDialog.show();
        } else {
            mRefreshItem.setActionView(null);
            mProgressDialog.dismiss();
        }


    }


    /**
     * *******数据库异步加载**********************************************
     */

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Logger.i(TAG, "onCreateLoader");
        return mDataHelper.getCursorLoader();
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Logger.i(TAG, "onLoadFinished");        //每当Loader对应的数据库发生变话时就会调用该方法。
        mAdapter.changeCursor(data);
        if (data != null && data.getCount() == 0) {
            Logger.i(TAG, "data is empty");
            loadFirst();
        } else if (data == null) {
            Logger.i(TAG, "data is null");
        } else {
            Logger.i(TAG, "data have something" + data.getCount());

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }


    /*************************************************************************/

}
