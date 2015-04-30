package felixzhang.project.my_douban.ui.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

/**
 * Created by felix on 15/4/30.
 */
public class CardsAnimationAdapter extends AnimationAdapter {


    private float mTranslationX = -400;
    private float mRotationY = 15;
    private long mDuration = 400;
    private long mDelay = 30;

    public CardsAnimationAdapter(BaseAdapter baseAdapter) {
        super(baseAdapter);
    }

    @Override
    protected long getAnimationDelayMillis() {
        return mDelay;
    }

    @Override
    protected long getAnimationDurationMillis() {
        return mDuration;
    }

    @Override
    public Animator[] getAnimators(ViewGroup viewGroup, View view) {
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "translationX", mTranslationX, 0),
                ObjectAnimator.ofFloat(view, "rotationY", mRotationY, 0)
        };
    }

}
