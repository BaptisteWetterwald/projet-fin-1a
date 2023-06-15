package fr.ensisa.ensiblog;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

/**
 * some tools used in activity file
 **/
public class Utils {

    /**
     * Used to display a tooltip in pages of the application with only one option to close the tooltip
     * @param title : tooltip title
     * @param text : principal text of the tooltip
     * @param PositiveText : button with Positive answer to the tooltip
     * @param context : page where to display the tooltip
     * @param onPositiveListener : Listener on the PositiveText button
     **/
    public static void showInfoBox(String title, String text, String PositiveText, Context context,
                                   DialogInterface.OnClickListener onPositiveListener) {
        showInfoBox(title,text,PositiveText,"",context,onPositiveListener,null);
    }

    /**
     * Used to display a tooltip in pages of the application with two options to close the tooltip
     * @param title : tooltip title
     * @param text : principal text of the tooltip
     * @param PositiveText : button with Positive answer to the tooltip
     * @param NegativeText : button with Negative answer to the tooltip
     * @param context : page where to display the tooltip
     * @param onPositiveListener : Listener on the PositiveText button
     * @param onNegativeListener : Listener on the NegativeText button
     **/
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

    /**
     * Allow to remove an element from a view
     * @param element : the element to remove
     **/
    public static void removeElement(View element){
        ViewGroup layout = (ViewGroup) element.getParent();
        if(null!=layout)
            layout.removeView(element);
    }


    /**
     * traduce an unusable Uri to a usable Path
     @param uri : the unusable uri
     **/
    public static String getFilePathFromUri(ContentResolver resolver, Uri uri) {
        String filePath = null;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = resolver.query(uri, projection, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            filePath = cursor.getString(columnIndex);
            cursor.close();
        }
        return filePath;
    }

}
