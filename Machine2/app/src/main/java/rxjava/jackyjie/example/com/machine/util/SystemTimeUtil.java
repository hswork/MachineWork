package rxjava.jackyjie.example.com.machine.util;

import android.app.AlarmManager;
import android.content.Context;
import android.provider.Settings;
import android.text.format.DateFormat;

import java.util.Calendar;

public class SystemTimeUtil {

    /*
    * set 24 hour
    * */
    public static void set24HourFormat(Context mContext){
        boolean is24Hour =  DateFormat.is24HourFormat(mContext);
        if(!is24Hour){
            android.provider.Settings.System.putString(mContext.getContentResolver(),
                    android.provider.Settings.System.TIME_12_24, "24");
        }
    }

    /*
    * judge
    * time auto set
    * */
    public static boolean isDateTimeAuto(Context mContext) {
        try {
            return android.provider.Settings.Global.getInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME) > 0;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }


    /*
    * set
    * time not autoSet
    * */
    public static void setNNotAutoDateTime(Context mContext){
        if(isDateTimeAuto(mContext)) {
            android.provider.Settings.Global.putInt(mContext.getContentResolver(),
                    android.provider.Settings.Global.AUTO_TIME, 0);
        }
    }

    /*
    * set
    * datetime
    * */
    public static void setSysTime(Context mContext, int hour,int minute){
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long when = c.getTimeInMillis();
        if(when / 1000 < Integer.MAX_VALUE){
            ((AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE)).setTime(when);
        }
    }
}
