/*
 * Copyright (C) 2016 Jared Rummler <jared.rummler@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.swak.app.model;


/**
 * Some SVGs to play with
 * SVG 动画 好看而已,暂时没用,根据实际情况来
 * 需要的话:
 * 可以自己去制作 可伸缩矢量图形
 * 或者
 * 更换其他动画库
 */
public enum ModelSVG {

    GOOGLE(
            new String[]{
                    "M142.9,24.2c40.2-13.9,85.3-13.6,125.3,1.1c22.2,8.2,42.5,21,59.9,37.1c-5.8,6.3-12.1,12.2-18.1,18.3 c-11.4,11.4-22.8,22.8-34.2,34.2c-11.3-10.8-25.1-19-40.1-23.6c-17.6-5.3-36.6-6.1-54.6-2.2c-21,4.5-40.5,15.5-55.6,30.9 c-12.2,12.3-21.4,27.5-27,43.9c-20.3-15.8-40.6-31.5-61-47.3C59,73.6,97.6,39.7,142.9,24.2z",
                    "M21.4,163.2c3.3-16.2,8.7-32,16.2-46.8c20.3,15.8,40.6,31.5,61,47.3c-8,23.3-8,49.2,0,72.4 c-20.3,15.8-40.6,31.6-60.9,47.3C18.9,246.7,13.2,203.6,21.4,163.2z",
                    "M203.7,165.1c58.3,0,116.7,0,175,0c5.8,32.7,4.5,66.8-4.7,98.8c-8.5,29.3-24.6,56.5-47.1,77.2 c-19.7-15.3-39.4-30.6-59.1-45.9c19.5-13.1,33.3-34.3,37.2-57.5c-33.8,0-67.6,0-101.4,0C203.7,213.5,203.7,189.3,203.7,165.1z",
                    "M37.5,283.5c20.3-15.7,40.6-31.5,60.9-47.3c7.8,22.9,22.8,43.2,42.6,57.1c12.4,8.7,26.6,14.9,41.4,17.9 c14.6,3,29.7,2.6,44.4,0.1c14.6-2.6,28.7-7.9,41-16.2c19.7,15.3,39.4,30.6,59.1,45.9c-21.3,19.7-48,33.1-76.2,39.6 c-31.2,7.1-64.2,7.3-95.2-1c-24.6-6.5-47.7-18.2-67.6-34.1C67,328.9,49.6,307.5,37.5,283.5z"
            },
            new int[]{
                    0xFFEA4335,
                    0xFFFBBC05,
                    0xFF4285F4,
                    0xFF34A853
            },
            400, 400
    );


    public final String[] glyphs;
    public final int[] colors;
    public final float width;
    public final float height;

    ModelSVG(String[] glyphs, int[] colors, float width, float height) {
        this.glyphs = glyphs;
        this.colors = colors;
        this.width = width;
        this.height = height;
    }

}
