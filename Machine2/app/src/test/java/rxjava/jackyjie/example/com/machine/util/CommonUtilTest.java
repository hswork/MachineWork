package rxjava.jackyjie.example.com.machine.util;

import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class CommonUtilTest {

    @Test
    public void paramsToMap() {
        String data = "IDcard=33253";
        HashMap<String, String> hashes = CommonUtil.paramsToMap(data);
        Assert.assertEquals(hashes.size(), 1);
        Assert.assertEquals(hashes.get("IDcard"), "33253");
    }

    @Test
    public void hexToDecimal() {
        String data = "E41F1441AE";
        long result = CommonUtil.hex2RfidHex(data);
        Assert.assertEquals(result, 1091837924);
    }

    @Test
    public void addKeyValue() {
        HashMap<String, Object> hashes = new HashMap<>();
        hashes.put("name", "小明");
        HashMapUtil.addKeyValue(hashes, "name", "小红");
        HashMapUtil.addKeyValue(hashes, "value", "小赵");
        Assert.assertEquals(hashes.get("name"), "小明");
        Assert.assertEquals(hashes.get("value"), "小赵");
    }
}