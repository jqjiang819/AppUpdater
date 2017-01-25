package com.charmeryl.appupdaterdemo;

import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.charmeryl.appupdater.AppUpdater;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView(){
        Button btn_update_avail = (Button) findViewById(R.id.btn_update_avail);
        Button btn_update_avail_auto = (Button) findViewById(R.id.btn_update_avail_auto);
        Button btn_update_none = (Button) findViewById(R.id.btn_update_none);

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        btn_update_avail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdater(MainActivity.this)
                        .setTriggerMethod(AppUpdater.TriggerMethod.MANUALLY)
                        .setSnackbarView(fab)
                        .setUpdateUrl("https://gist.githubusercontent.com/CharmeRyl/efa87949bd3724221b02ea4d01919ba9/raw/51557b836999e298d445e695438e42f1cfe3b0e5/updater.json")
                        .start();
            }
        });

        btn_update_avail_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdater(MainActivity.this)
                        .setTriggerMethod(AppUpdater.TriggerMethod.AUTOMATIC)
                        .setSnackbarView(fab)
                        .setUpdateUrl("https://gist.githubusercontent.com/CharmeRyl/efa87949bd3724221b02ea4d01919ba9/raw/51557b836999e298d445e695438e42f1cfe3b0e5/updater.json")
                        .start();
            }
        });

        btn_update_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdater(MainActivity.this)
                        .setTriggerMethod(AppUpdater.TriggerMethod.MANUALLY)
                        .setSnackbarView(fab)
                        .setUpdateUrl("https://raw.githubusercontent.com/charmeryl/AppUpdater/master/app/update-latest.json")
                        .start();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = getSharedPreferences("updater",0).edit();
                editor.clear().apply();
                Snackbar.make(v,"Pref cleared",Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}
