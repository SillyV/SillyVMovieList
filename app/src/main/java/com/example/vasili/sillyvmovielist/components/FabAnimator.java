package com.example.vasili.sillyvmovielist.components;

import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.vasili.sillyvmovielist.R;

/**
 * Created by vasili on 12/15/2015.
 */
public class FabAnimator
{

    private final Animation jumpDownAnimation;

    public FabAnimator(Context context, final FloatingActionButton[] floatingActionMenuItems)
    {
        jumpDownAnimation = AnimationUtils.loadAnimation(context, R.anim.jump_to_down);
        jumpDownAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation)
            {
                for (FloatingActionButton fabItem : floatingActionMenuItems)
                {
                    fabItem.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation)
            {
            }

            @Override
            public void onAnimationStart(Animation animation)
            {
            }
        });
    }

    public Animation getMenuAnimationDown()
    {
        return jumpDownAnimation;
    }
}
