package rxjava.jackyjie.example.com.machine.util;

import java.util.HashMap;

public class CommonUtil {

    /*
     * 键值对解析
     * a=b&b=c
     * */
    public static HashMap<String, String> paramsToMap(String params) {
        HashMap<String, String> hashes = new HashMap<>();
        String[] list = params.split("&|=");
        if (list.length % 2 == 1) return hashes;

        for (int i = 0; i < list.length; i = i + 2) {
            hashes.put(list[i], list[i + 1]);
        }
        return hashes;
    }

    /*
     * 16进制转成10进制
     * */
    public static long hexToDecimal(String hex) {
        long result = Long.parseLong(hex, 16);
        return result;
    }

    /*
     * IC卡号读取十六进制方式
     * 去除末尾2位
     * 每两位进行一次翻转
     * 16进制转10进制
     * */
    public static long hex2RfidHex(String hex) {
        hex = hex.substring(0, hex.length() - 2);
        StringBuilder sb = new StringBuilder();
        for (int i = hex.length() - 1; i >= 0; i--) {
            if (i % 2 == 1) {
                sb.append(String.valueOf(hex.charAt(i - 1)));
                sb.append(String.valueOf(hex.charAt(i)));
            }
        }
        String data = sb.toString();
        return hexToDecimal(sb.toString());
    }
}
