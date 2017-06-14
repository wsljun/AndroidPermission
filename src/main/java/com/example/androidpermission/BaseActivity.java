package com.example.androidpermission;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Android6.0+ 运行时权限封装类
 * 注意一定要记得在Manifest文件里添加上所有请求的权限，已授权的权限不会再次被请求
 */

public class BaseActivity extends AppCompatActivity {
    private final static int REQUEST_CODE = 1;
    private static IPermission mListener;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
    }

    public static void requestRunTimePermission(String[] permissions, IPermission listener) {
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) {
            return;
        }
        mListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity, permissionList.toArray(new String[permissionList.size()]), REQUEST_CODE);
        } else {
            //doSomething 已经允许过的权限
            mListener.onGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mListener.onGranted();//全部允许
                    } else {
                        mListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

    public void fileScan(File file){
        Uri data =  Uri.parse("file://"+ Environment.getExternalStorageDirectory());
        sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
    }
    public String rootDir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "ScreenRecord";

    /**
     * 文件刷新
     */
    public void refreshFile() {
        File file = new File(rootDir);
        MimeTypeMap mtm = MimeTypeMap.getSingleton();
        MediaScannerConnection.scanFile(this,
                new String[] { file.toString() },
                new String[] { mtm.getMimeTypeFromExtension(file.toString().substring(file.toString().lastIndexOf(".")+1)) },
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(final String path, final Uri uri) {
                        Log.d("fileUpdate", "刷新完毕path= "+path);
                    }
                });
    }

    /**
     *
     * 获取WIFI下ip地址
     */
    public String getLocalIpAddress() {
        WifiManager wifiManager = (WifiManager) this.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        // 获取32位整型IP地址
        int ipAddress = wifiInfo.getIpAddress();

        //返回整型地址转换成“*.*.*.*”地址
        return String.format("%d.%d.%d.%d",
                (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
    }



}
