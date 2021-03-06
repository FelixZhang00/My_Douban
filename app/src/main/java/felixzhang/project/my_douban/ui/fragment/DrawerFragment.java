package felixzhang.project.my_douban.ui.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.model.Category;
import felixzhang.project.my_douban.ui.MainActivity;
import felixzhang.project.my_douban.ui.adapter.DrawerAdapter;


/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class DrawerFragment extends BaseFragment {
    private ListView mListView;

    private DrawerAdapter mAdapter;

    private MainActivity mActivity;


    public DrawerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mActivity = (MainActivity) getActivity();
        View contentView = inflater.inflate(R.layout.fragment_drawer, null);
        mListView = (ListView) contentView.findViewById(R.id.listView);
        mAdapter = new DrawerAdapter(mListView);
        mListView.setAdapter(mAdapter);
        mListView.setItemChecked(0, true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListView.setItemChecked(position, true);
                mActivity.setCategory(Category.values()[position]);
            }
        });
        return contentView;
    }


    @Override
    public void loadFirstAndScrollToTop() {

    }
}
