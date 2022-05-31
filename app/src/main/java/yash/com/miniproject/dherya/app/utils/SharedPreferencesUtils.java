package yash.com.miniproject.dherya.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

import yash.com.miniproject.dherya.exceptions.SharedPreferenceNotFoundException;

/**
 * Created by Alex on 31/8/2016.
 */
public class SharedPreferencesUtils {

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static void checkIfPreferenceExists(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        if(!getSharedPreferences(context).contains(preferenceName))
            throw new SharedPreferenceNotFoundException(preferenceName + " does not exist");
    }

    public static void setString(Context context, String preferenceName, String value) {
        getSharedPreferences(context).edit().putString(preferenceName, value).commit();
    }
    public static void setInt(Context context, String preferenceName, int value) {
        getSharedPreferences(context).edit().putInt(preferenceName, value).commit();
    }
    public static void setBoolean(Context context, String preferenceName, boolean value) {
        getSharedPreferences(context).edit().putBoolean(preferenceName, value).commit();
    }
    public static void setLong(Context context, String preferenceName, long value) {
        getSharedPreferences(context).edit().putLong(preferenceName, value).commit();
    }
    public static void setFloat(Context context, String preferenceName, float value) {
        getSharedPreferences(context).edit().putFloat(preferenceName, value).commit();
    }

    public static String getString(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        checkIfPreferenceExists(context, preferenceName);
        return getSharedPreferences(context).getString(preferenceName, "");
    }
    public static int getInt(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        checkIfPreferenceExists(context, preferenceName);
        return getSharedPreferences(context).getInt(preferenceName, 0);
    }
    public static boolean getBoolean(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        checkIfPreferenceExists(context, preferenceName);
        return getSharedPreferences(context).getBoolean(preferenceName, false);
    }
    public static long getLong(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        checkIfPreferenceExists(context, preferenceName);
        return getSharedPreferences(context).getLong(preferenceName, 0);
    }
    public static float getFloat(Context context, String preferenceName) throws SharedPreferenceNotFoundException {
        checkIfPreferenceExists(context, preferenceName);
        return getSharedPreferences(context).getFloat(preferenceName, 0);
    }

}
