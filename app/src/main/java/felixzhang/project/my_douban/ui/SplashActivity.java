package felixzhang.project.my_douban.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;

import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.util.UserUtils;

/**
 * Created by felix on 15/4/26.
 */
public class SplashActivity extends Activity {

    private static final long DEFAULT_ANIM_DELAY = 1500;
    private RelativeLayout mRl;
    private final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        mRl = (RelativeLayout) findViewById(R.id.rl_splash);

        // give a anim ,than go into other Activity
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.6f);
        anim.setDuration(DEFAULT_ANIM_DELAY);
        mRl.startAnimation(anim);

        // using handler duration some time , than exec task.
        new Handler().postDelayed(new LoadMainTabTask(), DEFAULT_ANIM_DELAY);

    }

    class LoadMainTabTask implements Runnable {

        @Override
        public void run() {
            Intent intent;
            if (UserUtils.isUserAuthoroized()) {
                intent = new Intent(SplashActivity.this,
                        MainActivity.class);
            } else {
                intent = new Intent(SplashActivity.this,
                        LoginActivity.class);
            }

            startActivity(intent);
            finish();
        }

    }


}
