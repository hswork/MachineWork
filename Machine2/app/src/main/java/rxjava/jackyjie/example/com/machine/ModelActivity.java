package rxjava.jackyjie.example.com.machine;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import rxjava.jackyjie.example.com.machine.web.model.DropDown;
import rxjava.jackyjie.example.com.machine.web.model.Message;

public class ModelActivity extends BaseActivity implements View.OnClickListener {

    TextView info;
    TextView title;
    private TextView tv_timer;
    private Timer timer ;

    private List<Message> messageList = new ArrayList<>();
    private MessageAdapter adapter;
    GridLayoutManager gridLayoutManager;
    private RecyclerView recyclerView;
    private int iColNum = 2; // 列数

    private String currentPage = ""; // 当前操作类型编号
    public View.OnFocusChangeListener mFocusChangedListener;
    private boolean bCard; // 是否检测到卡号


    @Override
    protected int getLayoutId() {
        return R.layout.activity_model;
    }

    @Override
    protected Handler getHandler() {
        return mHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();

        TextView tv_jitai = (TextView)findViewById(R.id.tv_jiTaiHao);
        TextView tv_act_name = (TextView)findViewById(R.id.tv_act_name);
        tv_timer = (TextView)findViewById(R.id.tv_timer);
        tv_jitai.setText(getValue("sMacNo"));
        tv_act_name.setText(getValue("sWorker"));
        timerStart();
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
        adapter.setmFocusChangedListener(mFocusChangedListener);
    }

    protected void initView() {
        recyclerView = (RecyclerView)findViewById(R.id.main_view);
        gridLayoutManager = new GridLayoutManager(this, iColNum);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new MessageAdapter(messageList, iColNum);
        recyclerView.setAdapter(adapter);

        Button btn_cancel = (Button)findViewById(R.id.btn_cancel);
        Button btn_sure = (Button)findViewById(R.id.btn_sure);
        info  = (TextView)findViewById(R.id.info);
        title = (TextView)findViewById(R.id.title);

        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

        // api21以下padding问题
        LinearLayout padding1 = (LinearLayout)findViewById(R.id.padding1);

        padding1.setPadding(60, 0, 60, 10);
        recyclerView.setPadding(4, 4, 4,4);
    }

    protected void initData() {

        initFirstModel();
    }

    /*
    * 初始化iColNum
    * */
    private void initIColNum(int currentColNum){
        if(currentColNum > 0 && iColNum != currentColNum ){
            iColNum = currentColNum;
            gridLayoutManager = new GridLayoutManager(this, iColNum);
            recyclerView.setLayoutManager(gridLayoutManager);
            adapter = new MessageAdapter(messageList, iColNum);
            recyclerView.setAdapter(adapter);
        }
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

        if(hasCursorInput() && DataCenter.focusView != null){
            EditText editText = (EditText) DataCenter.focusView;
            editText.setText(data);
            editText.setSelection(editText.getText().length());
        }
        else {
            android.os.Message msg = new android.os.Message();
            msg.what = UsbService.MESSAGE_FROM_SERIAL_PORT;
            msg.obj = data;
            mHandler.sendMessage(msg);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.btn_sure:
                initModel();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<ModelActivity> mActivity;
        public MyHandler(ModelActivity activity) {
            mActivity = new WeakReference<ModelActivity>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            ModelActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {

                switch (msg.what) {
                    case 0x001:
                        // 页面显示
                        mSubActivity.initIColNum(mSubActivity.getIntValue("iColNum"));
                        mSubActivity.adapter.notifyDataSetChanged();

                        // 如果有延迟 则在多少秒后自动关闭
                        int delay = mSubActivity.getIntValue("nDelay");
                        if (delay >= 0){
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    android.os.Message msg = new android.os.Message();
                                    msg.what = 0x002;
                                    handleMessage(msg);
                                }
                            }, delay * 1000);
                        }
                        break;
                    case 0x002:
                        mSubActivity.finish();
                        break;
                    case 0x003:
                        mSubActivity.info.setText(msg.obj.toString());
                        break;
                    case 0x004:
                        mSubActivity.title.setText(msg.obj.toString());
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
                            }
                        }
                        break;
                }
            }
        }
    };

    // 首页
    private void initFirstModel(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> params = new ArrayList<>();
                params.add(getValue( "sMachineGroupNo"));
                params.add(getValue( "sMacNo"));
                params.add(getValue( "sWorkNo"));
                params.add("");
                params.add(MenuActivity.lastPage);
                WebApis.sendWebServiceByMethod(DataCenter.PROC_FIRST_OPERATE_STEP_FLOW, params);
                if(DataCenter.FirstOperateStepFLowModel.bFlag){

                    currentPage = DataCenter.FirstOperateStepFLowModel.extra.sOperateTypeID;

                    // 下拉框参数获取
                    for(Message item : DataCenter.FirstOperateStepFLowModel.t){
                        if(item.bu == 1) {
                            List<String> params2 = new ArrayList<>();
                            params2.add(getValue( "sMachineGroupNo"));
                            params2.add(getValue( "sMacNo"));
                            params2.add(getValue( "sWorkNo"));
                            params2.add("");
                            params2.add(MenuActivity.lastPage);
                            params2.add(currentPage);
                            params2.add(item.sn);
                            WebApis.sendWebServiceByMethod(DataCenter.PROC_DROP_DOWN, params2);

                            List<DropDown>dropDownList = new ArrayList<>();
                            if(DataCenter.DropDownModel.bFlag){
                                dropDownList = DataCenter.DropDownModel.t;
                            }
                            item.dropDownList = dropDownList;
                        }
                    }

                    // message present data
                    copyToMessageForPresent(mHandler, DataCenter.FirstOperateStepFLowModel.t, messageList, getIntValue("iColNum") > 0 ? getIntValue("iColNum") : iColNum);

                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x001;
                    mHandler.sendMessage(msg);

                }
                else {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x003;
                    msg.obj = "当前操作流程不存在";
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }

    // 新的页面
    private void initModel(){

        // 当前页面json
        info.setText("");
        removeKeyValue("nDelay");
        final String jsonText = getJsonFromMessageListForPresent(messageList);

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                params.add(getValue("sWorkNo"));
                params.add("");
                params.add(MenuActivity.lastPage);
                params.add(currentPage);
                params.add(jsonText);
                WebApis.sendWebServiceByMethod(DataCenter.PROC_ON_SUBMIT, params);
                if(DataCenter.OnSubmitModel.bFlag){

                    currentPage = DataCenter.OnSubmitModel.extra.sOperateTypeID;

                    if(DataCenter.OnSubmitModel.extra.sOperateTypeID == null || "".equals(DataCenter.OnSubmitModel.extra.sOperateTypeID)) {
                        android.os.Message msg2 = new android.os.Message();
                        msg2.what = 0x002;
                        mHandler.sendMessage(msg2);
                        return;
                    }

                    // 下拉框参数获取
                    for(Message item : DataCenter.OnSubmitModel.t){
                        if(item.bu == 1) {
                            List<String> params2 = new ArrayList<>();
                            params2.add(getValue("sMachineGroupNo"));
                            params2.add(getValue( "sMacNo"));
                            params2.add(getValue("sWorkNo"));
                            params2.add("");
                            params2.add(MenuActivity.lastPage);
                            params2.add(currentPage);
                            params2.add(item.sn);
                            WebApis.sendWebServiceByMethod(DataCenter.PROC_DROP_DOWN, params2);

                            List<DropDown>dropDownList = new ArrayList<>();
                            if(DataCenter.DropDownModel.bFlag){
                                dropDownList = DataCenter.DropDownModel.t;
                            }
                            item.dropDownList = dropDownList;
                        }
                    }

                    // message present data
                    copyToMessageForPresent(mHandler, DataCenter.OnSubmitModel.t, messageList, getIntValue("iColNum") > 0 ? getIntValue("iColNum") : iColNum);

                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x001;
                    mHandler.sendMessage(msg);

                }
                else {
                    android.os.Message msg = new android.os.Message();
                    msg.what = 0x003;
                    msg.obj = DataCenter.OnSubmitModel.message;
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }
}
