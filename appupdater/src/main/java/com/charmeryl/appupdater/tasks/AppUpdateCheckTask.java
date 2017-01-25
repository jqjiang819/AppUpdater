package com.charmeryl.appupdater.tasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.charmeryl.appupdater.R;
import com.charmeryl.appupdater.dialogs.AppUpdateAvlDialog;
import com.charmeryl.appupdater.managers.UpdateManager;

/**
 * Created by jqjiang on 1/25/17.
 */

public class AppUpdateCheckTask extends AsyncTask<Integer,Integer,Boolean> {
    private static class TriggerMethod {
        public static final int AUTOMATIC = 0;
        public static final int MANUALLY = 1;
    }

    private Context context;
    private View view;
    private ProgressDialog progressDialog;

    private int triggerMethod;
    private String updateUrl;
    private String downDir;
    private UpdateManager updateManager;

    public AppUpdateCheckTask(Context context) {
        this.context = context;
        this.triggerMethod = TriggerMethod.MANUALLY;
        this.updateManager = new UpdateManager(context);
        this.view = ((Activity) context).findViewById(android.R.id.content);
    }

    public AppUpdateCheckTask setUpdateManager(UpdateManager updateManager){
        if (updateManager != null) {
            this.updateManager = updateManager;
        }
        return this;
    }

    public AppUpdateCheckTask setTriggerMethod(int method){
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

    public AppUpdateCheckTask setUpdateUrl(String Url){
        this.updateUrl = Url;
        return this;
    }

    public AppUpdateCheckTask setDownDir(String dir){
        this.downDir = dir;
        return this;
    }

    public AppUpdateCheckTask setSnackbarView(View view){
        if (view != null) {
            this.view = view;
        }
        return this;
    }

    @Override
    protected void onPreExecute() {
        if(this.triggerMethod == TriggerMethod.MANUALLY) {
            this.progressDialog = new ProgressDialog(context);
            this.progressDialog.setMessage(context.getString(R.string.updater_checkUpdateMsg));
            this.progressDialog.setCancelable(false);
            this.progressDialog.create();
            this.progressDialog.show();
        }
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        this.updateManager.setJSONUrl(this.updateUrl);
        return this.updateManager.checkUpdate();
    }

    @Override
    protected void onPostExecute(Boolean status) {
        switch (this.triggerMethod) {
            case TriggerMethod.MANUALLY:
                this.progressDialog.dismiss();
                if(status){
                    new AppUpdateAvlDialog(context,updateManager)
                            .setDownDir(this.downDir)
                            .show();
                    break;
                }
                Snackbar.make(this.view,context.getString(R.string.updater_newestMsg),Snackbar.LENGTH_SHORT).show();
                break;
            case TriggerMethod.AUTOMATIC:
                if(status&&(!updateManager.isHideVer())){
                    new AppUpdateAvlDialog(context,updateManager)
                            .setDownDir(this.downDir)
                            .show();
                }
                break;
            default:
        }
    }
}
