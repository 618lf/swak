package com.veni.tools.view.progressing.style;

import com.veni.tools.view.progressing.sprite.Sprite;
import com.veni.tools.view.progressing.sprite.SpriteContainer;

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
