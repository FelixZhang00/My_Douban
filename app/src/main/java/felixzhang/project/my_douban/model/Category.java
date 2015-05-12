package felixzhang.project.my_douban.model;

/**
 * Created by storm on 14-3-25.
 */
public enum Category {
    // , myread("我读"),mynote("我的日记")

     myinfo("我的资料"),newbook("新书"), searchbook("搜索图书");  //自动调用构造方法
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
