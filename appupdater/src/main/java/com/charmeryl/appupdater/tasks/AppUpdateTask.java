package com.charmeryl.appupdater.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.charmeryl.appupdater.R;
import com.charmeryl.appupdater.managers.UpdateManager;
import com.charmeryl.appupdater.dialogs.AppUpdateDialog;

/**
 * Created by jqjiang on 12/27/16.
 */

public class AppUpdateTask extends AsyncTask<Integer,Integer,Boolean> {
    public static final int CHECKUPDATE = 0;
    public static final int INSTALLUPDATE = 1;

    public static final int AUTOMATIC = 0;
    public static final int MANUALLY = 1;

    private Context context;
    private FragmentManager fragmentManager;
    private View view;
    private ProgressDialog dialog;

    private int taskType;
    private int triggerMethod;
    private String updateUrl;
    private String downDir;
    private UpdateManager updateManager;

    public AppUpdateTask(Context context) {
        this.taskType = CHECKUPDATE;
        this.triggerMethod = AUTOMATIC;
        this.context = context;
        this.fragmentManager = ((AppCompatActivity)context).getSupportFragmentManager();;
        this.updateManager = new UpdateManager(context);
        this.view = ((AppCompatActivity) context).findViewById(android.R.id.content);
    }

    public AppUpdateTask setUpdateMan(UpdateManager updateMan){
        this.updateManager = updateMan;
        return this;
    }

    public AppUpdateTask setTaskType(int type){
        this.taskType = type;
        return this;
    }

    public AppUpdateTask setTriggerMethod(int method){
        this.triggerMethod = method;
        return this;
    }

    public AppUpdateTask setUpdateUrl(String Url){
        this.updateUrl = Url;
        return this;
    }

    public AppUpdateTask setDownDir(String dir){
        this.downDir = dir;
        return this;
    }

    public AppUpdateTask setSnackbarView(View view){
        this.view = view;
        return this;
    }

    @Override
    protected void onPreExecute() {
        switch (this.taskType) {
            case CHECKUPDATE:
                this.dialog = new ProgressDialog(context);
                if(this.triggerMethod == MANUALLY) {
                    this.dialog.setMessage(context.getString(R.string.updater_checkUpdateMsg));
                    this.dialog.setCancelable(false);
                    this.dialog.create();
                    this.dialog.show();
                }
                break;
            case INSTALLUPDATE:
        }
    }

    @Override
    protected Boolean doInBackground(Integer... params) {
        if(downDir!=null){
            updateManager.setDownloadDir(downDir);
        }
        switch (this.taskType) {
            case CHECKUPDATE:
                updateManager.setJSONUrl(this.updateUrl);
                return updateManager.checkUpdate();
            case INSTALLUPDATE:
                updateManager.startUpdate();
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean status) {
        switch (this.taskType) {
            case CHECKUPDATE:
                if(this.triggerMethod == MANUALLY){
                    this.dialog.dismiss();
                    if(status){
                        new AppUpdateDialog()
                                .setDownDir(downDir)
                                .setUpdateManager(updateManager)
                                .show(this.fragmentManager,null);
                        break;
                    }
                    Snackbar.make(this.view,context.getString(R.string.updater_newestMsg),Snackbar.LENGTH_SHORT).show();
                }
                if(this.triggerMethod == AUTOMATIC){
                    if(status&&(!updateManager.isHideVer())){
                        new AppUpdateDialog()
                                .setDownDir(downDir)
                                .setUpdateManager(updateManager)
                                .show(this.fragmentManager,null);
                        break;
                    }
                }
                break;
            case INSTALLUPDATE:
        }
    }
}
