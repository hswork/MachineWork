package rxjava.jackyjie.example.com.machine.web;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/5/3.
 */
public class NetUtil {
    /**
     * 判断是否有网络连接
     * @return
     */
    public  static boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) return false;
        // 获取NetworkInfo对象
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo == null ) return false;
        if (networkInfo.getState() == NetworkInfo.State.CONNECTED) return true;
        return false;
    }

    /**
     * 通过正则判断是不是网址
     * @param url
     * @return
     */
    public static boolean isUrlByString(String url){
        if(url==null||url.isEmpty()) return false;
        Pattern pattern = Pattern
                .compile("^(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])\\.(\\d|[1-9]\\d|1\\d{2}|2[0-5][0-5])$");
        Matcher m = pattern.matcher(url); // 操作的字符串
        return m.matches();
    }

    /**
     * 通过正则判断是否是端口
     * @param url
     * @return
     */
    public static boolean isPortString(String url){
        if(url==null||url.isEmpty()) return false;
        Pattern pattern = Pattern
                .compile("^([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-5]{2}[0-3][0-5])$");
        Matcher m = pattern.matcher(url); // 操作的字符串
        return m.matches();
    }
}
