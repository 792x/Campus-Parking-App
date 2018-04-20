package com.tue.errorhandling;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.mert.testproj2.R;

/**
 * Error handler has to be created inside a class:
 * Add this beneath class declaration:
 *
 * "ErrorHandler errorHandler = new ErrorHandler(this);"
 */
public class ErrorHandler {
    private Context context;

    public ErrorHandler(Context current) {
        this.context = current;
    }

    public void handleError(int errorID) {
        ((Activity)context).finish();

        // Get the source in the error.xml -file
        int source;
        switch (errorID) {
            case 1:
                source = R.array.error_1;
                break;
            case 2:
                source = R.array.error_2; // Class not found
                break;
            case 404:
                source = R.array.error_404;
                break;
            default:
                source = R.array.error_999; // Error when error isn't handled
        }

        String[] error = context.getResources().getStringArray(source);

        String errorMessage = error[0];
        String actionType = error[1];
        String actionMessage = error[2];
        String[] icon_path = error[3].split("/");
        String icon = icon_path[icon_path.length-1].replace(".xml", "").trim();
        int iconID = context.getResources().getIdentifier(icon, "drawable", context.getPackageName());

        Intent intent = new Intent(context, ErrorViewer.class);

        String sourceClassName = context.getClass().getName();

        intent.putExtra("sourceClassName", sourceClassName);
        intent.putExtra("errorMessage", errorMessage);
        intent.putExtra("actionType", actionType);
        intent.putExtra("actionMessage", actionMessage);
        intent.putExtra("icon", iconID);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        context.startActivity(intent);
    }

}


