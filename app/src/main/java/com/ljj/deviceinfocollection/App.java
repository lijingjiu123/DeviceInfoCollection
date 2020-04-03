package com.ljj.deviceinfocollection;

import android.app.Application;

import com.ljj.deviceinfograb.DeviceInfoUtils;

/**
 * @describe:
 * @author: ljj
 * @creattime: 2020/4/2 19:23
 */
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DeviceInfoUtils.getInstance().init(this);
    }
}
