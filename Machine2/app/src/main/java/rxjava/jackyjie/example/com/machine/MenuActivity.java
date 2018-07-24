package rxjava.jackyjie.example.com.machine;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
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
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rxjava.jackyjie.example.com.machine.adapter.OperateAdapter;
import rxjava.jackyjie.example.com.machine.base.BaseActivity;
import rxjava.jackyjie.example.com.machine.interfaces.OperateListener;
import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.WebApis;
import rxjava.jackyjie.example.com.machine.web.model.OperateStepFLow;

public class MenuActivity extends BaseActivity implements View.OnClickListener{

    private TextView tv_timer;
    private Timer timer ;

    private int currentPage = 0; // 页编号即流程编号
    private int pageAction = 1; // 下一步 1, 上一步 0

    public static String lastPage = ""; // 最后一次点击的流程编号
    List<OperateStepFLow> operateList = new ArrayList<>();
    OperateAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_menu;
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
        tv_jitai.setText(getValue( "sMacNo"));
        tv_act_name.setText(getValue("sWorker"));
        timerStart();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);


    }

    protected void initView() {

        final RecyclerView recyclerView = (RecyclerView)findViewById(R.id.main_view);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4);
        recyclerView.setLayoutManager(gridLayoutManager);
        adapter = new OperateAdapter(operateList, new OperateListener(){
            @Override
            public void OnClick(int iBreakEnd, String currentStep) {
                if(iBreakEnd == 1){
                    lastPage = currentStep;
                    Intent intent = new Intent(MenuActivity.this, ModelActivity.class);
                    startActivity(intent);
//                    Toast.makeText(MenuActivity.this, "到底了", Toast.LENGTH_SHORT).show();
                    return;
                }

                currentPage = Integer.parseInt(currentStep);
                pageAction = 1;

                // 下一步菜单
                initMenu();
            }
        });
        recyclerView.setAdapter(adapter);

        ImageView img_menu_main = (ImageView)findViewById(R.id.img_menu_main);
        Button btn_menu_main = (Button)findViewById(R.id.btn_menu_main);

        img_menu_main.setOnClickListener(this);
        btn_menu_main.setOnClickListener(this);

        // api21以下padding问题
        recyclerView.setPadding(4, 4, 4,4);
    }

    protected void initData() {

        // 设置menu
        initMenu();
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
        switch (view.getId()){
            case R.id.img_menu_main:
            case R.id.btn_menu_main:
                setResult(RESULT_OK);
                finish();
                break;
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        if(currentPage == 0){
            setResult(RESULT_OK);
            this.finish();
        }
        else {
            pageAction = 0;
            initMenu();
        }
    }

    private MyHandler mHandler = new MyHandler(this);
    private static class MyHandler extends Handler {
        private final WeakReference<MenuActivity> mActivity;
        public MyHandler(MenuActivity activity) {
            mActivity = new WeakReference<MenuActivity>(activity);
        }
        @Override
        public void handleMessage(android.os.Message msg) {
            MenuActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {

                switch (msg.what) {
                    case 0x001:
                        mSubActivity.adapter.notifyDataSetChanged();
                        break;
                    case 0x002:
                        Toast.makeText(mSubActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
                    case TIMER_CODE:
                        mSubActivity.tv_timer.setText(msg.obj.toString());
                        break;
                }
            }
        }
    };

    private void initMenu(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> params = new ArrayList<>();
                params.add(getValue( "sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                params.add(getValue( "sWorkNo"));
                params.add(Integer.toString(currentPage));
                params.add(Integer.toString(pageAction));
                WebApis.sendWebServiceByMethod(DataCenter.PROC_OPERATE_STEP_FLOW, params);
                if(DataCenter.OperateStepFlowModel.bFlag){

                    if(pageAction == 0){
                        currentPage = DataCenter.OperateStepFlowModel.extra.iPid;
                    }

                    operateList.clear();
                    for(OperateStepFLow item :DataCenter.OperateStepFlowModel.t){
                        operateList.add(item);
                    }
                    Message msg = new Message();
                    msg.what = 0x001;
                    mHandler.sendMessage(msg);
                }
                else {
                    Message msg = new Message();
                    msg.what = 0x002;
                    msg.obj = "下一步获取失败";
                    mHandler.sendMessage(msg);
                }
            }
        }).start();
    }
}
