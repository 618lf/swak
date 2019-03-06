package com.veni.tools.view.popupwindows.tools;

import android.view.View;

public class Coordinates {

    int left;
    int top;
    int right;
    int bottom;

    public Coordinates(View view){
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        left = location[0];
        right = left + view.getWidth();
        top = location[1];
        bottom = top + view.getHeight();
    }


}
