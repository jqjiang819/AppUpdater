package com.charmeryl.appupdater;

import android.content.Context;
import android.view.View;

import com.charmeryl.appupdater.tasks.AppUpdateTask;

/**
 * Created by jqjiang on 12/29/16.
 */

public class AppUpdater {
    public static final int AUTOMATIC = 0;
    public static final int MANUALLY = 1;

    private Context context;
    private AppUpdateTask updateTask;

    public AppUpdater(Context context){
        this.context = context;
        this.updateTask = new AppUpdateTask(context);
    }

    public void start(){
        this.updateTask.execute();
    }

    public AppUpdater setDownDir(String dir){
        this.updateTask.setDownDir(dir);
        return this;
    }
    public AppUpdater setUpdateUrl(String Url){
        this.updateTask.setUpdateUrl(Url);
        return this;
    }

    public AppUpdater setTriggerMethod(int method){
        this.updateTask.setTriggerMethod(method);
        return this;
    }

    public AppUpdater setSnackbarView(View view){
        this.updateTask.setSnackbarView(view);
        return this;
    }
}
