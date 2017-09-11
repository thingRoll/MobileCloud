package com.mobilecloud.react.modules;


import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.mobilecloud.MainApplication;
import com.mobilecloud.SecondActivity;
import com.mobilecloud.common.BundleManager;
import com.mobilecloud.common.HttpProcessCallBack;
import com.mobilecloud.pojo.AppPojo;
import com.mobilecloud.pojo.BundleUpdateRequestPojo;
import com.mobilecloud.pojo.update.AppUpdatePojo;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.module.annotations.ReactModule;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hailor on 2017/6/28.
 */

@ReactModule(name = "UpdateAndroid")
public class NativeManager extends ReactContextBaseJavaModule {

    private static final String DURATION_SHORT_KEY = "SHORT";
    private static final String DURATION_LONG_KEY = "LONG";
    private static Toast toast = null;


    public NativeManager(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    public String getName() {
        return "NativeManager";
    }

    @Override
    public Map<String, Object> getConstants() {
        final Map<String, Object> constants = new HashMap<>();
        constants.put(DURATION_SHORT_KEY, Toast.LENGTH_SHORT);
        constants.put(DURATION_LONG_KEY, Toast.LENGTH_LONG);
        return constants;
    }


    //点击子模块
    @ReactMethod
    public void openBundle(String name, /*Integer id,*/ Callback callback) {
        final AppPojo appPojo = BundleManager.getBundleManager().getAppPojo(this.getCurrentActivity().getApplication());
        final AppUpdatePojo appUpdatePojo = BundleManager.getBundleManager().getAppUpdatePojo(this.getCurrentActivity().getApplication());
        if (appPojo.getBundles().get(name) != null) {
            if(appUpdatePojo == null){
                callback.invoke("netError");
            }else if (appUpdatePojo.getBundlesUpdate().get(name) != null) {
                //提示更新子模块
                callback.invoke("update");
            } else {
                //cache的子模块是最新版本，直接打开
                ((MainApplication) getCurrentActivity().getApplication()).setActivity(SecondActivity.class.getName(), appPojo.getBundles().get(name).getPath());
                SecondActivity storedActivity = (SecondActivity) ((MainApplication) getCurrentActivity().getApplication()).getActivity(SecondActivity.class.getName());
                if (storedActivity != null) {
                    BundleManager.getBundleManager().loadBundle(storedActivity, new File(appPojo.getBundles().get(name).getPath()));
                }
                ReactApplicationContext context = getReactApplicationContext();
                Intent intent = new Intent(context, SecondActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        } else {
            //提示下载子模块
            callback.invoke("new");
        }
    }

    //下载并打开子模块
    @ReactMethod
    public void downloadAndOpenBundle(String name, Integer bundleId, final Callback callback) {
        final AppPojo appPojo = BundleManager.getBundleManager().getAppPojo(this.getCurrentActivity().getApplication());
        final AppUpdatePojo appUpdatePojo = BundleManager.getBundleManager().getAppUpdatePojo(this.getCurrentActivity().getApplication());
        String targetVersion = "0";
        if(appUpdatePojo == null){
            callback.invoke("netError");
        }else if (appUpdatePojo.getBundlesUpdate().get(name) != null) {
            //更新子模块
            targetVersion = appUpdatePojo.getBundlesUpdate().get(name).getTargetVersion();
        }
        BundleUpdateRequestPojo bundleUpdateRequestPojo = new BundleUpdateRequestPojo(appPojo.getId(), appPojo.getName(), appPojo.getCurrentVersion(), appPojo.getUrl(), name, targetVersion, bundleId);
        //调用updateBundle下载bundle
        BundleManager.getBundleManager().updateBundle(bundleUpdateRequestPojo, this.getCurrentActivity().getApplication(), new HttpProcessCallBack() {
            @Override
            public void progress(float progress) {
                Log.w("NativeManager", "+++++"+String.format("%f", progress));
                if(toast == null){
                    toast = Toast.makeText(getReactApplicationContext(), String.format("%f",progress), Toast.LENGTH_SHORT);
                }else{
                    if(progress<1.0){
                        toast.setText(String.format("%f",progress));
                    }
                }
                toast.show();
            }

            @Override
            public void success(Object object) {
                final File file = (File) object;
                Log.w("NativeManager", String.format("%s", file.getAbsolutePath()));
                //BundleManager.getBundleManager().loadBundle(getCurrentActivity(),file);
                if(toast == null){
                    toast = Toast.makeText(getReactApplicationContext(), String.format("download success"), Toast.LENGTH_SHORT);
                }else{
                    toast.setText(String.format("download success"));
                }
                callback.invoke("success", file.getAbsolutePath());
            }

            @Override
            public void failure(Object object) {
                Log.w("NativeManager", ((Exception) object).getMessage());
            }
        });
    }



    /*@ReactMethod
    public void show(String message, int duration) {
        ReactApplicationContext context = getReactApplicationContext();
        Intent intent = new Intent(context, SecondActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        Toast.makeText(getReactApplicationContext(), message, duration).show();
    }*/


    @ReactMethod
    public void checkMainUpdateAble(final Callback callback) {
        final AppPojo appPojo = BundleManager.getBundleManager().getAppPojo(this.getCurrentActivity().getApplication());
        final AppUpdatePojo appUpdatePojo = BundleManager.getBundleManager().getAppUpdatePojo(this.getCurrentActivity().getApplication());
        if (appUpdatePojo.getMainBundleUpdate() != null){
            callback.invoke("主模块有更新,本地版本："+appPojo.getMainBundle().getCurrentVersion()+",远程版本："+appUpdatePojo.getMainBundleUpdate().getTargetVersion()+"。");
        }
        /*BundleManager.getBundleManager().checkBundleConfigUpdate(this.getCurrentActivity().getApplication(), appPojo, new HttpProcessCallBack() {

            @Override
            public void progress(float progress) {

            }

            @Override
            public void success(Object object) {
                AppUpdatePojo appUpdatePojoResult = (AppUpdatePojo) object;
                if (appUpdatePojoResult.getMainBundleUpdate() != null) {
                    Log.w("MainUpdate", "=============主模块有更新");
                    WritableMap resultData = new WritableNativeMap();
                    resultData.putString("name", appPojo.getMainBundle().getName());
                    resultData.putString("path", appPojo.getMainBundle().getPath());
                    resultData.putString("version", appPojo.getMainBundle().getCurrentVersion());
                    callback.invoke(resultData, appUpdatePojoResult.getMainBundleUpdate().getTargetVersion());
                }
            }

            @Override
            public void failure(Object object) {

            }
        });*/
    }

    @ReactMethod
    public void updateMain(int bundleId, final Callback callback) {
        final AppPojo appPojo = BundleManager.getBundleManager().getAppPojo(this.getCurrentActivity().getApplication());
        final AppUpdatePojo appUpdatePojo = BundleManager.getBundleManager().getAppUpdatePojo(this.getCurrentActivity().getApplication());
        BundleUpdateRequestPojo bundleUpdateRequestPojo = new BundleUpdateRequestPojo(appPojo.getId(), appPojo.getName(), appPojo.getCurrentVersion(), appPojo.getUrl(), appPojo.getMainBundle().getName(), appUpdatePojo.getMainBundleUpdate().getTargetVersion(), bundleId);
        BundleManager.getBundleManager().updateBundle(bundleUpdateRequestPojo, this.getCurrentActivity().getApplication(), new HttpProcessCallBack() {
            @Override
            public void progress(float progress) {
                Log.w("NativeManager", String.format("%f", progress));
                //Toast.makeText(getReactApplicationContext(), String.format("%f",progress)+"%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void success(Object object) {
                final File file = (File) object;
                Log.w("NativeManager", String.format("%s", file.getAbsolutePath()));
                BundleManager.getBundleManager().loadBundle(getCurrentActivity(), file);
                callback.invoke("success", file.getAbsolutePath());
            }

            @Override
            public void failure(Object object) {
                Log.w("NativeManager", ((Exception) object).getMessage());
            }
        });
    }


}
