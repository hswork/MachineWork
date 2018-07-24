package rxjava.jackyjie.example.com.machine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rxjava.jackyjie.example.com.machine.adapter.MessageAdapter;
import rxjava.jackyjie.example.com.machine.base.BaseActivity;
import rxjava.jackyjie.example.com.machine.web.NetUtil;
import rxjava.jackyjie.example.com.machine.web.model.Message;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

    TextView info;
    private TextView tv_timer;
    private Timer timer ;

    List<Message> messageList = new ArrayList<>();
    MessageAdapter adapter;
    int iColNum = 1; // 行数

    @Override
    protected int getLayoutId() {
        return R.layout.activity_setting;
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

        tv_act_name.setText(getValue("sWorker"));
        tv_jitai.setText(getValue("sMacNo"));
        timerStart();
    }

    protected void initView() {
        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, iColNum);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new MessageAdapter(messageList, iColNum);
        recyclerView.setAdapter(adapter);

        ImageView image_setting = (ImageView)findViewById(R.id.image_setting);
        Button btn_setting = (Button)findViewById(R.id.btn_setting);
        Button  btn_cancel= (Button)findViewById(R.id.btn_cancel);
        Button btn_sure = (Button)findViewById(R.id.btn_sure);
        info = (TextView) findViewById(R.id.info);

        image_setting.setOnClickListener(this);
        btn_setting.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);
        btn_sure.setOnClickListener(this);

        // api21以下padding问题
        recyclerView.setPadding(4, 4, 4,4);
        LinearLayout padding1 = (LinearLayout)findViewById(R.id.padding1);
        padding1.setPadding(60, 0, 60, 10);
    }

    protected void initData() {

        initMessage();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        timerEnd();
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
            case R.id.btn_setting:
            case R.id.image_setting:
            case R.id.btn_cancel:
                this.finish();
                break;
            case R.id.btn_sure:
                // 保存设置
                sureSetting();
                break;
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<SettingActivity> mActivity;
        public MyHandler(SettingActivity activity) {
            mActivity = new WeakReference<SettingActivity>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            SettingActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {

                switch (msg.what) {
                    case 0x001:
                        // 页面显示
                        mSubActivity.info.setText(msg.obj.toString());
                        break;
                    case TIMER_CODE:
                        mSubActivity.tv_timer.setText(msg.obj.toString());
                        break;
                }
            }
        }
    };

    private void sureSetting(){
        // 验证是否有修改
        boolean bChanged = false;

        // 错误验证
        android.os.Message msg =new android.os.Message();
        if(!NetUtil.isUrlByString(messageList.get(0).sv.trim())){
            msg.what = 0x001;
            msg.obj = "IP格式不正确，请重新输入";
            mHandler.sendMessage(msg);
            return;
        }
        if(!NetUtil.isPortString(messageList.get(1).sv.trim())){
            msg.what = 0x001;
            msg.obj = "端口格式不正确，请重新输入";
            mHandler.sendMessage(msg);
            return;
        }
        if("".equals(messageList.get(2).sv.trim())){
            msg.what = 0x001;
            msg.obj = "机台类型不能为空，请重新输入";
            mHandler.sendMessage(msg);
            return;
        }
        if("".equals(messageList.get(3).sv.trim())){
            msg.what = 0x001;
            msg.obj = "机台号不能为空，请重新输入";
            mHandler.sendMessage(msg);
            return;
        }

        SharedPreferences sp = getSharedPreferences("User", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        boolean bChange = false ;

        String IP = getValue( "localIP");
        String Port= getValue( "localPort");
        String JiTaiType =getValue("sMachineGroupNo");
        String JiTaiHao =getValue("sMacNo");

        if(!(messageList.get(0).sv.trim().equals(IP))){
            edit.putString(getString(R.string.ip), messageList.get(0).sv.trim());
            bChange = true;
        }
        if(!(messageList.get(1).sv.trim().equals(Port))){
            edit.putString(getString(R.string.port), messageList.get(1).sv.trim());
            bChange = true;
        }
        if(!(messageList.get(2).sv.trim().equals(JiTaiType))){
            edit.putString(getString(R.string.jitaiType), messageList.get(2).sv.trim());
            bChange = true;
        }
        if(!(messageList.get(3).sv.trim().equals(JiTaiHao))){
            edit.putString(getString(R.string.jitaiID), messageList.get(3).sv.trim());
            bChange = true;
        }
        edit.commit();
        if(bChange) {
            setResult(RESULT_OK);
        }
        this.finish();
    }

    private void initMessage(){
        messageList.clear();

        Message item = new Message();
        item.sn = "IP";
        item.st = "IP";
        item.sv =getValue("localIP");
        item.br = 0;
        item.bc = 1;
        item.bu = 0;
        messageList.add(item);

        item = new Message();
        item.sn = "Port";
        item.st = "端口";
        item.sv =getValue("localPort");
        item.br = 0;
        item.bc = 1;
        item.bu = 0;
        messageList.add(item);

        item = new Message();
        item.sn = "jitaiType";
        item.st = "机台类型";
        item.sv =  getValue("sMachineGroupNo");
        item.br = 0;
        item.bc = 1;
        item.bu = 0;
        messageList.add(item);

        item = new Message();
        item.sn = "jitaiHao";
        item.st = "机台号";
        item.sv = getValue( "sMacNo");
        item.br = 0;
        item.bc = 1;
        item.bu = 0;
        messageList.add(item);
        adapter.notifyDataSetChanged();
    }

}
