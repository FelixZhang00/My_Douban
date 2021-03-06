package felixzhang.project.my_douban.ui;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.engine.DoubanFetcher;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/4/26.
 * <p/>
 * 为webview增加加载监听事件
 */
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
//    UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");

    private WebView mWebView;
    private ProgressBar mProgressBar;
    private DoubanFetcher mFetcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_webview);
        actionBar.setIcon(R.drawable.ic_actionbar);


        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        mFetcher = new DoubanFetcher();


        String url = mFetcher.jointLoginUrl();

        //使webview提供交互
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Logger.i(TAG, "url = " + url);

                int index_code = url.indexOf("?code=");
                if (index_code == -1) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
                } else {
                    String code = url.substring(index_code + 6);
                    Logger.i(TAG, " code=" + code);

                    new FetcherTask().execute(code);
                }

                //拦截在当前url基础上加载的新url，
                //也就是说webview不会加载新的url
                //这样我就可以拿到code了
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }
        });

        mWebView.loadUrl(url);

    }


    private void leap() {
        finish();
    }

    class FetcherTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String code = params[0];
            boolean isLoginSuccess = mFetcher.login(code);
            Logger.i(TAG, "isLoginSuccess = " + isLoginSuccess);
            return isLoginSuccess;
        }

        @Override
        protected void onPostExecute(Boolean isLoginSuccess) {
            super.onPostExecute(isLoginSuccess);
            mProgressBar.setVisibility(View.GONE);

            if (isLoginSuccess) {
                Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK);
                leap();
            } else {
                Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_SHORT).show();
            }

        }
    }


}
