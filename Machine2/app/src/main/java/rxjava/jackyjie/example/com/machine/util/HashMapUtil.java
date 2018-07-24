package rxjava.jackyjie.example.com.machine.util;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;

public class HashMapUtil {

    /*
     * hashMap add
     * if contains discover
     * if not add
     * */
    public static void removeKeyValue(HashMap<String, Object> hashes, String name) {

        if (hashes.containsKey(name)) {
            hashes.remove(name);
        }
    }

    /*
     * hashMap get
     * value to String
     * */
    public static String getStringValue(HashMap<String, Object> hashes, String name) {
        if (!hashes.containsKey(name) || hashes.get(name).toString() == "null") {
            return "";
        }
        return hashes.get(name).toString();
    }

    /*
     * hashMap get
     * value to String
     * */
    public static int getIntValue(HashMap<String, Object> hashes, String name) {
        try{
            if (!hashes.containsKey(name) || hashes.get(name).toString() == "null") {
                throw new Exception();
            }
            int result = Integer.parseInt(hashes.get(name).toString());
            return result;
        }catch (Exception ex){
            return -1;
        }
    }

    /*
     * hashMap get
     * value to String
     * */
    public static long getLongValue(HashMap<String, Object> hashes, String name) {
        try{
            if (!hashes.containsKey(name) || hashes.get(name).toString() == "null") {
                throw new Exception();
            }
            long result = Long.parseLong(hashes.get(name).toString());
            return result;
        }catch (Exception ex){
            return -1;
        }
    }

    /*
     * hashMap add
     * if contains discover
     * if not add
     * */
    public static void addKeyValue(HashMap<String, Object> hashes, String name, Object value) {

        if (hashes.containsKey(name)) {
            hashes.remove(name);
            hashes.put(name, value);
        } else {
            hashes.put(name, value);
        }
    }

    /*
     * hashMap add
     * if contains return;
     * if not add;
     * */
    public static void addKeyValue(HashMap<String, Object> hashes, String name, Object value, boolean discover) {
        if (hashes.containsKey(name)) {
            return;
        } else {
            hashes.put(name, value);
        }
    }

    /*
     * JsonObject
     * get key-value pairs from jsonObject
     * */
    public static HashMap<String, Object> getKeyValues(JSONObject object) {
        HashMap<String, Object> hashes = new HashMap<>();
        Iterator<String> sIterator = object.keys();
        while (sIterator.hasNext()) {
            try {
                String key = sIterator.next();
                Object value = object.get(key);
                hashes.put(key, value);
            } catch (Exception ex) {

            }
        }
        return hashes;
    }

    /*
     * JsonObject
     * get key-value pairs from jsonObject
     * except param1, param2
     * */
    public static HashMap<String, Object> getKeyValues(JSONObject object, String except1) {
        HashMap<String, Object> hashes = new HashMap<>();
        Iterator<String> sIterator = object.keys();
        while (sIterator.hasNext()) {
            try {
                String key = sIterator.next();
                if (!except1.equals(key)) {
                    Object value = object.get(key);
                    hashes.put(key, value);
                }
            } catch (Exception ex) {

            }
        }
        return hashes;
    }
}
