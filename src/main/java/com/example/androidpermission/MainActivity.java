package com.example.androidpermission;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.List;

import static com.example.androidpermission.BaseActivity.requestRunTimePermission;

public class MainActivity extends BaseActivity implements IPermission{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermissions();
    }
    /**
     * 申请录屏权限, //申请权限 6.0以上权限需要动态注册
     */
    private void requestPermissions(){
        //使用baseActivity 中封装的权限处理方法
        String[] permissionList = {Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_PHONE_STATE
        ,Manifest.permission.CAMERA};
        requestRunTimePermission(permissionList,this);

    }

    @Override
    public void onGranted() {
        // 权限被允许
        Toast.makeText(this,"全部允许",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDenied(List<String> deniedPermission) {
        //未被允许权限集合
        for (String p :deniedPermission){
            Toast.makeText(this,p,Toast.LENGTH_SHORT).show();
        }
    }
}
