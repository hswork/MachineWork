package rxjava.jackyjie.example.com.machine;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import rxjava.jackyjie.example.com.machine.base.BaseActivity;
import rxjava.jackyjie.example.com.machine.service.UsbService;
import rxjava.jackyjie.example.com.machine.util.CommonUtil;
import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.WebApis;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText edit_card;
    private TextView tv_timer;
    private Timer timer ;
    private boolean bCard; // 是否检测到卡号

    @Override
    protected int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    protected Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

        TextView tv_jitai = (TextView)findViewById(R.id.tv_jiTaiHao);
        TextView tv_act_name = (TextView)findViewById(R.id.tv_act_name);
        tv_timer = (TextView)findViewById(R.id.tv_timer);
        tv_act_name.setText(getValue("sWorker"));
        tv_jitai.setText(getValue("sMacNo"));
        timerStart();

        View.OnFocusChangeListener mFocusChangedListener;
        mFocusChangedListener = new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                DataCenter.hasFocusOn = hasFocus;
                if(!hasFocus){
                    DataCenter.focusView = null;
                }
                else {
                    DataCenter.focusView = v;
                }
            }
        };
        edit_card.setOnFocusChangeListener(mFocusChangedListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
    }

    protected void initView() {

        ImageView image_main = (ImageView)findViewById(R.id.image_main);
        Button btn_main = (Button)findViewById(R.id.btn_main);
        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        Button btn_sure = (Button)findViewById(R.id.btn_sure);
        edit_card = (EditText)findViewById(R.id.edit_card);

        image_main.setOnClickListener(this);
        btn_main.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

        // api21以下padding问题
        LinearLayout padding1 = (LinearLayout)findViewById(R.id.layout_padding1);
        LinearLayout padding2 = (LinearLayout)findViewById(R.id.layout_padding2);

        padding1.setPadding(10, 10, 10,10);
        padding2.setPadding(60, 0, 60,10);
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
        timer.cancel();
        endRfid();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.image_main:
            case R.id.btn_main:
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.btn_sure:
                login(null);
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<LoginActivity> mActivity;
        public MyHandler(LoginActivity activity) {
            mActivity = new WeakReference<LoginActivity>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            LoginActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {

                switch (msg.what) {
                    case 0x001:
                        String obj = msg.obj.toString();
                        Toast.makeText(mSubActivity, obj, Toast.LENGTH_SHORT).show();
                        break;
                    case 0x002:
                        mSubActivity.setResult(RESULT_OK);
                        mSubActivity.finish();
                        break;
                    case TIMER_CODE:
                        mSubActivity.tv_timer.setText(msg.obj.toString());
                        break;
                    case UsbService.MESSAGE_FROM_SERIAL_PORT:
                        // 解析字符串 如果存在ID
                        String params = msg.obj.toString().trim();
                        if(!mSubActivity.bCard) {
                            if(params.split("IDcard").length > 1){
                                HashMap<String, String> hashes =  CommonUtil.paramsToMap(params);
                                String cardID = Long.toString(CommonUtil.hex2RfidHex(hashes.get("IDcard")));
                                if(mSubActivity.hasCursorInput() && DataCenter.focusView != null){
                                    mSubActivity.bCard = true;
                                    EditText editText = (EditText) DataCenter.focusView;
                                    editText.setText(cardID);
                                    editText.setSelection(editText.getText().length());
                                    mSubActivity.bCard = false;
                                }
                                else {
                                    mSubActivity.bCard = true;
                                    mSubActivity.login(cardID);
                                }
                            }
                        }
                        break;
                }
            }
        }
    };

    // 登录操作
    // 获取菜单
    private void login(String IcCard){

        String editText;
        if(IcCard == null || "".equals(IcCard)) {
            editText = edit_card.getText().toString().trim();
            if ("".equals(editText)) {
                Toast.makeText(LoginActivity.this, "卡号不能为空", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        else {
            editText = IcCard;
        }

        final String cardID = editText;

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> params = new ArrayList<>();
                params.add(DataCenter.globalData.get("sMachineGroupNo").toString());
                params.add(DataCenter.globalData.get("sMacNo").toString());
                params.add(cardID);
                WebApis.sendWebServiceByMethod(DataCenter.PROC_CARD_INFO, params);
                if(DataCenter.CardInfoModel.bFlag) {
                    // 添加卡号
                    setKeyValue("sCardNo", cardID);

                    // 添加员工号
                    for(rxjava.jackyjie.example.com.machine.web.model.Message item :DataCenter.CardInfoModel.t){
                        if(item.sn.equals("sWorkNo")){

                            setKeyValue( "sWorkNo", item.sv);
                            break;
                        }
                    }

                    // 关闭并跳转页面
                    setResult(RESULT_LOGIN);
                    finish();
                }
                else {
                    // 弹出错误信息
                    String msgInfo = DataCenter.CardInfoModel.t.get(0).sv;
                    Message msg = new Message();
                    msg.what = 0x001;
                    msg.obj = msgInfo;
                    mHandler.sendMessage(msg);

                }
            }
        }).start();
    }
}
