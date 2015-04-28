package felixzhang.project.my_douban.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.gson.Gson;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.Source;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import felixzhang.project.my_douban.MyApp;
import felixzhang.project.my_douban.R;
import felixzhang.project.my_douban.api.DoubanApi;
import felixzhang.project.my_douban.dao.DBHelper;
import felixzhang.project.my_douban.model.NewBook;
import felixzhang.project.my_douban.model.TokenBean;
import felixzhang.project.my_douban.util.Logger;

/**
 * Created by felix on 15/4/26.
 */
public class DoubanFetcher {


    private static final String TAG = "DoubanFetcher";
    private static final String XML_RATING = "gd:rating";  //xml中表示评分的节点名称


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


    /**
     * 拿code换token
     *
     * @param code
     * @return
     */
    public boolean login(String code) {
        /**
         https://www.douban.com/service/auth2/token?
         client_id=0b5405e19c58e4cc21fc11a4d50aae64&
         client_secret=edfc4e395ef93375&
         redirect_uri=https://www.example.com/back&
         grant_type_code=authorization_code&
         code=9b73a4248
         */

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(DoubanApi.auth2_uri2);
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("client_id", DoubanApi.douban_apiKey));
        params.add(new BasicNameValuePair("client_secret", DoubanApi.douban_secret));
        params.add(new BasicNameValuePair("redirect_uri", DoubanApi.redirect_uri));
        params.add(new BasicNameValuePair("grant_type", DoubanApi.grant_type_code));
        params.add(new BasicNameValuePair("code", code));


        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "utf-8");
            httpPost.setEntity(entity);
            HttpResponse response = httpClient.execute(httpPost);
            Logger.i(TAG, "StatusCode() = " + response.getStatusLine().getStatusCode());
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity httpEntity = response.getEntity();
                String result = EntityUtils.toString(httpEntity, "utf-8");

                Logger.i(TAG, "token = " + result);

                Gson gson = new Gson();
                TokenBean tokenBean = gson.fromJson(result, TokenBean.class);

                //保存数据到配置文件中
                SharedPreferences sp = MyApp.getContext().getSharedPreferences("config",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("access_token", tokenBean.getAccess_token());
                editor.putString("douban_user_name", tokenBean.getDouban_user_name());
                editor.putString("douban_user_id", tokenBean.getDouban_user_id());
                editor.putString("expires_in", tokenBean.getExpires_in());
                editor.putString("refresh_token", tokenBean.getRefresh_token());
                editor.commit();
                Logger.i(TAG, tokenBean.toString());

            } else {
                return false;
            }

            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
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


    public List<NewBook> getNewBooks(Context context) throws Exception {
        DBHelper dbHelper = new DBHelper(context);

        String newbookurl = context.getResources().getString(
                R.string.newbookurl);
        URL url = new URL(newbookurl);

        URLConnection conn = url.openConnection();
        Source source = new Source(conn);
        List<Element> elements = source.getAllElements("li");
        System.out.println(elements.size());


        List<NewBook> newBooks = new ArrayList<NewBook>();
        for (Element element : elements) {
            List<Element> childElements = element.getChildElements();
            if (childElements.size() == 2) {
                if ("detail-frame".equals(childElements.get(0)
                        .getAttributeValue("class"))) {
                    NewBook newBook = new NewBook();
                    Element divElement = childElements.get(0);
                    List<Element> divlists = divElement.getChildElements();

                    String title = divlists.get(0).getTextExtractor()
                            .toString();
                    newBook.setTitle(title);
                    String desc = divlists.get(1).getTextExtractor().toString();
                    newBook.setDescription(desc);
                    String summary = divlists.get(2).getTextExtractor()
                            .toString();
                    newBook.setSummary(summary);

                    Element imgElement = childElements.get(1);
                    String idhref = imgElement.getAttributeValue("href");

                    // 这是要分析的正则表达式 <a
                    // href="http://book.douban.com/subject/25942487/">

                    String regex = "\\d+"; // 正则表达式
                    Pattern p = Pattern.compile(regex);

                    Matcher m = p.matcher(idhref);
                    String id = null;
                    while (m.find()) {
                        id = m.group();
                    }
                    System.out.println("id---" + id);

                    String detialurl = "http://api.douban.com/v2/book/" + id + "?apikey=" + DoubanApi.douban_apiKey;
                    newBook.setId(id);

                    String imgurl = imgElement.getChildElements().get(0)
                            .getAttributeValue("src");
                    newBook.setImgurl(imgurl);

                    //由于豆瓣对访问有限制，detialurl暂不使用
                    //解析此ulr对应的json数据，从中获取评分信息和图片url
//                    parseRatingAndImg(newBook, detialurl);

                    newBooks.add(newBook);

                    dbHelper.insertNewBook(newBook);
                }

            }

        }
        return newBooks;

    }

    /**
     * 解析此ulr对应的json数据，从中获取评分信息和图片url
     */
    private void parseRatingAndImg(NewBook newBook, String detialurl) throws XmlPullParserException, IOException {
        String json = getUrl(detialurl);
        Gson gson = new Gson();
        JsonTemp jsonTemp = gson.fromJson(json, JsonTemp.class);
        newBook.setRating(Double.valueOf(jsonTemp.rating.average));
        newBook.setImgurl(jsonTemp.image);
    }

    /**
     * 为了解析json数据，临时定义的对象
     */
    class JsonTemp {

        class Rating {
            String average;
        }

        Rating rating;
        String image;

    }

}
