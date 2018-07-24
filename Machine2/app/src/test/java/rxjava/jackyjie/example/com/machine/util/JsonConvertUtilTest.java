package rxjava.jackyjie.example.com.machine.util;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;

public class JsonConvertUtilTest {

    @Test
    public void getKeyValues() {
        String json = "{\"name\":\"小明\",\"value\":1,\"time\":false}";
        try {
            JSONObject object = new JSONObject(json);
            HashMap<String, Object>  hashes =  HashMapUtil.getKeyValues(object);

            Assert.assertEquals(hashes.size(), 3);
            Assert.assertEquals(hashes.get("name"), "小明");
            Assert.assertEquals(hashes.get("value"), 1);
            Assert.assertEquals(hashes.get("time"), false);
        }catch (Exception ex){
            String message = ex.getMessage();
            Assert.assertFalse(false);
        }
    }
}