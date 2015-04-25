package felixzhang.project.my_douban.model;

/**
 * Created by storm on 14-3-25.
 */
public enum Category {
    hot("Hot"), trending("Trending"), fresh("Fresh");  //自动调用构造方法
    private String mDisplayName;

    Category(String displayName) {
        mDisplayName = displayName;
    }

    public String getDisplayName() {
        return mDisplayName;
    }
}
