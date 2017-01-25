package com.charmeryl.appupdater.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;

import com.charmeryl.appupdater.R;
import com.charmeryl.appupdater.managers.UpdateManager;
import com.charmeryl.appupdater.tasks.AppUpdateInstTask;

/**
 * Created by jqjiang on 1/25/17.
 */

public class AppUpdateAvlDialog implements DialogInterface.OnClickListener{

    private Context context;
    private UpdateManager updateManager;
    private AlertDialog dialog;


    public AppUpdateAvlDialog(Context context, UpdateManager updateManager)
    {
        this.context = context;
        this.updateManager = updateManager;
        this.initView();
    }

    protected void initView() {
        if (this.updateManager==null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(R.layout.dialog_app_update_avl)
                .setTitle(this.context.getString(R.string.updater_newUpdateTitle))
                .setPositiveButton(this.context.getString(R.string.updater_textUpdate),this)
                .setNegativeButton(this.context.getString(R.string.updater_textLater),this)
                .setNeutralButton(this.context.getString(R.string.updater_textHide),this);
        this.dialog = builder.create();
        this.dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                TextView text_updateMsg = (TextView) ((AlertDialog)dialog).findViewById(R.id.text_updateMsg);
                TextView text_updateCLog = (TextView) ((AlertDialog)dialog).findViewById(R.id.text_updateCLog);

                String version = updateManager.getVersion();
                String changelog = updateManager.getChangelog();

                text_updateMsg.setText(String.format(context.getString(R.string.updater_newUpdateMsg),version));
                text_updateCLog.setText(changelog);
            }
        });
    }

    public AppUpdateAvlDialog setDownDir(String dir) {
        updateManager.setDownloadDir(dir);
        return this;
    }

    public void show() {
        if (this.dialog!=null) {
            this.dialog.show();
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                new AppUpdateInstTask(context,updateManager).execute();
                break;
            case Dialog.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            case Dialog.BUTTON_NEUTRAL:
                updateManager.setHideVer();
                break;
        }
    }

}
