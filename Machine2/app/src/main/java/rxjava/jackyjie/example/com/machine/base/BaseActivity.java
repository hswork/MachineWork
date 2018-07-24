package rxjava.jackyjie.example.com.machine.base;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import rxjava.jackyjie.example.com.machine.service.UsbService;
import rxjava.jackyjie.example.com.machine.util.HashMapUtil;
import rxjava.jackyjie.example.com.machine.util.SystemUtil;
import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.model.Message;

public abstract class BaseActivity extends AppCompatActivity {

    public static final int TOAST_CODE = 0x010; // 提示框
    public static final int TIMER_CODE = 0x011; // 时间
    public static final int FRESH_CODE = 0x012; // listView更新
    public static final int RESULT_LOGIN = 0x009; // 登录

    private UsbService usbService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1.页面加载之前设置
        this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,// 设置全屏
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(getLayoutId());
        // 2.页面加载之后设置
        if (getSupportActionBar() != null){ // 隐藏标题栏
            getSupportActionBar().hide();
        }
        handler = getHandler();
    }

    /**
     * 接口
     * this activity layout res
     * 设置layout布局,在子类重写该方法.
     * @return res layout xml id
     */
    protected abstract int getLayoutId();

    /*
    * Handler
    * getHandler
    * */
    protected abstract Handler getHandler();

    /*
     * from hashMap
    * getGlobalValue
    * */
    protected String getValue(String name){
        return HashMapUtil.getStringValue(DataCenter.globalData, name);
    }

    /*
     * from hashMap
     * getIntGlobalValue
     * */
    protected int getIntValue(String name){
        return HashMapUtil.getIntValue(DataCenter.globalData, name);
    }

    /*
     * from hashMap
     * getLongGlobalValue
     * */
    protected long getLongValue(String name){
        return HashMapUtil.getLongValue(DataCenter.globalData, name);
    }

    /*
     * from hashMap
     * setGlobalValue
     * */
    protected void setKeyValue(String name, String value){
        HashMapUtil.addKeyValue(DataCenter.globalData, name, value);
        return;
    }

    /*
     * from hashmap
    * remove GlobalValue
    * */
    protected void removeKeyValue(String name){
        HashMapUtil.removeKeyValue(DataCenter.globalData, name);
        return;
    }

    /*
    * copy Value
    * from apiReturnResult
    * to Messagelist on present
    * return hasTitle
    * */
    protected void copyToMessageForPresent(Handler handler, List<Message> from , List<Message> to, int iColNum){
        boolean flag = false;
        to.clear();
        for(Message item : from){
            if("sTitle".equals(item.sn)){
                if(handler != null) {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x004;
                    msg.obj = item.sv;
                    handler.sendMessage(msg);
                }
            }
            else {
                to.add(item);
            }
        }
        while(to.size() % iColNum != 0){
            Message msg = new Message();
            msg.st = "";
            msg.sv = "";
            msg.sn = "";
            msg.br = 1;
            msg.bc = 0;
            msg.bu = 0;
            to.add(msg);
        }
    }

    /*
    * getValue
    * Submit
    * Delete null
    * */
    protected String getJsonFromMessageListForPresent(List<Message> from){
        StringBuilder json = new StringBuilder();
        for(Message msg : from){
            if("".equals(msg.sn)) continue;
            if(!("".equals(json.toString()))) {
                json.append(",");
            }
            json.append(",\""+msg.sn+"\":\""+msg.sv+"\"");
        }
        json.insert(0, "{");
        json.append("}");
        return json.toString();
    }

    /*
    * set systemSystem
    * from tDateTime
    * */
    protected boolean setSsytemTime(Context context, long millions){

        return SystemUtil.setSystemTime(context, millions);
    }

    /*
    * has Cursor for input
    * can card id to input
    * */
    protected boolean hasCursorInput(){
        return DataCenter.hasFocusOn;
    }


    // serviceConnection
    protected final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    protected void setFilters() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        filter.addAction(UsbService.ACTION_SEND_MESSAGE);
        registerReceiver(mUsbReceiver, filter);
    }

    // 开启服务
    protected void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /*
     * Notifications from UsbService will be received here.
     */
    protected final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    // 如果有输入框光标 则输入数字
                    // 如果没有则刷卡
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_SEND_MESSAGE:

                    final String data = intent.getStringExtra("str");
                    android.os.Message msg = new android.os.Message();
                    msg.what = UsbService.MESSAGE_FROM_SERIAL_PORT;
                    msg.obj = data;
                    handler.sendMessage(msg);
                    break;
            }
        }
    };

    /*
    * handle info
    * rfid info
    * */
    protected  void rfidHandle(String data){};

    protected void startRfid(){
        setFilters();  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    protected void endRfid(){
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }
}
