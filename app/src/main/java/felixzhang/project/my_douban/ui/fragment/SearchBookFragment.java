package felixzhang.project.my_douban.ui.fragment;


import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.SearchView;

import com.android.volley.Response;

import butterknife.ButterKnife;
import butterknife.InjectView;
import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.api.DoubanApi;
import felixzhang.project.my_douban.engine.data.GsonRequest;
import felixzhang.project.my_douban.model.Book;
import felixzhang.project.my_douban.ui.MainActivity;
import felixzhang.project.my_douban.ui.adapter.BookRequestDataAdapter;
import felixzhang.project.my_douban.util.CommUtils;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/5/3.
 */
public class SearchBookFragment extends BaseFragment implements MainActivity.onDrawerListener, LoaderManager.LoaderCallbacks<Cursor> {

    private final String TAG = getClass().getSimpleName();

    @InjectView(R.id.listview)
    ListView mListView;

    private MenuItem mRefreshItem;
    private Menu mMenu;
    private ProgressDialog mProgressDialog;

    private SearchView searchView;

    private Book.BookRequestData mBookRequestData;
    private BookRequestDataAdapter mAdapter;

    private String mStart;    //分页查询的首位置

    public static SearchBookFragment newInstance() {
        return new SearchBookFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_book_searched, container, false);
        ButterKnife.inject(this, contentView);
        ((MainActivity) getActivity()).setOnDrawerListener(this);   //MainActivity中侧滑菜单的监听器

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String bookid = ((Book) parent.getItemAtPosition(position)).id;
                Logger.i(TAG, "ID= " + bookid);
            }
        });

        getLoaderManager().initLoader(0, null, this);
        setupAdapter();
        loadFirst();
        return contentView;
    }


    private void setupAdapter() {
        if (getActivity() == null || mListView == null) {
            return;
        }

        if (mBookRequestData != null) {
            // mGridview.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),
            // android.R.layout.simple_gallery_item, mGalleryItems));
            mAdapter = new BookRequestDataAdapter(getActivity(), mBookRequestData.books);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter = null;
            mListView.setAdapter(null);
            // Toast.makeText(getActivity(), "加载失败", 0).show();
        }
    }


    @Override
    public void loadFirstAndScrollToTop() {
        loadFirst();
    }

    /**
     * *******数据分页加载**********************************************
     */


    private void loadFirst() {
        mStart = "0";
        loadData(mStart);
    }

    private void loadNext() {
        loadData(mStart);
    }

    private void loadData(String start) {
        Logger.i(TAG, "LOADDATE");
        if ("0".equals(start)){
            setRefreshing(true);
        }

        String query = PreferenceManager.getDefaultSharedPreferences(MyApp.getContext()).getString(MyApp.PREF_SEARCHQUERY, null);
        if (query != null) {
            String url = getActivity().getString(R.string.booksearch_host) + "?q=" + query.trim() + "&start=" + start + "&apikey=" + DoubanApi.douban_apiKey;
            executeRequest(new GsonRequest(url, Book.BookRequestData.class, responseListener(), errorListener()));
        }

    }


    private Response.Listener<Book.BookRequestData> responseListener() {
        Logger.i(TAG, "responseListener");

        return new Response.Listener<Book.BookRequestData>() {
            @Override
            public void onResponse(Book.BookRequestData bookRequestData) {
                mBookRequestData = bookRequestData;


                updateUI();

            }
        };
    }

    /*************************************************************************/

    /**
     * 更新界面
     */
    private void updateUI() {
        setupAdapter();
        setRefreshing(false);
    }


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

        if (mRefreshItem == null||mProgressDialog==null) return;

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
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


    /*************************************************************************/

}
