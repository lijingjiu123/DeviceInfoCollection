package com.ljj.deviceinfograb;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Enumeration;

/**
 * @Desc: -
 * - 网络状态
 * create by zhusw on 2018-10-9 10:47
 */
public class NetStatusUtils {
    private static final String TAG = "NetStatusUtils";


    public static final int NETWORK_UNKNOW = -1;

    public static final int NETWORK_WIFI = 1;

    public static final int NETWORK_CLASS_2G = 2;

    public static final int NETWORK_CLASS_3G = 3;

    public static final int NETWORK_CLASS_4G = 4;

    public static final int NETWORK_CLASS_5G = 5;


    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context == null)
            return false;

        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        boolean wifi = false, internet = false;
        try {
            wifi = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        } catch (Exception e) {
        }
        try {
            internet = mConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        } catch (Exception e) {
        }
        return wifi || internet;
    }

    /**
     * 判断网络连接是否是wifi
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null) {
            return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        } else {
            return false;
        }
    }


    /**
     * 判断网络连接类型
     *
     * @param context
     * @return
     */
    public static int getNetWorkClass(Context context) {
        if (!isNetworkConnected(context)) return NETWORK_UNKNOW;

        if (isWifiConnected(context)) return NETWORK_WIFI;

        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (null == telephonyManager) return NETWORK_UNKNOW;

        switch (telephonyManager.getNetworkType()) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return NETWORK_CLASS_2G;
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
            case TelephonyManager.NETWORK_TYPE_TD_SCDMA:
                return NETWORK_CLASS_3G;
            case TelephonyManager.NETWORK_TYPE_LTE:
                return NETWORK_CLASS_4G;
            case 20://TelephonyManager.NETWORK_TYPE_NR
                return NETWORK_CLASS_5G;
            default:
                return NETWORK_UNKNOW;
        }
    }

    /**
     * 获取使用数据网络时的公网IP地址
     * 使用wifi时不能使用此方法
     *
     * @param context
     * @return
     */
    public static String getGprsIp(Context context) {
        NetworkInfo info = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//使用gprs
                try {
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }


    private static String[] platforms = {
            "http://pv.sohu.com/cityjson",
            "http://pv.sohu.com/cityjson?ie=utf-8"
    };

    /**
     * 获取公网ip
     * @param index 从0开始
     * @return
     */
    public static String getNetIP(int index) {
        if (index < platforms.length) {
            BufferedReader buff = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(platforms[index]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setReadTimeout(5000);//读取超时
                urlConnection.setConnectTimeout(5000);//连接超时
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {//找到服务器的情况下,可能还会找到别的网站返回html格式的数据
                    InputStream is = urlConnection.getInputStream();
                    buff = new BufferedReader(new InputStreamReader(is, "UTF-8"));//注意编码，会出现乱码
                    StringBuilder builder = new StringBuilder();
                    String line = null;
                    while ((line = buff.readLine()) != null) {
                        builder.append(line);
                    }

                    buff.close();//内部会关闭 InputStream
                    urlConnection.disconnect();

                    if (index == 0 || index == 1) {//搜狐链接
                        //截取字符串
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);//包含[satrtIndex,endIndex)
                        JSONObject jo = new JSONObject(json);
                        String ip = jo.getString("cip");

                        return ip;
                    } else if (index == 2) {//待添加，解析其它链接

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return null;
        }
        return getNetIP( ++index);
    }


}
