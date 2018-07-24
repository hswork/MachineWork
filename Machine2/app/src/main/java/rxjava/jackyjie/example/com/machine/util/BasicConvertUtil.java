package rxjava.jackyjie.example.com.machine.util;

public class BasicConvertUtil {

    /*
     * double Convert String
     * if double = -1 String = ""
     * */
    public static String getString(double a){
        if(a == -1)
            return "";
        else
            return Double.toString(a);
    }
}
