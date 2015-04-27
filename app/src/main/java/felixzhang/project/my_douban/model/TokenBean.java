package felixzhang.project.my_douban.model;

/**
 * Created by felix on 15/4/27.
 * 封装OAuth2认证返回的结果
 */
public class TokenBean extends BaseModel {
    private String access_token;
    private String expires_in;
    private String refresh_token;
    private String douban_user_id;
    private String douban_user_name;

    public TokenBean(String access_token, String expires_in, String refresh_token, String douban_user_id) {
        this.access_token = access_token;
        this.expires_in = expires_in;
        this.refresh_token = refresh_token;
        this.douban_user_id = douban_user_id;
    }

    public TokenBean() {
    }

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getDouban_user_id() {
        return douban_user_id;
    }

    public void setDouban_user_id(String douban_user_id) {
        this.douban_user_id = douban_user_id;
    }

    public String getDouban_user_name() {
        return douban_user_name;
    }

    public void setDouban_user_name(String douban_user_name) {
        this.douban_user_name = douban_user_name;
    }

    @Override
    public String toString() {
        return "TokenBean{" +
                "access_token='" + access_token + '\'' +
                ", expires_in='" + expires_in + '\'' +
                ", refresh_token='" + refresh_token + '\'' +
                ", douban_user_id='" + douban_user_id + '\'' +
                ", douban_user_name='" + douban_user_name + '\'' +
                '}';
    }
}
