package fr.ensisa.ensiblog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.util.Objects;

public class Utils {

    public static void showInfoBox(String title, String text, String PositiveText, Context context,
                                   DialogInterface.OnClickListener onPositiveListener) {
        showInfoBox(title,text,PositiveText,"",context,onPositiveListener,null);
    }

    public static void showInfoBox(String title, String text, String PositiveText, String NegativeText, Context context,
                                   DialogInterface.OnClickListener onPositiveListener, DialogInterface.OnClickListener onNegativeListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setMessage(text);

        builder.setTitle(title);

        builder.setCancelable(false);

        if(!Objects.equals(PositiveText, ""))
            builder.setPositiveButton(PositiveText, onPositiveListener);
        if(!Objects.equals(NegativeText, ""))
            builder.setNegativeButton(NegativeText, onNegativeListener);

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

}
