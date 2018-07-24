package rxjava.jackyjie.example.com.machine.web;

import android.util.Log;

import java.util.HashMap;
import java.util.List;

public class WebApis {

    private static final String TAG = "WebApis";
    // 服务器地址
    public static String sUrl = "http://183.134.73.140:868/QueryService.asmx";

    // functionName
    public static String sFunctionName = "ExecAndQuery";

    // webservice 调用格式
    private static HashMap<String, String> setHashes(String sql){
        HashMap<String, String> hashes = new HashMap<>();
        hashes.put("sToken", "HsTest");
        hashes.put("sSql", sql);
        hashes.put("bIncludeSchema", "false");
        hashes.put("bCompress", "false");
        hashes.put("iResultType", "1");
        return hashes;
    }

    // 调用 webservice 输入sql语句
    private static String sendWebServiceBySql(String sql){
        WebServiceHelper helper = new WebServiceHelper(sUrl, "");
        HashMap<String, String> hashes = setHashes(sql);
        String sResult = helper.getData(sFunctionName, hashes);
        Log.d(TAG, "run: "+sResult);
        return sResult;
    }

    // 调用webservice 输入存储过程和参数
    public static boolean sendWebServiceByMethod(String procName, List<String> params){
        StringBuilder sql = new StringBuilder();
        sql.append("exec ");
        sql.append(procName);
        if(params.size() >= 0) {
            for (int i = 0; i < params.size(); i++) {
                if (i == 0) {
                    sql.append(" '" + params.get(i) + "'");
                } else {
                    sql.append(",'" + params.get(i) + "'");
                }
            }
        }
        String sSql = sql.toString();
        String sResult = sendWebServiceBySql(sSql);
        return DataHandler.handleMessageByProc(procName, sResult);
    }
}
