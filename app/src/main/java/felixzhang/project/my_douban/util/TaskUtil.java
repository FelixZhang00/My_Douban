package felixzhang.project.my_douban.util;

import android.os.AsyncTask;
import android.os.Build;

/**
 * Created by felix on 15/5/3.
 */
public class TaskUtil {

    public static <Params, Progress, Result>
    void executeAsyncTask(AsyncTask<Params, Progress, Result> task,
                          Params... params) {

        if (Build.VERSION.SDK_INT >= 11) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            task.execute(params);
        }

    }
}
