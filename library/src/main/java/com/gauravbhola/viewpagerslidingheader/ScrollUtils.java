package com.gauravbhola.viewpagerslidingheader;

import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
 * Created by gauravbhola on 28/12/15.
 */
public final class ScrollUtils {
    private ScrollUtils() {
    }

    public static float getFloat(float value, float minValue, float maxValue) {
        return Math.min(maxValue, Math.max(minValue, value));
    }

    public static int getColorWithAlpha(float alpha, int baseColor) {
        int a = Math.min(255, Math.max(0, (int)(alpha * 255.0F))) << 24;
        int rgb = 16777215 & baseColor;
        return a + rgb;
    }

    public static void addOnGlobalLayoutListener(final View view, final Runnable runnable) {
        ViewTreeObserver vto = view.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                if(Build.VERSION.SDK_INT < 16) {
                    view.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                runnable.run();
            }
        });
    }

    public static int mixColors(int fromColor, int toColor, float toAlpha) {
        float[] fromCmyk = cmykFromRgb(fromColor);
        float[] toCmyk = cmykFromRgb(toColor);
        float[] result = new float[4];

        for(int i = 0; i < 4; ++i) {
            result[i] = Math.min(1.0F, fromCmyk[i] * (1.0F - toAlpha) + toCmyk[i] * toAlpha);
        }

        return -16777216 + (16777215 & rgbFromCmyk(result));
    }

    public static float[] cmykFromRgb(int rgbColor) {
        int red = (16711680 & rgbColor) >> 16;
        int green = ('\uff00' & rgbColor) >> 8;
        int blue = 255 & rgbColor;
        float black = Math.min(1.0F - (float)red / 255.0F, Math.min(1.0F - (float)green / 255.0F, 1.0F - (float)blue / 255.0F));
        float cyan = 1.0F;
        float magenta = 1.0F;
        float yellow = 1.0F;
        if(black != 1.0F) {
            cyan = (1.0F - (float)red / 255.0F - black) / (1.0F - black);
            magenta = (1.0F - (float)green / 255.0F - black) / (1.0F - black);
            yellow = (1.0F - (float)blue / 255.0F - black) / (1.0F - black);
        }

        return new float[]{cyan, magenta, yellow, black};
    }

    public static int rgbFromCmyk(float[] cmyk) {
        float cyan = cmyk[0];
        float magenta = cmyk[1];
        float yellow = cmyk[2];
        float black = cmyk[3];
        int red = (int)((1.0F - Math.min(1.0F, cyan * (1.0F - black) + black)) * 255.0F);
        int green = (int)((1.0F - Math.min(1.0F, magenta * (1.0F - black) + black)) * 255.0F);
        int blue = (int)((1.0F - Math.min(1.0F, yellow * (1.0F - black) + black)) * 255.0F);
        return ((255 & red) << 16) + ((255 & green) << 8) + (255 & blue);
    }
}
