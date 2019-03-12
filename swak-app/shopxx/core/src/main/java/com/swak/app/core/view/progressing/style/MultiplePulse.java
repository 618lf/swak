package com.swak.app.core.view.progressing.style;

import com.swak.app.core.view.progressing.sprite.Sprite;
import com.swak.app.core.view.progressing.sprite.SpriteContainer;

/**
 * Created by ybq.
 * 复杂脉冲
 */
public class MultiplePulse extends SpriteContainer {
    @Override
    public Sprite[] onCreateChild() {
        return new Sprite[]{
                new Pulse(),
                new Pulse(),
                new Pulse(),
        };
    }

    @Override
    public void onChildCreated(Sprite... sprites) {
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].setAnimationDelay(200 * (i + 1));
        }
    }
}
