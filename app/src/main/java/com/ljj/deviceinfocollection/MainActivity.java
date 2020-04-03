package com.ljj.deviceinfocollection;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ljj.deviceinfograb.DeviceInfoUtils;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "device_info";
    private DeviceInfoUtils diu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDeviceInfo();
            }
        });
        diu = DeviceInfoUtils.getInstance();
    }

    private void getDeviceInfo(){
        Log.i(TAG,"getImei:"+diu.getImei());
        Log.i(TAG,"getImsi:"+diu.getImsi());
        Log.i(TAG,"getAndroidId:"+diu.getAndroidId());
        Log.i(TAG,"getMacAddress:"+diu.getMacAddress());
        Log.i(TAG,"getDeviceType:"+diu.getDeviceType());
        Log.i(TAG,"getDeviceModel:"+diu.getDeviceModel());
        Log.i(TAG,"getDeviceBrand:"+diu.getDeviceBrand());


        //
        Log.i(TAG,"getMCC:"+diu.getMCC());
        Log.i(TAG,"getMNC:"+diu.getMNC());
        Log.i(TAG,"getNetworkType:"+diu.getNetworkType());
        Log.i(TAG,"getUserAgent:"+diu.getUserAgent());
        //
        Log.i(TAG,"getLongitude:"+diu.getLongitude());
        Log.i(TAG,"getLatitude:"+diu.getLatitude());
        Log.i(TAG,"getGpsType:"+diu.getGpsType());
        //
        Log.i(TAG,"getSystemVersion:"+diu.getSystemVersion());
        Log.i(TAG,"getSystemLanguage:"+diu.getSystemLanguage());
        //
        Log.i(TAG,"getScreenHeight:"+diu.getScreenHeight());
        Log.i(TAG,"getScreenWidth:"+diu.getScreenWidth());
        Log.i(TAG,"getScreenDensity:"+diu.getScreenDensity());
        Log.i(TAG,"getScreenDensityDpi:"+diu.getScreenDensityDpi());
        Log.i(TAG,"getScreenSize:"+diu.getScreenSize());
        Log.i(TAG,"getScreenOrientation:"+diu.getScreenOrientation());
        //
        Log.i(TAG,"getAppName:"+diu.getAppName());
        Log.i(TAG,"getAppPackageName:"+diu.getAppPackageName());
        Log.i(TAG,"getAppVersionName:"+diu.getAppVersionName());
        Log.i(TAG,"getAppVersionCode:"+diu.getAppVersionCode());
        //
        Log.i(TAG,"getIP:"+diu.getIP());
    }
}
