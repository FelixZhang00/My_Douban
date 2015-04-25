package felixzhang.project.my_douban.ui.fragment;

import android.support.v4.app.Fragment;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


/**
 * Created by storm on 14-3-25.
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    @Override
    public void onDestroy() {
        Log.i(TAG, "BaseFragment onDestroy");
        super.onDestroy();
//        RequestManager.cancelAll(this);
    }

    protected void executeRequest(Request request) {
//        RequestManager.addRequest(request, this);
    }

    protected Response.ErrorListener errorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                ToastUtils.showLong(error.getMessage());
            }
        };
    }
}
