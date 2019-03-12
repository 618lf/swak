package com.swak.app.core.view.progressing.style;

import android.animation.ValueAnimator;

import com.swak.app.core.view.progressing.animation.SpriteAnimatorBuilder;
import com.swak.app.core.view.progressing.sprite.CircleSprite;
import com.swak.app.core.view.progressing.sprite.Sprite;
import com.swak.app.core.view.progressing.sprite.SpriteContainer;

/**
 * Created by ybq.
 * 两点上下晃动
 */
public class DoubleBounce extends SpriteContainer {

    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new Bounce(), new Bounce()
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        super.onChildCreated(sprites);
        sprites[1].setAnimationDelay(-1000);
    }

    private class Bounce extends CircleSprite {

        Bounce() {
            setAlpha(153);
            setScale(0f);
        }

        @Override
        public ValueAnimator onCreateAnimation() {
            float fractions[] = new float[]{0f, 0.5f, 1f};
            return new SpriteAnimatorBuilder(this).scale(fractions, 0f, 1f, 0f).
                    duration(2000).
                    easeInOut(fractions)
                    .build();
        }
    }
}
