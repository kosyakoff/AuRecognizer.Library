package com.arview.aurecognizerlibrary

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import com.googlecode.tesseract.android.TessBaseAPI

class AuCodeLibrary
{
    private val APPLIED_CONTRAST : Float = 0.99f;
    private val LIGHTNESS_LOW : Float = 0.08f;
    private val LIGHTNESS_HIGH : Float = 0.90f;
    private val SATURATION_HIGH : Float = 0.2f;

    private val  _tesseractApi = TessBaseAPI();

    private var _bitmapPixelArray: IntArray = IntArray(1);
    private var _currentBitmapPixelArray: IntArray = IntArray(1);
    private val _colorConverterHelper : ColorConverterHelper = ColorConverterHelper()

    /*
    * dataPath - path to tesseract data directory path
    * */
    fun Init(dataPath : String, lang : String) : Boolean
    {
        var init = false;
        try
        {
            init = _tesseractApi.init(dataPath, lang);
            _tesseractApi.setVariable("tessedit_char_blacklist", ":,.`~!@#$;%^?&*()-_+=|/<>}{]['…“№*+-¡©´·ˆˇˈˉˊˋˎˏ‘„‚.’—");
        }
        catch (e : Exception)
        {
            Log.d("AuCodeLibrary. Init Exception", e.message)
        }

        return init
    }

    fun RecognizeText(bitmap : Bitmap, textColorHues : ArrayList<HueRange>) : String
    {

        try
        {

            if (bitmap.width == 0 || bitmap.height == 0)
            {
                return ""
            }
            var h = bitmap.height;
            var w = bitmap.width;

            if (_bitmapPixelArray.size != w * h)
            {
                _bitmapPixelArray =  IntArray(w * h);
                _currentBitmapPixelArray = IntArray(w * h);
            }

            bitmap.getPixels(_bitmapPixelArray, 0, w, 0, 0, w, h);

            for (i in 0 until _currentBitmapPixelArray.size)
            {
                _currentBitmapPixelArray[i] = Color.WHITE;
            }
            var pixelColor: Int;
            var red: Int;
            var green: Int;
            var blue: Int;

            val hsl = FloatArray(3);

            for (j in 0 until h)
            {
                for (i in 0 until w)
                {
                    pixelColor = _bitmapPixelArray[j * w + i];

                    red = pixelColor.red;
                    green = pixelColor.green;
                    blue = pixelColor.blue

                    _colorConverterHelper.RGBToHSL(red, green, blue, hsl);

                    if (hsl[2] < LIGHTNESS_LOW || hsl[2] > LIGHTNESS_HIGH
                        || hsl[1] < SATURATION_HIGH)
                    {
                        continue;
                    }

                    for (referenceHue in textColorHues)
                    {
                        if (hsl[0] >= referenceHue.HueLow && hsl[0] <= referenceHue.HueHigh)
                        {
                            _currentBitmapPixelArray[j * w + i] = _bitmapPixelArray[j * w + i];
                            break;
                        }
                    }
                }
            }

            bitmap.setPixels(_currentBitmapPixelArray, 0, w, 0, 0, w, h);

        }
        catch (e : Exception)
        {
            Log.d("AuCodeLibrary. Recognize Exception", e.message)
        }

        //var bm = _colorConverterHelper.TurnToGrayScale(bitmap);
        //bm = _colorConverterHelper.ChangeBitmapContrastBrightness(bm, APPLIED_CONTRAST, 0f);

        val result : String = TesseractTextRecognizing(bitmap);

        return result
    }

    private fun TesseractTextRecognizing(croppedBitmap : Bitmap) : String
    {
        try
        {
            _tesseractApi.setImage(croppedBitmap);

            return  _tesseractApi.utF8Text
        }
        catch (e : Exception)
        {
            Log.d("AuCodeLibrary. Internal recognize Exception", e.message)
        }

        return "";
    }
}