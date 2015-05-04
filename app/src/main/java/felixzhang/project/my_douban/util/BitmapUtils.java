package felixzhang.project.my_douban.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by storm on 14-6-17.
 * Created by felix on 15/4/26.
 */
public class BitmapUtils {
    public static Bitmap drawViewToBitmap(View view, int width, int height, int downSampling) {
        return drawViewToBitmap(view, width, height, 0f, 0f, downSampling);
    }

    public static Bitmap drawViewToBitmap(View view, int width, int height, float translateX,
                                          float translateY, int downSampling) {
        float scale = 1f / downSampling;
        int bmpWidth = (int) (width * scale - translateX / downSampling);
        int bmpHeight = (int) (height * scale - translateY / downSampling);
        Bitmap dest = Bitmap.createBitmap(bmpWidth, bmpHeight, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(dest);
        c.translate(-translateX / downSampling, -translateY / downSampling);
        if (downSampling > 1) {
            c.scale(scale, scale);
        }
        view.draw(c);
        return dest;
    }


    /**
     * 图片合成
     * 并在image上设置
     */
    public static void compose(Bitmap bitmap1, Bitmap bitmap2, ImageView iv) {
        Bitmap basebitmap;
        Canvas canvas;
        Paint paint;

        //根据选择的图片创建一个新图，但这个是个空图，只有大小和配置信息
        basebitmap = Bitmap.createBitmap(bitmap1.getWidth(), bitmap1.getHeight(), bitmap1.getConfig());

        canvas = new Canvas(basebitmap);
        paint = new Paint();
        canvas.drawBitmap(bitmap1, 0, 0, paint);

        //把第二张位图的内容 画在 basebitmap上
        try {
            //将画笔设置为可覆盖模式
            paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.MULTIPLY));
            canvas.drawBitmap(bitmap2, 0, 0, paint);
            iv.setImageBitmap(basebitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    // px、dip转换的工具类

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
