package com.tue.errorhandling;

import android.app.Application;

import com.tue.errorhandling.ErrorHandler;

/**
 * Handles unhandled exceptions
 */
public class ExceptionHandler extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable e) {
                handleUncaughtException(thread, e);
            }
        });
    }

    private void handleUncaughtException(Thread thread, Throwable e) {
        ErrorHandler errorHandler = new ErrorHandler(this);
        errorHandler.handleError(1);
    }
}
