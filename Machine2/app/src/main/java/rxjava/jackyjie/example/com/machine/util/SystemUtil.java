package rxjava.jackyjie.example.com.machine.util;

import android.content.Context;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SystemUtil {

    /*
    * 校准系统时间
    * correct system time
    * */
    public static boolean setSystemTime(final Context cxt, long millions) {
        try {
            String time = getDateFromMillions(millions);
            Process process = Runtime.getRuntime().exec("sh");
            String cmd = "chmod 666 /dev/alarm\n"
                    + "setprop persist.sys.timezone GMT\n"
                    +"/system/bin/date -s " + time + "\n"
                    + "exit\n";
            process.getOutputStream().write(cmd.getBytes());
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /*
    * format Time
    * from millions  bit s
    * to yyMMdd.HHmmss
    * */
    public static String getDateFromMillions(long millions){
        long time = millions * 1000;
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd.HHmmss");
        Date d1 = new Date(time);
        String t1 = format.format(d1);
        return t1;
    }
}
