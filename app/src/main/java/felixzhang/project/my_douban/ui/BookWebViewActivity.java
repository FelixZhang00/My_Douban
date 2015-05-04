package felixzhang.project.my_douban.ui;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.util.CommUtils;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/5/4.
 */
public class BookWebViewActivity extends BaseActivity {

    private static final String TAG = BookWebViewActivity.class.getSimpleName();
    public static final String EXTRA_BOOK_ALT = "felixzhang.project.my_douban.EXTRA_BOOK_ALT";
    private WebView mWebView;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_webview);
        actionBar.setIcon(R.drawable.ic_actionbar);

        mWebView = (WebView) findViewById(R.id.webview);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);

        //使webview提供交互
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new WebViewClient() {

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


        loadUrl();


    }

    private void loadUrl() {
        String alturl = getIntent().getStringExtra(EXTRA_BOOK_ALT);
        Logger.i(TAG,"bookurl = "+alturl);
        if (CommUtils.isValidUrl(alturl)) {
            mWebView.loadUrl(alturl);
        }
    }


}
