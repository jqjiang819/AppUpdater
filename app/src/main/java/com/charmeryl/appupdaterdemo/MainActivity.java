package com.charmeryl.appupdaterdemo;

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
        Button btn_update_none = (Button) findViewById(R.id.btn_update_none);

        btn_update_avail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdater(MainActivity.this)
                        .setTriggerMethod(AppUpdater.TriggerMethod.MANUALLY)
                        .setUpdateUrl("https://gist.githubusercontent.com/CharmeRyl/efa87949bd3724221b02ea4d01919ba9/raw/51557b836999e298d445e695438e42f1cfe3b0e5/updater.json")
                        .start();
            }
        });

        btn_update_none.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUpdater(MainActivity.this)
                        .setTriggerMethod(AppUpdater.TriggerMethod.MANUALLY)
                        .setUpdateUrl("https://raw.githubusercontent.com/charmeryl/AppUpdater/master/app/update-latest.json")
                        .start();
            }
        });
    }
}
