package felixzhang.project.my_douban.model;

/**
 * Created by felix on 15/4/28.
 */
public class User {

    //在Prefs文件中保存的字段名
    public static final String USER_NAME = "douban_user_name";
    public static final String USER_ID = "douban_user_id";

    private String name;
    private String uid;
    private String avatar;          //小图的url
    private String large_avatar;    // 大图的url

    public User(String name, String uid, String avatar, String large_avatar) {
        this.name = name;
        this.uid = uid;
        this.avatar = avatar;
        this.large_avatar = large_avatar;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLarge_avatar() {
        return large_avatar;
    }

    public void setLarge_avatar(String large_avatar) {
        this.large_avatar = large_avatar;
    }
}
