package rxjava.jackyjie.example.com.machine.util;

import org.junit.Test;

public class SystemUtilTest {

    @Test
    public void getDate() {

        long million = 1532346003;
        String result = SystemUtil.getDateFromMillions(million);
    }
}