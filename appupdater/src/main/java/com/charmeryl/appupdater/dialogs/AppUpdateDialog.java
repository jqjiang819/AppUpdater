package com.charmeryl.appupdater.dialogs;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.TextView;
import android.widget.Toast;

import com.charmeryl.appupdater.R;
import com.charmeryl.appupdater.managers.UpdateManager;
import com.charmeryl.appupdater.tasks.AppUpdateTask;

/**
 * Created by jqjiang on 12/27/16.
 */

public class AppUpdateDialog extends DialogFragment implements DialogInterface.OnClickListener {
    private UpdateManager updateManager;
    private String downDir;

    @Override
    public void show(FragmentManager manager, String tag) {
        if (updateManager == null){
            return;
        }
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        AlertDialog dialog;
        builder.setView(R.layout.dialog_app_update)
                .setTitle(getString(R.string.updater_newUpdateTitle))
                .setPositiveButton(getString(R.string.updater_textUpdate),this)
                .setNegativeButton(getString(R.string.updater_textLater),this)
                .setNeutralButton(getString(R.string.updater_textHide),this);

        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                TextView text_updateMsg = (TextView) getDialog().findViewById(R.id.text_updateMsg);
                TextView text_updateCLog = (TextView) getDialog().findViewById(R.id.text_updateCLog);
                String version = updateManager.getVersion();
                String changelog = updateManager.getChangelog();

                text_updateMsg.setText(String.format(getString(R.string.updater_newUpdateMsg),version));
                text_updateCLog.setText(changelog);
            }
        });
        return dialog;
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    public AppUpdateDialog setUpdateManager(UpdateManager updateManager){
        this.updateManager = updateManager;
        return this;
    }

    public AppUpdateDialog setDownDir(String downDir){
        this.downDir = downDir;
        return this;
    }

    @Override
    public void onClick(DialogInterface dialog, int which){
        switch (which) {
            case Dialog.BUTTON_POSITIVE:
                if (ContextCompat.checkSelfPermission(getContext()
                        , Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    new AppUpdateTask(getContext())
                            .setDownDir(downDir)
                            .setUpdateMan(updateManager)
                            .setTaskType(AppUpdateTask.INSTALLUPDATE)
                            .execute();
                }else{
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},0);
                }
                break;
            case Dialog.BUTTON_NEGATIVE:
                dialog.dismiss();
                break;
            case Dialog.BUTTON_NEUTRAL:
                updateManager.setHideVer();
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode){
                case 0:
                    new AppUpdateTask(getContext())
                            .setDownDir(downDir)
                            .setUpdateMan(updateManager)
                            .setTaskType(AppUpdateTask.INSTALLUPDATE)
                            .execute();
                    break;
            }
        } else {
            new AlertDialog.Builder(getContext())
                    .setTitle(getString(R.string.text_error))
                    .setMessage(getString(R.string.text_permErrMsg))
                    .setPositiveButton(R.string.action_confirm,null)
                    .create().show();
        }
    }
}
