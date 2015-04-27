package felixzhang.project.my_douban.engine;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import felixzhang.project.my_douban.util.Logger;


/**
 * Created by felix on 15/4/27.
 * 从网络加载图片的线程
 */
public class ThumbnailDownLoader<Token> extends HandlerThread{

    private static final String TAG = "ThumbnailDownLoader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private  Handler mHandler;
    private Handler mResponseHandler;

    private Map<Token, String> requestMap = Collections
            .synchronizedMap(new HashMap<Token, String>());


    private Listener<Token> mListener;

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }

    public ThumbnailDownLoader(Handler responseHandler) {
        super(TAG);
        mResponseHandler=responseHandler;
    }

    /**
     * 在loorer工作之前的初始化设置
     */
    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) { // 从信息队列中不断不断拿出消息来处理
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    handleRequest(token);
                }
                super.handleMessage(msg);
            }

        };
        super.onLooperPrepared();
    }


    public void queueThumbnail(Token token, String url) {
        Logger.i(TAG, "url=" + url);
        requestMap.put(token, url);
        mHandler.obtainMessage(MESSAGE_DOWNLOAD, token).sendToTarget();
    }


    private void handleRequest(final Token token) {
        final String url = requestMap.get(token);
        if (url == null) {
            return;
        }
        try {
            byte[] bytes = new DoubanFetcher().getUrlBytes(url); // 执行这步操作时会阻塞

            final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);
            Logger.i(TAG, "download thumbnail success.");

            mResponseHandler.post(new Runnable() {

                @Override
                public void run() {
                    if (requestMap.get(token) != url) {
                        return;
                    }
                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, bitmap);
                }
            });

        } catch (IOException e) {
            Logger.i(TAG, "download thumbnail failed!");
            e.printStackTrace();
        }

    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }

}
