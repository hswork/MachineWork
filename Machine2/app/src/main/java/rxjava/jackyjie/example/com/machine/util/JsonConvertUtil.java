package rxjava.jackyjie.example.com.machine.util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class JsonConvertUtil {

    /*
     * JsonObject
     * getString by name
     */
    public static String getString(JSONObject object, String name){
        if(object.has(name)){
            String value;
            try {
                value = object.getString(name);
                if("null".equals(value))
                    return "";
            }catch(Exception ex){
                return "";
            }
            return value;
        }
        else {
            return "";
        }
    }

    /*
    *  JsonObject
     * getDouble by name
    * */
    public static Double getDouble(JSONObject object, String name){
        if(object.has(name)){
            Double value;
            try {
                value = object.getDouble(name);
                if("null".equals(value))
                    return (double)-1;
            }catch(Exception ex){
                return (double)-1;
            }
            return value;
        }
        else {
            return (double)-1;
        }
    }

    /*
     *  JsonObject
     * getInt by name
     * */
    public static int getInt(JSONObject object, String name){
        if(object.has(name)){
            int value;
            try {
                value = object.getInt(name);
                if("null".equals(value))
                    return -1;
            }catch(Exception ex){
                return -1;
            }
            return value;
        }
        else {
            return -1;
        }
    }

    /*
     *  JsonObject
     * getLong by name
     * */
    public static long getLong(JSONObject object, String name){
        if(object.has(name)){
            long value;
            try {
                value = object.getLong(name);
                if("null".equals(value))
                    return -1;
            }catch(Exception ex){
                return -1;
            }
            return value;
        }
        else {
            return -1;
        }
    }

    /*
     *  JsonObject
     * getBoolean by name
     * */
    public static boolean getBoolean(JSONObject object, String name){
        if(object.has(name)){
            boolean value;
            try {
                value = object.getBoolean(name);
                if("null".equals(value))
                    return false;
            }catch(Exception ex){
                return false;
            }
            return value;
        }
        else {
            return false;
        }
    }
}
