package felixzhang.project.my_douban.util;

import android.app.Activity;
import android.view.View;

/**
 * Created by felix on 15/5/3.
 */
public class ActionBarUtils {
    public static View findActionBarContainer(Activity activity) {
        int id = activity.getResources().getIdentifier("action_bar_container", "id", "android");
        return activity.findViewById(id);
    }

}
