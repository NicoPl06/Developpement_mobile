package iut.dam.powerhome;

import android.content.Context;
import android.graphics.Color;

public class ColorManager {

    private static final String PREFS_NAME = "powerhome_prefs";
    private static final String KEY_COLOR_PREFIX  = "accent_color_user_";
    private static final int    DEFAULT    = Color.parseColor("#228B22");

    public static int getColor(Context ctx, int userId) {
        if (userId == -1) return DEFAULT;
        return ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getInt(KEY_COLOR_PREFIX + userId, DEFAULT);
    }

    public static void saveColor(Context ctx, int color, int userId) {
        if (userId == -1) return;
        ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putInt(KEY_COLOR_PREFIX + userId, color).apply();
    }
}