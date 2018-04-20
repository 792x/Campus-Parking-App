package com.tue.errorhandling;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.mert.testproj2.R;

/**
 * Views an error.
 */
public class ErrorViewer extends Activity {
    TextView errorMessageView;
    TextView actionMessageView;
    ImageButton iconView;
    String actionType;
    String sourceClassName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_error);

        errorMessageView = (TextView) findViewById(R.id.error_message);
        actionMessageView = (TextView) findViewById(R.id.action_message);
        iconView = (ImageButton) findViewById(R.id.error_icon);

        Intent intent = getIntent();
        sourceClassName = intent.getStringExtra("sourceClassName");
        String errorMessage = intent.getStringExtra("errorMessage");
        actionType = intent.getStringExtra("actionType");
        String actionMessage = intent.getStringExtra("actionMessage");
        int iconID = intent.getIntExtra("icon", 1);

        errorMessageView.setText(errorMessage);
        actionMessageView.setText(actionMessage);
        iconView.setImageResource(iconID);

        Log.e("ErrorViewer", "Source activity: " + sourceClassName);
        Log.e("ErrorViewer", "Error message: " + errorMessage);
        Log.e("ErrorViewer", "Action type: " + actionType);
        Log.e("ErrorViewer", "Action message: " + actionMessage);
        Log.e("ErrorViewer", "IconID:" + iconID);
    }


    public void takeAction(View view) {
        switch (actionType) {
            case "reload":
                reload(sourceClassName);
                break;
            case "restart":
                restart();
                break;
        }
    }

    private void restart() {
        Log.e("ErrorViewer", "Restarting the app");
        Intent intent = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    private void reload(String sourceName) {
        Class<?> className;
        if(sourceName != null) {
            try {
                className = Class.forName(sourceName );
                Intent intent = new Intent(this, className);
                startActivity(intent);
            } catch (ClassNotFoundException e) {
                ErrorHandler errorHandler = new ErrorHandler(this);
                errorHandler.handleError(2);
                e.printStackTrace();
            }
        }
    }

}
