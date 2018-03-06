package com.xt.dbbook.tools;

import android.content.Context;

/**
 * Created by xt on 2018/01/29.
 */

public class DipPixelUtil {

    public static int dip2Pixel(Context context, float dip) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    public static int pixel2Dip(Context context, float pixel) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pixel / scale + 0.5f);
    }
}
