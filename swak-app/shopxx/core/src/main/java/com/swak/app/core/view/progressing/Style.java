package com.swak.app.core.view.progressing;

/**
 * Created by ybq.
 *
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
 *
 */
public enum Style {
    DOUBLE_BOUNCE(0),
    WAVE(1),
    WANDERING_CUBES(2),
    PULSE(3),
    CHASING_DOTS(4),
    THREE_BOUNCE(5),
    CIRCLE(6),
    CUBE_GRID(7),
    FADING_CIRCLE(8),
    FOLDING_CUBE(9),
    MULTIPLE_PULSE(10),
    PULSE_RING(11),
    MULTIPLE_PULSE_RING(12);

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private int value;

    Style(int value) {
        this.value = value;
    }
}
