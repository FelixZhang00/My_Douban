package felixzhang.project.my_douban.view.roundImage;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by felix on 15/4/28.
 */
public class RoundImageView extends ImageView{

    public RoundImageView(Context context) {
        super(context);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private RadialGradient gradient;
    private Paint paint;
    private void init() {
//        RadialGradient gradient = new RadialGradient(j/2,k/2,j/2,new int[]{0xff5d5d5d,0xff5d5d5d,0x00ffffff},new float[]{0.f,0.8f,1.0f}, Shader.TileMode.CLAMP);
//        paint.setShader(gradient);
//        canvas.drawCircle(j/2,k/2,j/2,paint);
    }
}
