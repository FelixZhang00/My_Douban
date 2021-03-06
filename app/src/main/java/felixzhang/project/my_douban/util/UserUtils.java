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
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(MyApp.PREFS_FILE, Context.MODE_PRIVATE);
        String access_token = sp.getString("access_token", null);
        if (access_token == null || "".equals(access_token)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 清楚用户登录信息
     */
    public static void clearUser(){
        SharedPreferences sp = MyApp.getContext().getSharedPreferences(MyApp.PREFS_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
    }



}
