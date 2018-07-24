package rxjava.jackyjie.example.com.machine;

import android.app.Instrumentation;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rxjava.jackyjie.example.com.machine.adapter.MessageAdapter;
import rxjava.jackyjie.example.com.machine.base.BaseActivity;
import rxjava.jackyjie.example.com.machine.service.UsbService;
import rxjava.jackyjie.example.com.machine.util.CommonUtil;
import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.WebApis;
import rxjava.jackyjie.example.com.machine.web.model.Message;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_timer;
    private TextView tv_jitai;
    private TextView tv_act_name;
    private Timer timer ;

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private int iColNum = 2; // 列数

    private final int START_CODE = 0x001;
    private final int SETTING_CODE = 0x002;
    private final int LOGIN_CODE = 0x003;
    private final int REFRESH_CODE = 0x004;
    private boolean bCard; // 是否检测到卡号

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();

        tv_jitai = (TextView)findViewById(R.id.tv_jiTaiHao);
        tv_act_name = (TextView)findViewById(R.id.tv_act_name);
        tv_timer = (TextView)findViewById(R.id.tv_timer);
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        startActivityForResult(intent, START_CODE);
    }

    protected void initView() {

        recyclerView = (RecyclerView)findViewById(R.id.main_view);
        gridLayoutManager = new GridLayoutManager(this, iColNum){
            @Override
            public int getSpanCount() {
                return iColNum;
            }
        };
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new MessageAdapter(messageList, iColNum);
        recyclerView.setAdapter(adapter);

        ImageView image_login = (ImageView)findViewById(R.id.image_login);
        ImageView image_setting = (ImageView)findViewById(R.id.image_setting);
        Button btn_setting = (Button)findViewById(R.id.btn_setting);
        Button btn_login = (Button)findViewById(R.id.btn_login);

        image_login.setOnClickListener(this);
        image_setting.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_login.setOnClickListener(this);

        // api21以下padding问题
        recyclerView.setPadding(4, 4, 4,4);
    }

    /*
     * 标题栏时间更新
     * */
    private void timerStart(){

        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long time = System.currentTimeMillis();//long now = android.os.SystemClock.uptimeMillis();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date d1 = new Date(time);
                String t1 = format.format(d1);

                android.os.Message msg = new android.os.Message();
                msg.what = TIMER_CODE;
                msg.obj = t1;
                mHandler.sendMessage(msg);
            }
        }, 0, 1000);
    }

    /*
     * 标题栏时间关闭
     * */
    private void timerEnd(){

        if(timer == null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        timerStart();
        startRfid();
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerEnd();
        endRfid();
    }

    @Override
    protected void rfidHandle(String data) {

        android.os.Message msg = new android.os.Message();
        msg.what = UsbService.MESSAGE_FROM_SERIAL_PORT;
        msg.obj = data;
        mHandler.sendMessage(msg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.image_login:
            case R.id.btn_login:
                intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent, LOGIN_CODE); // 启动登录页面
                break;
            case R.id.btn_setting:
            case R.id.image_setting:
                intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivityForResult(intent, SETTING_CODE);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_LOGIN){
            Intent intent = new Intent(MainActivity.this, MenuActivity.class);
            startActivity(intent);
        }

        switch (requestCode){
            case START_CODE:
                if(resultCode == RESULT_CANCELED){
                    // 如果启动页面未完成
                }
                else if(resultCode == RESULT_FIRST_USER){
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    startActivity(intent);
                }
                else if(resultCode == RESULT_OK){
                    // 设置基础信息
                    tv_act_name.setText(getValue("sWorker"));
                    tv_jitai.setText(getValue("sMacNo"));
                    // 重置系统时间
                    if(setSsytemTime(getApplicationContext(), getLongValue("tDateTime"))) {
                        timerStart();
                    }
                    requestMainMessage();
                }
                break;
            case SETTING_CODE:
                if(resultCode == RESULT_CANCELED){
                }
                else if(resultCode == RESULT_OK){
                    // 重启服务
                    Intent intent = new Intent(MainActivity.this, StartActivity.class);
                    startActivityForResult(intent, START_CODE);
                }
                break;
            case LOGIN_CODE:
                if(resultCode == RESULT_OK){
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                }
                break;
            case REFRESH_CODE:
                if(resultCode == RESULT_OK){
                    mainShow();
                }
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;
        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            MainActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {

                switch (msg.what) {
                    case 0x001:
                        mSubActivity.mainShow();
                        break;
                    case 0x002:
                        Toast.makeText(mSubActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case TIMER_CODE:
                        mSubActivity.tv_timer.setText(msg.obj.toString());
                        break;
                    case UsbService.MESSAGE_FROM_SERIAL_PORT:
                        // 解析字符串 如果存在ID
                        String params = msg.obj.toString();
                        if(!mSubActivity.bCard) {
                            if(params.split("IDcard").length > 1){
                                mSubActivity.bCard = true;
                                mSubActivity.handleICCard(msg.obj.toString().trim());
                            }
                        }
                        break;
                }
            }
        }
    };

    private void handleICCard(String ICCard){
        final HashMap<String, String> hashes =  CommonUtil.paramsToMap(ICCard);
        final String cardID = Long.toString(CommonUtil.hex2RfidHex(hashes.get("IDcard")));
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                params.add(cardID);
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_CARD_INFO, params);
                if(bFlag) {
                    // 添加卡号
                    setKeyValue( "sCardNo", cardID);

                    // 添加员工号
                    for(Message item :DataCenter.CardInfoModel.t){
                        if(item.sn.equals("sWorkNo")){
                            setKeyValue( "sWorkNo", item.sv);
                            break;
                        }
                    }
                    bCard = false;

                    // 跳转页面
                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivityForResult(intent, REFRESH_CODE);
                }
                else {
                    // 弹出错误信息
                    String msgInfo = DataCenter.CardInfoModel.t.get(0).sv;
                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x002;
                    msg.obj = msgInfo;
                    mHandler.sendMessage(msg);
                    bCard = false;
                }
            }
        }).start();
    }

    private void mainShow(){
        messageList.clear();
        for(Message msg : DataCenter.MainOperateModel.t){
            msg.sv = getValue(msg.sn);
            messageList.add(msg);
        }
        adapter.notifyDataSetChanged();
    }

    // 主页面请求
    private void requestMainMessage(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_MAIN_OPERATE, params);
                if(bFlag){
                    mHandler.sendEmptyMessage(0x001);
                }
            }
        }).start();
    }


    /*
     * Notifications from UsbService will be received here.
     */
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(MainActivity.this, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(MainActivity.this, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(MainActivity.this, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(MainActivity.this, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    // 如果有输入框光标 则输入数字
                    // 如果没有则刷卡
                    Toast.makeText(MainActivity.this, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_SEND_MESSAGE:

                    final String data = intent.getStringExtra("str");
                    boolean flag = false;
                    flag = hasCursorInput();
                    if(flag) {
                        Thread t = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Instrumentation instrumentation = new Instrumentation();
                                int keyCode = Integer.parseInt(data);
                                instrumentation.sendKeyDownUpSync(keyCode);
                            }
                        });
                    }
                    else {
                        android.os.Message msg = new android.os.Message();
                        msg.what = UsbService.MESSAGE_FROM_SERIAL_PORT;
                        msg.obj = data;
                        mHandler.sendMessage(msg);
                    }
                    break;
            }
        }
    };

}
