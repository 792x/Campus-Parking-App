package com.tue.tools;

import android.os.AsyncTask;

/**
 * Created by Mert on 30-3-2016.
 */
public class AndroidTask<K> extends AsyncTask<K, Void, K[]> {


    Handler<K> handler;

    public AndroidTask(Handler handler) {
        this.handler = handler;
    }

    @Override
    protected K[] doInBackground(K... params) {
        handler.handle(params);
        return params;
    }
}
