package com.naren.kagga;

import android.os.Build;
import android.text.Html;
import android.text.Spanned;

/**
 * Created by narensmac on 15/03/18.
 */

public class TextUtils {

    public static Spanned getText(String text){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }
}
