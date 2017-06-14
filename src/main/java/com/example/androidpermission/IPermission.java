package com.example.androidpermission;

import java.util.List;

/**
 * 该接口处理 permission 操作回调
 */

public interface IPermission {

    void onGranted(); // 已被允许权限

    void onDenied(List<String> deniedPermission);// 未被允许权限
}
