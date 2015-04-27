package felixzhang.project.my_douban.model;

/**
 * Created by storm on 14-3-25.
 */
public enum Category {
    newbook("新书"), myinfo("我的资料"), myread("我读"),mynote("我的日记");  //自动调用构造方法
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
