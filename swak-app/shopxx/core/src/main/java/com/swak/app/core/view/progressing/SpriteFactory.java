package com.swak.app.core.view.progressing;

import com.swak.app.core.view.progressing.sprite.Sprite;
import com.swak.app.core.view.progressing.sprite.Sprite;
import com.swak.app.core.view.progressing.style.ChasingDots;
import com.swak.app.core.view.progressing.style.Circle;
import com.swak.app.core.view.progressing.style.CubeGrid;
import com.swak.app.core.view.progressing.style.DoubleBounce;
import com.swak.app.core.view.progressing.style.FadingCircle;
import com.swak.app.core.view.progressing.style.FoldingCube;
import com.swak.app.core.view.progressing.style.MultiplePulse;
import com.swak.app.core.view.progressing.style.MultiplePulseRing;
import com.swak.app.core.view.progressing.style.Pulse;
import com.swak.app.core.view.progressing.style.PulseRing;
import com.swak.app.core.view.progressing.style.ThreeBounce;
import com.swak.app.core.view.progressing.style.WanderingCubes;
import com.swak.app.core.view.progressing.style.Wave;

/**
 * Created by ybq.
 *   DOUBLE_BOUNCE 两点上下晃动
 *   WAVE 波浪
 *   WANDERING_CUBES 对角旋转正方体
 *   PULSE 脉冲
 *   CHASING_DOTS 点追逐
 *   THREE_BOUNCE 三点上下晃动
 *   CIRCLE 圆圈 菊花
 *   CUBE_GRID 正方体网格
 *   FADING_CIRCLE 衰退圆圈
 *   FOLDING_CUBE 折叠正方体
 *   MULTIPLE_PULSE 复杂脉冲
 *   PULSE_RING 环形脉冲
 *   MULTIPLE_PULSE_RING 复杂环形脉冲
 *
 */
public class SpriteFactory {

    public static Sprite create(Style style) {
        Sprite sprite = null;
        switch (style) {
            case THREE_BOUNCE:
                sprite = new ThreeBounce();
                break;
            case CHASING_DOTS:
                sprite = new ChasingDots();
                break;
            case CIRCLE:
                sprite = new Circle();
                break;
            case CUBE_GRID:
                sprite = new CubeGrid();
                break;
            case DOUBLE_BOUNCE:
                sprite = new DoubleBounce();
                break;
            case FADING_CIRCLE:
                sprite = new FadingCircle();
                break;
            case FOLDING_CUBE:
                sprite = new FoldingCube();
                break;
            case WAVE:
                sprite = new Wave();
                break;
            case PULSE:
                sprite = new Pulse();
                break;
            case MULTIPLE_PULSE:
                sprite = new MultiplePulse();
                break;
            case MULTIPLE_PULSE_RING:
                sprite = new MultiplePulseRing();
                break;
            case WANDERING_CUBES:
                sprite = new WanderingCubes();
                break;
            case PULSE_RING:
                sprite = new PulseRing();
                break;
            default:
                break;
        }
        return sprite;
    }
}
