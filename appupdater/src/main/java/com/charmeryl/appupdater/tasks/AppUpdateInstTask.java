package com.charmeryl.appupdater.tasks;

import android.content.Context;
import android.os.AsyncTask;

import com.charmeryl.appupdater.managers.UpdateManager;

/**
 * Created by jqjiang on 1/25/17.
 */

public class AppUpdateInstTask extends AsyncTask<Integer,Integer,Integer> {

    private Context context;
    private UpdateManager updateManager;

    public AppUpdateInstTask(Context context, UpdateManager updateManager) {
        this.context = context;
        this.updateManager = updateManager;
    }

    public AppUpdateInstTask setDownDir(String dir) {
        if (dir != null) {
            this.updateManager.setDownloadDir(dir);
        }
        return this;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(Integer... params) {
        updateManager.startUpdate();
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }
}
