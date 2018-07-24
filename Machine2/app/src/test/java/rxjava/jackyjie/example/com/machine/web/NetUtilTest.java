package rxjava.jackyjie.example.com.machine.web;

import org.junit.Assert;
import org.junit.Test;

public class NetUtilTest {

    @Test
    public void isUrlByString() {

        String url = "192.168.1.126:868";
        String url2 = "192.168.1.128";
        boolean result = NetUtil.isUrlByString(url);
        boolean result2 = NetUtil.isUrlByString(url2);
        Assert.assertEquals(result, false);
        Assert.assertEquals(result2, true);

    }
}