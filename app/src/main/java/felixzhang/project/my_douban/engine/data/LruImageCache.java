package felixzhang.project.my_douban.engine.data;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by felix on 15/4/29.
 * <p/>
 * LRU图片缓存
 */
public class LruImageCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> lruCache;

    public LruImageCache() {
        int maxsize = 10 * 1024 * 1024;     //10M
        lruCache = new LruCache<String, Bitmap>(maxsize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        return lruCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        lruCache.put(url, bitmap);
    }
}
