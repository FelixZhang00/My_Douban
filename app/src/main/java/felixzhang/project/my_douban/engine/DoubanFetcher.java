package felixzhang.project.my_douban.engine;

import android.net.Uri;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import felixzhang.project.my_douban.api.DoubanApi;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/4/26.
 */
public class DoubanFetcher {


    private static final String TAG = "DoubanFetcher";


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
            URL url1 = connection.getURL();
            Logger.i(TAG, "url1= " + url1.toString());
            InputStream in = connection.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int len = 0;

            byte[] buffer = new byte[1024];
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }

    }


    public String getUrl(String urlSpec) throws IOException {
        if (getUrlBytes(urlSpec) == null) {
            return null;
        }
        return new String(getUrlBytes(urlSpec));
    }


    public boolean login(String code) {
        /**
         https://www.douban.com/service/auth2/token?
         client_id=0b5405e19c58e4cc21fc11a4d50aae64&
         client_secret=edfc4e395ef93375&
         redirect_uri=https://www.example.com/back&
         grant_type=authorization_code&
         code=9b73a4248
         */

        String url = Uri.parse(DoubanApi.auth2_uri2).buildUpon()
                .appendQueryParameter("client_id", DoubanApi.douban_apiKey)
                .appendQueryParameter("client_secret", DoubanApi.douban_secret)
                .appendQueryParameter("redirect_uri", DoubanApi.redirect_uri)
                .appendQueryParameter("grant_type", DoubanApi.grant_type)
                .appendQueryParameter("code", code)
                .build().toString();

        HttpGet httpGet = new HttpGet(url);
        // 创建一个浏览器
        DefaultHttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        try {
            response = client.execute(httpGet);
            InputStream is = response.getEntity().getContent();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                bos.write(buffer, 0, len);
            }
            is.close();
            Logger.i(TAG, "token= " + new String(bos.toByteArray()));

            //保存获得的数据


            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 拼接用户登录的url
     * 为了在LoginActivity中的WebView
     * 拿到OAuth2的code参数
     */
    public String jointLoginUrl() {
        /**url拼接例子：
         https://www.douban.com/service/auth2/auth?client_id=082e587080482c44188b095180d884cc&redirect_uri=https://www.example.com/back/&response_type=code
         */
        String url = Uri.parse(DoubanApi.auth2_uri).buildUpon()
                .appendQueryParameter("client_id", DoubanApi.douban_apiKey)
                .appendQueryParameter("redirect_uri", DoubanApi.redirect_uri)
                .appendQueryParameter("response_type", DoubanApi.response_type_code)
                .build().toString();
        return url;
    }

}
