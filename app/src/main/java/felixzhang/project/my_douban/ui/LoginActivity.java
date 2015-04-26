package felixzhang.project.my_douban.ui;

import android.os.Bundle;

import felixzhang.project.my_douban.R;

/**
 * Created by felix on 15/4/26.
 */
public class LoginActivity extends BaseActivity {
//    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        actionBar.setIcon(R.drawable.ic_actionbar);

    }
}
