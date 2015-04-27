package felixzhang.project.my_douban.api;

/**
 * Created by felix on 15/4/26.
 * <p/>
 * 豆瓣api
 * http://developers.douban.com/wiki/?title=oauth2#limits
 */
public class DoubanApi {

    public static final String douban_apiKey = "082e587080482c44188b095180d884cc";
    public static final String douban_secret = "9710e6749a71c153";
    public static final String douban_appname = "Demo_my_douban";


    /**
     * **获取authorization_code**
     */
    // 用户授权完成后的回调地址，应用需要通过此回调地址获得用户的授权结果。
    // 此地址必须与在应用注册时填写的回调地址一致.
    //一个字符都不能差
    public static final String redirect_uri = "https://www.example.com/back/";
    public static final String auth2_uri = "https://www.douban.com/service/auth2/auth";
    public static final String response_type_code = "code";

    public static final String auth2_uri2 = "https://www.douban.com/service/auth2/token";
    public static final String grant_type_code ="authorization_code";






}
