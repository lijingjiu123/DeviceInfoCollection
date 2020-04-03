package com.ljj.deviceinfograb;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.webkit.WebSettings;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 * @describe:
 * @author: ljj
 * @creattime: 2020/3/26 11:24
 */
public abstract class DeviceInfoBase {
    private static final String METHOD_IMEI = "getImei";
    private static final String METHOD_IMSI = "getSubscriberId";
    protected WeakReference<Context> weakReference;

    //设备硬件信息
    protected String imei;
    protected String imsi;
    protected String androidId;
    protected String mac;
    protected int deviceType = -1;
    protected String deviceModel;
    protected String deviceBrand;
    //网络信息
    protected String mcc;
    protected String mnc;
    protected int networkType = -1;
    protected String userAgent;
    //地位信息
    protected double lon = -1;
    protected double lat = -1;
    protected int gpsType = -1;
    //系统信息
    protected String symVersion;
    protected String symLanguage;
    //屏幕信息
    protected int scrHeight = -1;
    protected int scrWidth = -1;
    protected float scrDensity = -1;
    protected int scrDensityDpi = -1;
    protected String scrSize;
    protected int scrOrientation = -1;
    //应用信息
    protected String appName;
    protected String appPkgName;
    protected String appVerName;
    protected int appVerCode = -1;


    abstract void init(Context context);

    //------------------------设备硬件信息imei、imsi、mac、androidID、设备类型、设备型号、设备品牌---------------------------------
    /**
     * 获取imei
     * @return 手机的imei
     */
    public String getImei() {
        try{
            String imei;
            if (Build.VERSION.SDK_INT >= 21) {//5.0及以上
                //获取1号卡位imei
                imei = getPhoneInfo(0, METHOD_IMEI);
                if (TextUtils.isEmpty(imei) || imei.length() < 15) {
                    //获取2号卡位imei
                    imei = getPhoneInfo(1, METHOD_IMEI);
                    if (TextUtils.isEmpty(imei) || imei.length() < 15) {
                        //获取默认卡位imei
                        imei = getPhoneInfo(METHOD_IMEI);
                        if (TextUtils.isEmpty(imei) || imei.length() < 15) imei = null;
                    }
                }
            } else {
                //获取默认imei
                imei = getPhoneInfo(METHOD_IMEI);
                if (TextUtils.isEmpty(imei) || imei.length() < 15) imei = null;
            }
            return imei;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取imsi
     * @return 手机的imsi
     */
    public String getImsi() {
        try{
            String imsi;
            if (Build.VERSION.SDK_INT >= 21) {//5.0及以上
                //获取1号卡imsi
                imsi = getPhoneInfo(0, METHOD_IMSI);
                if (TextUtils.isEmpty(imsi)) {
                    //获取2号卡imsi
                    imsi = getPhoneInfo(1, METHOD_IMSI);
                    if (TextUtils.isEmpty(imsi)) {
                        //获取默认imsi
                        imsi = getPhoneInfo(METHOD_IMSI);
                    }
                }
            } else {
                //获取默认imsi
                imsi = getPhoneInfo(METHOD_IMSI);
            }
            this.imsi = imsi;
            return imsi;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 反射获取设备指定卡槽的imei和imsi
     */
    private String getPhoneInfo(int subId, String methodName) {
        //5.0之后可以使用
        try {
            TelephonyManager tm = (TelephonyManager) weakReference.get().getSystemService(Context.TELEPHONY_SERVICE);
            Method method = tm.getClass().getDeclaredMethod(methodName, int.class);
            if (subId >= 0) {
                return (String) method.invoke(tm, subId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反射获取设备默认imei和imsi
     */
    private String getPhoneInfo(String methodName) {
        //5.0之前使用
        try {
            TelephonyManager tm = (TelephonyManager) weakReference.get().getSystemService(Context.TELEPHONY_SERVICE);
            Method method = tm.getClass().getDeclaredMethod(methodName, int.class);
            return (String) method.invoke(tm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取androidID
     * @return 手机的androidID
     */
    public String getAndroidId(){
        try {
            return Settings.System.getString(weakReference.get().getContentResolver(), Settings.System.ANDROID_ID);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取mac地址
     * @return 手机的mac地址
     */
    public String getMacAddress() {
        try{
            String macAddr;
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //6.0及以上、7.0以下
                macAddr = MacAdressUtils.getMacAddress(weakReference.get());
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                //7.0及以上
                macAddr = MacAdressUtils.getMacAddrByIp();
            }else{
                //6.0以下
                macAddr = MacAdressUtils.getMacAddress0(weakReference.get());
            }
            return macAddr;

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取设备类型，手机或者平板
     * @return 1手机， 2平板；
     */
    public int getDeviceType() {
        try {
                if ((weakReference.get().getResources().getConfiguration().screenLayout
                        & Configuration.SCREENLAYOUT_SIZE_MASK)
                        >= Configuration.SCREENLAYOUT_SIZE_LARGE){
                    return  2;
                }else {
                    return  1;
                }

        }catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }

    /**
     * 获取手机型号
     * @return  手机型号
     */
    public String getDeviceModel() {
        return Build.MODEL;
    }

    /**
     * 获取手机厂商
     * @return  手机厂商
     */
    public String getDeviceBrand() {
        return Build.BRAND;
    }

    //------------------------网络信息 MCC、MNC、网络类型、UA---------------------------------

    /**
     * 获取sim卡的国家码
     * @return  移动国家码
     */
    public String getMCC(){
        try{
            if (TextUtils.isEmpty(imsi)) imsi = getImsi();
            if (TextUtils.isEmpty(imsi)){//获取不到imsi
                return null;
            }else {
                if (imsi.length() > 2){
                    return imsi.substring(0, 3);
                }else {//位数不够
                    return null;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取sim卡的网络码
     * @return  移动网络码
     */
    public String getMNC(){
        try{
            if (TextUtils.isEmpty(imsi)) imsi = getImsi();
            if (TextUtils.isEmpty(imsi)){//获取不到imsi
                return null;
            }else {
                if (imsi.length() > 4){
                    return imsi.substring(3, 5);
                }else {//位数不够
                    return null;
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取手机当前网络类型
     * @return  网络类型 -1 未知，1 wifi，2 2G，3 3G，4 4G，5 5G
     */
    public int getNetworkType(){
        try{
            return NetStatusUtils.getNetWorkClass(weakReference.get());

        }catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * 获取浏览器UserAgent
     * @return  UserAgent
     */
    public String getUserAgent() {
        try{
            String userAgent = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                try {
                    userAgent = WebSettings.getDefaultUserAgent(weakReference.get());
                } catch (Exception e) {
                    userAgent = System.getProperty("http.agent");
                }
            } else {
                userAgent = System.getProperty("http.agent");
            }
            StringBuffer sb = new StringBuffer();
            for (int i = 0, length = userAgent.length(); i < length; i++) {
                char c = userAgent.charAt(i);
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", (int) c));
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }



    //------------------------定位信息 经度、纬度、定位类型---------------------------------
    /**
     * 获取最近一次定位的经度
     * @return  经度
     */
    public double getLongitude(){
        try{
            Location location = LocationUtils.beginLocatioon(weakReference.get());
            if (location != null) {
                return location.getLongitude();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取最近一次定位的纬度
     * @return  纬度
     */
    public double getLatitude(){
        try{
            Location location = LocationUtils.beginLocatioon(weakReference.get());
            if (location != null) {
                return location.getLatitude();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取定位类型
     * @return  定位类型 -1未知， 1 网络定位，2 gps定位
     */
    public int getGpsType(){
        try{
            return LocationUtils.getLocationType(weakReference.get());

        }catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }


    //------------------------系统信息 系统版本、系统语言---------------------------------

    /**
     * 获取当前手机系统版本号
     * @return  系统版本号，例如"1.0" or "3.4b5"
     */
    public String getSystemVersion() {
        return Build.VERSION.RELEASE;
    }

    /**
     * 获取当前手机系统语言。
     * @return 返回当前系统语言。例如：当前设置的是“中文-中国”，则返回“zh-CN”
     */
    public String getSystemLanguage() {
        return Locale.getDefault().getLanguage();
    }

    //------------------------屏幕信息 屏幕高、屏幕宽、屏幕密度、屏幕密度dpi、屏幕尺寸 ---------------------------------

    /**
     * 获取屏幕高度
     * @return 屏幕高度
     */
    public int getScreenHeight(){
        try{
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager)weakReference.get().getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getRealMetrics(dm);
                return dm.heightPixels;
            }else {
                DisplayMetrics dm = weakReference.get().getResources().getDisplayMetrics();
                return dm.heightPixels;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取屏幕宽度
     * @return 屏幕宽度
     */
    public int getScreenWidth(){
        try{
            DisplayMetrics dm = weakReference.get().getResources().getDisplayMetrics();
            return dm.widthPixels;

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取屏幕密度
     * @return 屏幕密度
     */
    public float getScreenDensity(){
        try{
            DisplayMetrics dm = weakReference.get().getResources().getDisplayMetrics();
            return dm.density;

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取屏幕密度dpi
     * @return 屏幕密度dpi
     */
    public int getScreenDensityDpi(){
        try{
            DisplayMetrics dm = weakReference.get().getResources().getDisplayMetrics();
            return dm.densityDpi;

        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 获取屏幕尺寸
     * @return 屏幕尺寸
     */
    public String getScreenSize(){
        try{
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN){
                DisplayMetrics dm = new DisplayMetrics();
                WindowManager wm = (WindowManager)weakReference.get().getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getRealMetrics(dm);
                return String.format("%.2f", (Math.sqrt(Math.pow(dm.heightPixels,2)+Math.pow(dm.widthPixels,2))/dm.xdpi));
            }else {
                DisplayMetrics dm = weakReference.get().getResources().getDisplayMetrics();
                return String.format("%.2f", (Math.sqrt(Math.pow(dm.heightPixels,2)+Math.pow(dm.widthPixels,2))/dm.xdpi));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取屏幕方向
     * @return 屏幕方向 1竖屏， 2横屏
     */
    public int getScreenOrientation(){
        try{
            Configuration mConfiguration = weakReference.get().getResources().getConfiguration(); //获取设置的配置信息
            int ori = mConfiguration.orientation; //获取屏幕方向
            if (ori == mConfiguration.ORIENTATION_LANDSCAPE) { //横屏
                return 2;
            } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {//竖屏
                return 1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return 1;
    }



    //------------------------应用信息 应用名称、应用包名、版本号、版本号名称---------------------------------
    /**
     * 获取应用名称
     * @return app名称
     */
    public String getAppName() {
        try {
            PackageManager packageManager = weakReference.get().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    weakReference.get().getPackageName(), 0);
            int labelRes = packageInfo.applicationInfo.labelRes;
            return weakReference.get().getResources().getString(labelRes);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用包名
     * @return app包名
     */
    public String getAppPackageName() {

        try {
            return weakReference.get().getPackageName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用版本名称
     * @return app版本名
     */
    public String getAppVersionName() {
        try {
            PackageManager packageManager = weakReference.get().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    weakReference.get().getPackageName(), 0);
            return packageInfo.versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用版本号
     * @return app版本号
     */
    public int getAppVersionCode() {
        try {
            PackageManager packageManager = weakReference.get().getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(
                    weakReference.get().getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

}
