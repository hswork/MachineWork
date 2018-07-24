package rxjava.jackyjie.example.com.machine.web;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

public class WebServiceHelper {
    private String FEndPoint = "";
    private static final String F_NAME_SPACE = "http://tempuri.org/";
    public static final String CUS_SERVICE_CHECK_CODE = "4A8D54D1-C7A1-48DC-AD7E-8CA1686FBF85";
    private String FCheckCode = "";

    //---------------------------------------------------------------------------------------
    public WebServiceHelper(String url, String checkCode) {
        FEndPoint = url;
        FCheckCode = checkCode;
    }

    public String getData(String functionName, Map<String,String> params) {
        String nameSpace = F_NAME_SPACE;
        String endPoint = FEndPoint;
        String soapAction = nameSpace + functionName;

        // 第一：实例化SoapObject
        // 对象，指定webService的命名空间（从相关WSDL文档中可以查看命名空间），以及调用方法名称
        SoapObject rpc = new SoapObject(nameSpace, functionName);

        // 第二步：假设方法有参数的话,设置调用方法参数
        if(params != null){
            for(Map.Entry<String, String> item : params.entrySet()){
                rpc.addProperty(item.getKey(), item.getValue());
            }
        }

        // 第三步：设置SOAP请求信息(参数部分为SOAP协议版本号，与你要调用的webService中版本号一致)
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);

        // 第四步：注册Envelope
        envelope.enc = "http://schemas.xmlsoap.org/soap/encoding/";
        envelope.env = "http://schemas.xmlsoap.org/soap/envelope/";
        envelope.xsi = "http://www.w3.org/2001/XMLSchema-instance";
        envelope.xsd = "http://www.w3.org/2001/XMLSchema";
        envelope.bodyOut = rpc;
        envelope.dotNet = true;//注意跟服务器对应，如果服务器用.net开发，就为true
        new MarshalBase64().register(envelope);

        // 第五步：构建传输对象，并指明WSDL文档URL
        HttpTransportSE ht = new HttpTransportSE(FEndPoint);
        ht.debug = true;

        // 第六步:调用WebService(其中参数为1：nameSpace+方法名称，2：Envelope对象)
        try {
            ht.call(nameSpace + functionName, envelope);
            Object object = (Object) envelope.getResponse();
            // 第七步：解析返回数据
            String result = "";
            if (object != null) result = object.toString();
            if (result == null || result.isEmpty() || result.equals("anyType{}")) result = "";
            return result;
        } catch (Exception e) {
            return "";
        }
    }
}
