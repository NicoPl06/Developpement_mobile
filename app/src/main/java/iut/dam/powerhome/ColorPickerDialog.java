package iut.dam.powerhome;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ColorPickerDialog extends DialogFragment {

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }

    private OnColorSelectedListener listener;

    // Palette prédéfinie : nom + valeur hex
    private static final String[] NAMES = {
            "Forêt",  "Océan",  "Violet",  "Ardoise", "Rubis",
            "Indigo",  "Teal",   "Ambre",   "Corail",  "Graphite"
    };
    private static final String[] COLORS = {
            "#228B22", "#1565C0", "#6A1B9A", "#37474F", "#C62828",
            "#283593", "#00695C", "#E65100", "#AD1457", "#424242"
    };

    public static ColorPickerDialog newInstance(OnColorSelectedListener l) {
        ColorPickerDialog d = new ColorPickerDialog();
        d.listener = l;
        return d;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context ctx = requireContext();
        View root = LayoutInflater.from(ctx).inflate(R.layout.dialog_color_picker, null);

        // Grille de couleurs
        android.widget.GridLayout grid = root.findViewById(R.id.colorGrid);
        View preview = root.findViewById(R.id.colorPreview);
        TextView tvName = root.findViewById(R.id.tvColorName);

        // Couleur courante
        int currentColor = ColorManager.getColor(ctx);
        preview.setBackgroundColor(currentColor);

        // Trouver le nom de la couleur courante
        for (int i = 0; i < COLORS.length; i++) {
            if (Color.parseColor(COLORS[i]) == currentColor) {
                tvName.setText(NAMES[i]);
                break;
            }
        }

        final int[] selected = { currentColor };

        for (int i = 0; i < COLORS.length; i++) {
            final int color  = Color.parseColor(COLORS[i]);
            final String name = NAMES[i];

            View swatch = new View(ctx);
            android.widget.GridLayout.LayoutParams lp =
                    new android.widget.GridLayout.LayoutParams();
            lp.width  = dpToPx(ctx, 48);
            lp.height = dpToPx(ctx, 48);
            lp.setMargins(dpToPx(ctx, 6), dpToPx(ctx, 6),
                    dpToPx(ctx, 6), dpToPx(ctx, 6));
            swatch.setLayoutParams(lp);

            // Cercle coloré
            android.graphics.drawable.GradientDrawable circle =
                    new android.graphics.drawable.GradientDrawable();
            circle.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            circle.setColor(color);

            // Bordure si c'est la couleur active
            if (color == currentColor) {
                circle.setStroke(dpToPx(ctx, 3), Color.WHITE);
                swatch.setAlpha(1f);
            } else {
                swatch.setAlpha(0.75f);
            }
            swatch.setBackground(circle);

            swatch.setOnClickListener(v -> {
                selected[0] = color;
                preview.setBackgroundColor(color);
                tvName.setText(name);

                // Reset toutes les bordures
                for (int j = 0; j < grid.getChildCount(); j++) {
                    View sw = grid.getChildAt(j);
                    android.graphics.drawable.GradientDrawable bg =
                            (android.graphics.drawable.GradientDrawable) sw.getBackground();
                    int swColor = Color.parseColor(COLORS[j]);
                    bg.setStroke(swColor == color ? dpToPx(ctx, 3) : 0,
                            Color.WHITE);
                    sw.setAlpha(swColor == color ? 1f : 0.75f);
                    sw.setBackground(bg);
                }
            });

            grid.addView(swatch);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(ctx)
                .setView(root)
                .setTitle("Couleur d'accentuation")
                .setPositiveButton("Appliquer", (dialog, which) -> {
                    if (listener != null) listener.onColorSelected(selected[0]);
                })
                .setNegativeButton("Annuler", null);

        return builder.create();
    }

    private static int dpToPx(Context ctx, int dp) {
        return Math.round(dp * ctx.getResources().getDisplayMetrics().density);
    }
}