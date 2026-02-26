package iut.dam.powerhome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

public class ColorManager {

    private static final String PREFS_NAME = "powerhome_prefs";
    private static final String KEY_COLOR  = "accent_color";
    private static final int    DEFAULT    = Color.parseColor("#228B22");

    public static int getColor(Context ctx) {
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_COLOR, DEFAULT);
    }

    public static void saveColor(Context ctx, int color) {
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putInt(KEY_COLOR, color).apply();
    }
}