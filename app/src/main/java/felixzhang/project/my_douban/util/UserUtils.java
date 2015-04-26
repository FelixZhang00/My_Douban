package felixzhang.project.my_douban.util;

import android.content.Context;
import android.content.SharedPreferences;

import felixzhang.project.my_douban.MyApp;

/**
 * Created by felix on 15/4/26.
 */
public class UserUtils {

    /**
     * 判断是否得到了用户授权
     *
     * @return
     */
    public static boolean isUserAuthoroized() {
        SharedPreferences sp = MyApp.getContext().getSharedPreferences("config", Context.MODE_PRIVATE);
        String tokenaccess = sp.getString("tokenaccess", null);
        String tokensecret = sp.getString("tokensecret", null);
        if (tokenaccess == null || tokensecret == null
                || "".equals(tokenaccess)) {
            return false;
        } else {
            return true;
        }
    }
}
