package felixzhang.project.my_douban.util;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by felix on 15/4/26.
 */
public class CommUtils {

    /**
     * This method is used to hide a keyboard after a user has finished typing
     * the url.
     */
    public static void hideKeyboard(Activity activity, IBinder windowToken) {
        InputMethodManager mgr = (InputMethodManager) activity
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    public static boolean isValidUrl(String urlstr) {
        if (urlstr == null) return false;

        if (urlstr.startsWith("http://") || urlstr.startsWith("https://")) {
            return true;
        } else {
            return false;
        }
    }

}
