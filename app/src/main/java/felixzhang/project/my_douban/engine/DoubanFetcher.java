package felixzhang.project.my_douban.engine;

import android.net.Uri;

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


 //        HttpGet  httpGet= new HttpGet(urlSpec);
//        // 创建一个浏览器
//        DefaultHttpClient client = new DefaultHttpClient();
//        HttpResponse response = client.execute(httpGet);
//        Log.i(TAG,"状态码："+response.getStatusLine().getStatusCode());

    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        try {
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return null;
            }
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


    public boolean login(String name, String pwd) throws IOException {
        String url = jointLoginUrl();

        String result = getUrl(url);



        Logger.i(TAG, "result" + result);

        return false;
    }

    /**
     * 拼接用户登录的url
     */
    private String jointLoginUrl() {
        /**url拼接例子：
         https://www.douban.com/service/auth2/auth?
         client_id=082e587080482c44188b095180d884cc&
         redirect_uri=http://douban.com/&
         response_type=code
         */
        String url = Uri.parse(DoubanApi.auth2_uri).buildUpon()
                .appendQueryParameter("client_id", DoubanApi.douban_apiKey)
                .appendQueryParameter("redirect_uri", DoubanApi.redirect_uri)
                .appendQueryParameter("response_type", DoubanApi.response_type_code)
                .build().toString();
        return url;
    }

}
