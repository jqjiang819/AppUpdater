package com.charmeryl.appupdater.managers;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

import static android.support.v4.content.FileProvider.getUriForFile;

/**
 * Created by jqjiang on 12/27/16.
 */

public class UpdateManager {

    private static final int SCRIPT_JSON = 0;
    private static final int SCRIPT_XML = 1;

    private Context context;
    private DownloadStateReceiver downloadStateReceiver;

    private String url;
    private String changelog;
    private String version;
    private String hideVer;

    private int UrlType;
    private String ScriptUrl;
    private String CurVer;
    private String FileProviderName;
    private String DownloadDir;

    public UpdateManager(Context context){
        this.context = context;
        this.DownloadDir = "Download/";
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            this.CurVer = packageInfo.versionName;
            this.FileProviderName = packageInfo.packageName + ".ShareFileProvider";
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setXMLUrl(String url){
        this.ScriptUrl = url;
        this.UrlType = SCRIPT_XML;
    }

    public void setJSONUrl(String url){
        this.ScriptUrl = url;
        this.UrlType = SCRIPT_JSON;
    }

    public void setGithub(String user, String repo, String branch) {

    }

    public void setHideVer(String version){
        this.hideVer = version;
        SharedPreferences.Editor editor = context.getSharedPreferences("updater",0).edit();
        editor.putString("hide_ver",version).apply();
    }

    public void setHideVer(){
        this.hideVer = this.version;
        SharedPreferences.Editor editor = context.getSharedPreferences("updater",0).edit();
        editor.putString("hide_ver",this.version).apply();
    }

    public void setDownloadDir(String downloadDir){
        if (downloadDir != null && !downloadDir.isEmpty()) {
            this.DownloadDir = downloadDir;
        }
    }

    public String getHideVer(){
        SharedPreferences pref = context.getSharedPreferences("updater",0);
        this.hideVer = pref.getString("hide_ver","");
        return this.hideVer;
    }

    public boolean isHideVer(){
        return this.version.equals(getHideVer());
    }

    public String getUrl(){
        return this.url;
    }

    public String getChangelog(){
        return this.changelog;
    }

    public String getVersion(){
        return this.version;
    }

    public boolean checkUpdate(){
        String curApk = "app-update-v"+this.CurVer+".apk";
        File curApkFile = new File(Environment.getExternalStorageDirectory(),DownloadDir+curApk);
        if(curApkFile.exists()){
            curApkFile.delete();
        }
        try {
            HttpsURLConnection conn = (HttpsURLConnection) new URL(this.ScriptUrl).openConnection();
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");
            if(conn.getResponseCode()!=conn.HTTP_OK){
                return false;
            }
            InputStream inputStream = conn.getInputStream();
            String[] updateParams = this.parseJSON(inputStream);
            if(!compareVerCode(updateParams[0],CurVer)){
                return false;
            }
            this.version = updateParams[0];
            this.url = updateParams[1];
            this.changelog = updateParams[2];
            return true;
        } catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    public void startUpdate(){
        try {
            String savename = "app-update-v"+this.version+".apk";
            File savefile = new File(Environment.getExternalStorageDirectory(),DownloadDir+savename);
            if(savefile.exists()){
                Intent installer = new Intent(Intent.ACTION_VIEW);
                Uri fileUri = Uri.fromFile(savefile);
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                    fileUri = getUriForFile(context, FileProviderName, savefile);
                }
                installer.setDataAndType(fileUri, "application/vnd.android.package-archive");
                //installer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                context.startActivity(installer);
                return;
            }

            if(!savefile.getParentFile().exists()){
                savefile.getParentFile().mkdirs();
            }
            DownloadManager downloadManager =(DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(this.url));
            request.setDestinationInExternalPublicDir(DownloadDir,savename);
            long requestId = downloadManager.enqueue(request);
            downloadStateReceiver = new DownloadStateReceiver(downloadManager,requestId);
            context.registerReceiver(downloadStateReceiver, new IntentFilter(
                    DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        } catch (Exception e){
            e.printStackTrace();
        }
        return;
    }

    private String[] parseJSON(InputStream inputStream) throws Exception{
        String[] updateParams = new String[3];
        JSONObject updateScript = new JSONObject(convertStreamToString(inputStream));
        updateParams[0] = updateScript.getString("latestVersion");
        updateParams[1] = updateScript.getString("url");
        updateParams[2] = "";
        JSONArray arr = updateScript.getJSONArray("releaseNotes");
        String sep = "";
        for(int i=0;i<arr.length();i++){
            updateParams[2]+=(sep+arr.getString(i));
            sep = "\n";
        }
        return updateParams;
    }

    private static boolean compareVerCode(String newver, String oldver){
        String[] ver_new = newver.split("\\.");
        String[] ver_old = oldver.split("\\.");
        int min_length = Math.min(ver_new.length,ver_old.length);
        for(int i=0;i<min_length;i++){
            int newnum = Integer.parseInt(ver_new[i]);
            int oldnum = Integer.parseInt(ver_old[i]);
            if(newnum>oldnum){
                return true;
            }
            if(newnum<oldnum){
                return false;
            }
        }
        if(ver_new.length>ver_old.length){
            return true;
        }
        return false;
    }

    private static String convertStreamToString(InputStream in) {
        Scanner s = new Scanner(in).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    private class DownloadStateReceiver extends BroadcastReceiver {
        private long requestId;
        private DownloadManager downloadManager;
        public DownloadStateReceiver(DownloadManager downloadManager, long requestId){
            this.downloadManager = downloadManager;
            this.requestId = requestId;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            if(requestId!=intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID,0)){
                return;
            }
            context.unregisterReceiver(downloadStateReceiver);
            String savename = "app-update-v"+UpdateManager.this.version+".apk";
            File savefile = new File(Environment.getExternalStorageDirectory(),DownloadDir+savename);
            Intent installer = new Intent(Intent.ACTION_VIEW);
            Uri fileUri = Uri.fromFile(savefile);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                fileUri = getUriForFile(context, FileProviderName, savefile);
            }
            installer.setDataAndType(fileUri, "application/vnd.android.package-archive");
            //installer.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            installer.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(installer);
        }
    }
}
