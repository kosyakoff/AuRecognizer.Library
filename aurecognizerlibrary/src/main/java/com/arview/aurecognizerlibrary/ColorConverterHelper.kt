package com.arview.aurecognizerlibrary

import android.graphics.*
import kotlin.math.max
import kotlin.math.min

 internal class ColorConverterHelper
{
    val MIN_BRIGHTNESS = -255;
    val MAX_BRIGHTNESS = 255;
    val MIN_CONTRAST : Float = 0f;
    val MAX_CONTRAST : Float = 10f;

    /*
    * https://stackoverflow.com/a/40548718
    * */
    fun ChangeBitmapContrastBrightness(bmp : Bitmap, contrast: Float, brightness: Float) : Bitmap
    {
        val cm : ColorMatrix = (ColorMatrix( floatArrayOf(
                contrast, 0f, 0f, 0f, brightness,
                0f, contrast, 0f, 0f, brightness,
                0f, 0f, contrast, 0f, brightness,
                0f, 0f, 0f, 1f, 0f
        )));

        val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config);
        val canvas = Canvas(ret);

        val paint = Paint();
        paint.colorFilter = ColorMatrixColorFilter(cm);
        canvas.drawBitmap(ret, 0f, 0f, paint);
        return ret;
    }

    fun TurnToGrayScale(bmp : Bitmap) : Bitmap
    {
        val cm = ColorMatrix( floatArrayOf(

            0.5f, 0.5f, 0.5f, 0f, 0f,
            0.5f, 0.5f, 0.5f, 0f, 0f,
            0.5f, 0.5f, 0.5f, 0f, 0f,
            0f, 0f, 0f, 1f, 0f
        ));

        val ret = Bitmap.createBitmap(bmp.width, bmp.height, bmp.config);

        val canvas = Canvas(ret);

        val paint = Paint();
        paint.colorFilter = ColorMatrixColorFilter(cm);
        canvas.drawBitmap(ret, 0f, 0f, paint);

        return ret;
    }

    /*
        https://geekymonkey.com/Programming/CSharp/RGB2HSL_HSL2RGB.htm
    */
    fun RGBToHSL(red : Int, green : Int, blue: Int,  hsl : FloatArray)
    {
        val r : Float = (red / 255f);
        val g : Float = (green / 255f);
        val b : Float = (blue / 255f);

        val min : Float = min(min(r, g), b);
        val max : Float = max(max(r, g), b);
        val delta : Float = max - min;

        var h : Float = 0f;
        var s : Float = 0f;
        val l : Float = ((max + min) / 2.0f);

        if (delta != 0f)
        {
            if (l < 0.5f)
            {
                s = (delta / (max + min));
            }
            else
            {
                s = (delta / (2.0f - max - min));
            }

            if (r == max)
            {
                h = (g - b) / delta;
            }
            else if (g == max)
            {
                h = 2f + (b - r) / delta;
            }
            else if (b == max)
            {
                h = 4f + (r - g) / delta;
            }
        }

        h = h * 60f;

        if (h < 0)
            h += 360;

        hsl[0] = h;
        hsl[1] = s;
        hsl[2] = l;
    }
}