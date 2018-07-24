package rxjava.jackyjie.example.com.machine;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import rxjava.jackyjie.example.com.machine.base.BaseActivity;
import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.NetUtil;
import rxjava.jackyjie.example.com.machine.web.WebApis;

public class StartActivity extends BaseActivity {

    private TextView progress_text;
    private static final int DOWNLOAD = 0x001;
    private static final int MACHINE_STATUS_TYPE = 0x002;
    private static final int MACHINE_TYPE = 0x003;
    private static final int MACHINE_SIGNAL = 0x004;
    private static final int SERVER_TIME = 0x005;
    private static final int FINISH = 0x006;

    public static int timerSpan = 200;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_start;
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
    }

    protected void initView() {
        progress_text = (TextView) findViewById(R.id.step);
    }

    protected void initData() {
        // 初始化前页面显示
        progress_text.setText(getString(R.string.load_zero));

        // 初始化本地数据
        boolean flag = initLocalData();

        // 本地数据初始化成功开始获取基础数据
        if (flag) {
            if (!NetUtil.isNetworkAvailable(StartActivity.this)) {
                progress_text.setText("本地网络不可使用，请检查..");
            } else {
                mHandler.sendEmptyMessage(DOWNLOAD);
            }
        } else {
            setResult(RESULT_FIRST_USER);
            this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        setResult(RESULT_CANCELED);
    }

    private MyHandler mHandler = new MyHandler(this);

    private static class MyHandler extends Handler {
        private final WeakReference<StartActivity> mActivity;

        public MyHandler(StartActivity activity) {
            mActivity = new WeakReference<StartActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            StartActivity mSubActivity = mActivity.get();
            if (mSubActivity != null) {
                switch (msg.what) {
                    case DOWNLOAD:
                        mSubActivity.progress_text.setText(R.string.load_first);
                        mSubActivity.handleDownload();
                        break;
                    case MACHINE_SIGNAL:
                        mSubActivity.progress_text.setText(R.string.load_second);
                        mSubActivity.handleMachineSignal();
                        break;
                    case MACHINE_STATUS_TYPE:
                        mSubActivity.progress_text.setText(R.string.load_third);
                        mSubActivity.handleMachineStatusType();
                        break;
                    case MACHINE_TYPE:
                        mSubActivity.progress_text.setText(R.string.load_fourth);
                        mSubActivity.handleMachineType();
                        break;
                    case SERVER_TIME:
                        mSubActivity.progress_text.setText(R.string.load_fifth);
                        mSubActivity.handleServerTime();
                        break;
                }
            }
        }
    }

    // 初始化本地数据
    private boolean initLocalData() {

        SharedPreferences sharedPref = getSharedPreferences("User", Context.MODE_PRIVATE);
        String IP = sharedPref.getString(getString(R.string.ip), "");
        String Port = sharedPref.getString(getString(R.string.port), "");
        String jiTaiID = sharedPref.getString(getString(R.string.jitaiID), "");
        String jiTaiType = sharedPref.getString(getString(R.string.jitaiType), "");
        if ("".equals(IP) || "".equals(Port) || "".equals(jiTaiID) || "".equals(jiTaiType)) {
            return false;
        } else {

            setKeyValue("localIP", IP); // IP
            setKeyValue("localPort", Port); // 端口
            setKeyValue("localUrl", "http://" + IP + ":" + Port + "/QueryService.asmx");
            setKeyValue("sMacNo", jiTaiID); // 机台
            setKeyValue("sMachineGroupNo", jiTaiType); // 机组
            WebApis.sUrl = getValue("localUrl");
            return true;
        }
    }

    // 上电下载任务
    private void handleDownload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_DOWNLOAD_MACHINE, params);
                if (bFlag) {
                    try {
                        Thread.sleep(timerSpan);
                    } catch (Exception ex) {

                    }
                    mHandler.sendEmptyMessage(MACHINE_SIGNAL);
                }
            }
        }).start();
    }

    // 获取织机信号
    private void handleMachineSignal() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_MACHINE_SIGNAL, params);
                if (bFlag) {
                    try {
                        Thread.sleep(timerSpan);
                    } catch (Exception ex) {

                    }
                    mHandler.sendEmptyMessage(MACHINE_STATUS_TYPE);
                }
            }
        }).start();
    }

    // 获取织机所有状态
    private void handleMachineStatusType() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_MACHINE_STATUS_TYPE, params);
                if (bFlag) {
                    try {
                        Thread.sleep(timerSpan);
                    } catch (Exception ex) {

                    }
                    mHandler.sendEmptyMessage(MACHINE_TYPE);
                }
            }
        }).start();
    }

    // 获取织机所有机型
    private void handleMachineType() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_MACHINE_TYPE, params);
                if (bFlag) {
                    try {
                        Thread.sleep(timerSpan);
                    } catch (Exception ex) {

                    }
                    mHandler.sendEmptyMessage(SERVER_TIME);
                }
            }
        }).start();
    }

    // 服务器时间
    private void handleServerTime() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<String> params = new ArrayList<>();
                params.add(getValue("sMachineGroupNo"));
                params.add(getValue("sMacNo"));
                boolean bFlag = WebApis.sendWebServiceByMethod(DataCenter.PROC_SERVER_DATETIME, params);
                if (bFlag) {
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }).start();
    }
}
