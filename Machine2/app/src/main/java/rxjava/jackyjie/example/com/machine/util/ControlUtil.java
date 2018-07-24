package rxjava.jackyjie.example.com.machine.util;

import android.graphics.Paint;

public class ControlUtil {

    /*
    * 获取字符串得长度
    * text is value, size is font size
    */
    public static int getCharacterWidth(String text, float size) {
        if (null == text || "".equals(text)){
            return 0;

        }

        Paint paint = new Paint();
        paint.setTextSize(size);
        int text_width = (int) paint.measureText(text);// 得到总体长度
        // int width = text_width/text.length();//每一个字符的长度
        return text_width;
    }
}
