package xyz.zzyitj.qbremote.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.TypedValue;
import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

public class ColorUtils {

    @ColorInt
    public static int resolveColor(Context context, int colorAttr, int defaultResId) {
        TypedValue typedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(colorAttr, typedValue, true);
        if (resolved) {
            if (typedValue.type == TypedValue.TYPE_STRING) {
                ColorStateList stateList = ContextCompat.getColorStateList(context, typedValue.resourceId);
                if (stateList != null) return stateList.getDefaultColor();
            } else {
                return typedValue.data;
            }
        }

        return ContextCompat.getColor(context, defaultResId);
    }
}
