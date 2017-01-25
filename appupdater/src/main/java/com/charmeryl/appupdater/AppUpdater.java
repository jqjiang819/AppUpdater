package com.charmeryl.appupdater;

import android.content.Context;
import android.view.View;

import com.charmeryl.appupdater.tasks.AppUpdateCheckTask;

/**
 * Created by jqjiang on 12/29/16.
 */

public class AppUpdater {
    public static class TriggerMethod {
        public static final int AUTOMATIC = 0;
        public static final int MANUALLY = 1;
    }

    private Context context;
    private String downDir;
    private String updateUrl;
    private int triggerMethod;
    private View snackbarView;

    public AppUpdater(Context context){
        this.context = context;
    }

    public AppUpdater setDownDir(String dir){
        this.downDir = dir;
        return this;
    }
    public AppUpdater setUpdateUrl(String url){
        this.updateUrl = url;
        return this;
    }

    public AppUpdater setTriggerMethod(int method){
        switch (method) {
            default:
            case TriggerMethod.MANUALLY:
                this.triggerMethod = TriggerMethod.MANUALLY;
                break;
            case TriggerMethod.AUTOMATIC:
                this.triggerMethod = TriggerMethod.AUTOMATIC;
                break;
        }
        return this;
    }

    public AppUpdater setSnackbarView(View view){
        this.snackbarView = view;
        return this;
    }


    public void start(){
        AppUpdateCheckTask checkTask = new AppUpdateCheckTask(context)
                .setSnackbarView(this.snackbarView)
                .setTriggerMethod(this.triggerMethod)
                .setDownDir(downDir)
                .setUpdateUrl(this.updateUrl);
        checkTask.execute();
    }
}
