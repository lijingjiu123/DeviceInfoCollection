package com.ljj.deviceinfograb;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.ref.WeakReference;

/**
 * @describe:
 * @author: ljj
 * @creattime: 2020/3/28 17:00
 */
public class DeviceInfoUtils extends DeviceInfoBase{
    private static DeviceInfoUtils sInstance;
    private static final String SP_NAME = "lzx_sdk_device_info";
    private static final String KEY_IMEI = "lzx_sdk_device_info_key_imei";
    private static final String KEY_IMSI = "lzx_sdk_device_info_key_imsi";
    private static final String KEY_MAC = "lzx_sdk_device_info_key_mac";
    private static final String KEY_ANDROID_ID = "lzx_sdk_device_info_key_android_id";

    private String netIP;
    private String gprsIP;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static DeviceInfoUtils getInstance() {
        if (null == sInstance) {
            sInstance = new DeviceInfoUtils();
        }
        return sInstance;

    }

    private DeviceInfoUtils() {
    }

    @Override
    public void init(Context context) {
        weakReference = new WeakReference<>(context);
        //如果当前是wifi连接，获取IP地址
        if (NetStatusUtils.isWifiConnected(weakReference.get())){
            updateNetIP();//获取公网IP
        }
        //
        loadSpDate();
    }

    private void loadSpDate(){
        sp = weakReference.get().getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        editor = sp.edit();
        imei = sp.getString(KEY_IMEI,null);
        imsi = sp.getString(KEY_IMSI,null);
        mac = sp.getString(KEY_MAC,null);
        androidId = sp.getString(KEY_ANDROID_ID,null);
    }

    private void updateNetIP(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                netIP = NetStatusUtils.getNetIP(0);
            }
        }).start();
    }

    /**
     * 获取公网ip地址
     * @return ip地址
     */
    public String getIP(){
        try {
            if (NetStatusUtils.isWifiConnected(weakReference.get())){//wifi连接
                if (notEmpty(netIP)){
                    return netIP;
                }else {
                    updateNetIP();
                }
            }else {//数据网络
                if (notEmpty(gprsIP)){
                    return gprsIP;
                }else {
                    return gprsIP = NetStatusUtils.getGprsIp(weakReference.get());
                }
            }
            return null;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getImei() {
        if (notEmpty(imei)){
            return imei;
        }else {
            imei = super.getImei();
            saveSp(KEY_IMEI,imei);
        }
        return imei;
    }

    @Override
    public String getImsi() {
        if (notEmpty(imsi)){
            return imsi;
        }else {
            imsi = super.getImsi();
            saveSp(KEY_IMSI,imsi);
        }
        return imsi;
    }

    @Override
    public String getAndroidId() {
        if (notEmpty(androidId)){
            return androidId;
        }else {
            androidId = super.getAndroidId();
            saveSp(KEY_ANDROID_ID,androidId);
        }
        return androidId;
    }

    @Override
    public String getMacAddress() {
        if (notEmpty(mac)) {
            return mac;
        }else {
            mac = super.getMacAddress();
            saveSp(KEY_MAC,mac);
        }
        return mac;
    }

    @Override
    public int getDeviceType() {
        if (notEmpty(deviceType)) return deviceType;
        return deviceType = super.getDeviceType();
    }

    @Override
    public String getDeviceModel() {
        if (notEmpty(deviceModel)) return deviceModel;
        return deviceModel = super.getDeviceModel();
    }

    @Override
    public String getDeviceBrand() {
        if (notEmpty(deviceBrand)) return deviceBrand;
        return deviceBrand = super.getDeviceBrand();
    }

    @Override
    public String getMCC() {
        if (notEmpty(mcc)) return mcc;
        return mcc = super.getMCC();
    }

    @Override
    public String getMNC() {
        if (notEmpty(mnc)) return mnc;
        return mnc = super.getMNC();
    }

    @Override
    public int getNetworkType() {
        if (notEmpty(networkType)) return networkType;
        return networkType = super.getNetworkType();
    }

    @Override
    public String getUserAgent() {
        if (notEmpty(userAgent)) return userAgent;
        return userAgent = super.getUserAgent();
    }

    @Override
    public double getLongitude() {
        if (notEmpty(lon)) return lon;
        return lon = super.getLongitude();
    }

    @Override
    public double getLatitude() {
        if (notEmpty(lat)) return lat;
        return lat = super.getLatitude();
    }

    @Override
    public int getGpsType() {
        if (notEmpty(gpsType)) return gpsType;
        return gpsType = super.getGpsType();
    }

    @Override
    public String getSystemVersion() {
        if (notEmpty(symVersion)) return symVersion;
            return symVersion = super.getSystemVersion();
    }

    @Override
    public String getSystemLanguage() {
        if (notEmpty(symLanguage)) return symLanguage;
        return symLanguage = super.getSystemLanguage();
    }

    @Override
    public int getScreenHeight() {
        if (notEmpty(scrHeight)) return scrHeight;
        return scrHeight = super.getScreenHeight();
    }

    @Override
    public int getScreenWidth() {
        if (notEmpty(scrWidth))return scrWidth;
        return scrWidth = super.getScreenWidth();
    }

    @Override
    public float getScreenDensity() {
        if (notEmpty(scrDensity)) return scrDensity;
        return scrDensity = super.getScreenDensity();
    }

    @Override
    public int getScreenDensityDpi() {
        if (notEmpty(scrDensityDpi)) return scrDensityDpi;
        return scrDensityDpi = super.getScreenDensityDpi();
    }

    @Override
    public String getScreenSize() {
        if (notEmpty(scrSize)) return scrSize;
        return scrSize = super.getScreenSize();
    }

    @Override
    public int getScreenOrientation() {
        if (notEmpty(scrOrientation)) return scrOrientation;
        return scrOrientation = super.getScreenOrientation();
    }

    @Override
    public String getAppName() {
        if (notEmpty(appName)) return appName;
        return appName = super.getAppName();
    }

    @Override
    public String getAppPackageName() {
        if (notEmpty(appPkgName)) return appPkgName;
        return appPkgName = super.getAppPackageName();
    }

    @Override
    public String getAppVersionName() {
        if (notEmpty(appVerName)) return appVerName;
        return appVerName = super.getAppVersionName();
    }

    @Override
    public int getAppVersionCode() {
        if (notEmpty(appVerCode)) return appVerCode;
        return appVerCode = super.getAppVersionCode();
    }

    private void saveSp(String key,String value){
        if (TextUtils.isEmpty(value)) return;
        if (null == editor) return;
        editor.putString(key, value);
        editor.apply();
    }

    private boolean notEmpty(String string){
        return !TextUtils.isEmpty(string);
    }
    private boolean notEmpty(int value){
        return value != -1;
    }
    private boolean notEmpty(double value){
        return value != -1;
    }
    private boolean notEmpty(float value){
        return value != -1;
    }
}
