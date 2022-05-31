package yash.com.miniproject.dherya.enums;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;

import yash.com.miniproject.dherya.R;

/**
 * Created by Alex on 30/8/2016.
 * This enum lists background drawables (for credit cards, for example)
 * which internally are either actual drawables or simple colors
 */
public enum CreditCardBackground {
    GOLD(R.drawable.cc_background_gold, R.color.cc_foreground_gold,  R.color.cc_text_black),
    LIGHT_GRAY(R.drawable.cc_background_light_gray, R.color.cc_foreground_light_gray,  R.color.cc_text_white),
    GREEN(R.drawable.cc_background_dark_green, R.color.cc_foreground_green,  R.color.cc_text_white),
    DARK_RED(R.drawable.cc_background_dark_red, R.color.cc_foreground_red, R.color.cc_text_white),
    RED(R.drawable.cc_background_red, R.color.cc_foreground_red, R.color.cc_text_white),
    ORANGE(R.drawable.cc_background_orange, R.color.cc_foreground_orange,  R.color.cc_text_white),
    LIGHT_BLUE(R.drawable.cc_background_light_blue, R.color.cc_foreground_light_blue,  R.color.cc_text_white);


    int backgroundResId;
    int foregroundColorId;
    int textColorId;

    CreditCardBackground(@DrawableRes int backgroundResId, @ColorRes int foregroundColorId, @ColorRes int textColorId) {
        this.backgroundResId = backgroundResId;
        this.foregroundColorId = foregroundColorId;
        this.textColorId = textColorId;
    }


    public int getBackgroundResId() {
        return backgroundResId;
    }

    public int getForegroundColorId() {
        return foregroundColorId;
    }

    public int getTextColorId() {
        return textColorId;
    }

    public String getCode(){
        return this.name();
    }


    public Drawable getBackgroundDrawable(Context context) throws Resources.NotFoundException {

        Drawable backgroundDrawable = ContextCompat.getDrawable(context, backgroundResId);
        if(foregroundColorId != -1) {
            LayerDrawable ld = (LayerDrawable) backgroundDrawable;
            Drawable drawable = ld.getDrawable(1);
            drawable.mutate().setColorFilter(getForegroundColor(context), PorterDuff.Mode.MULTIPLY);
        }
        return backgroundDrawable;
    }

    public int getForegroundColor(Context context) throws Resources.NotFoundException {
        return ContextCompat.getColor(context, foregroundColorId);
    }

    public int getTextColor(Context context) throws Resources.NotFoundException {
        return ContextCompat.getColor(context, textColorId);
    }
}
