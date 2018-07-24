package rxjava.jackyjie.example.com.machine;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rxjava.jackyjie.example.com.machine.web.DataCenter;
import rxjava.jackyjie.example.com.machine.web.WebApis;

public class APIActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "APIActivity";

    private final int TEST=0x001;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api);
        LinearLayout layout = (LinearLayout)findViewById(R.id.layout);
        int length = layout.getChildCount();
        for(int i = 0; i< layout.getChildCount();i++){
            View v = layout.getChildAt(i);
            if(v instanceof Button){
                v.setOnClickListener(this);
            }
        }
        textView = (TextView)findViewById(R.id.result);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        List<String> params = new ArrayList<>();
        String procName = "";
        switch (view.getId()){
            case R.id.btnCall:
                procName = DataCenter.PROC_ANSWER_CALL;
                params.add("11");
                params.add("2");
                params.add("22090");
                break;
            case R.id.download:
                procName = DataCenter.PROC_DOWNLOAD_MACHINE;
                params.add("11");
                params.add("2");
                break;
            case R.id.machineSignal:
                procName = DataCenter.PROC_MACHINE_SIGNAL;
                params.add("11");
                params.add("2");
                break;
            case R.id.machineStatusType:
                procName = DataCenter.PROC_MACHINE_SIGNAL;
                params.add("11");
                params.add("2");
                break;
            case R.id.machineType:
                procName = DataCenter.PROC_MACHINE_TYPE;
                break;
            case R.id.serverTime:
                procName = DataCenter.PROC_SERVER_DATETIME;
                params.add("11");
                params.add("2");
                break;
            case R.id.message:
                procName = DataCenter.PROC_MESSAGE;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("1");
                break;
            case R.id.artImage:
                procName = DataCenter.PROC_ART_IMAGE;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("17121005");
                break;
            case R.id.dropDown:
                procName = DataCenter.PROC_DROP_DOWN;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("");
                params.add("501");
                params.add("sCardNo");
                break;
            case R.id.mainOperate:
                procName = DataCenter.PROC_MAIN_OPERATE;
                params.add("11");
                params.add("2");
                break;
            case R.id.cardInfo:
                procName = DataCenter.PROC_MACHINE_SIGNAL;
                params.add("11");
                params.add("2");
                params.add("206078327");
                break;
            case R.id.operateFlow:
                procName = DataCenter.PROC_OPERATE_STEP_FLOW;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("0");
                params.add("1");
                break;
            case R.id.firstOperateFlow:
                procName = DataCenter.PROC_FIRST_OPERATE_STEP_FLOW;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("");
                params.add("5");
                break;
            case R.id.onSubmit:
                procName = DataCenter.PROC_ON_SUBMIT;
                params.add("11");
                params.add("2");
                params.add("22090");
                params.add("2");
                params.add("5");
                params.add("502");
                params.add("{\"nThisPickNums\":\"0\",\"sCardNo\":\"17121005\"}");
                break;
            case R.id.queryPage:
                procName = DataCenter.PROC_QUERY_PAGE;
                params.add("11");
                params.add("201");
                params.add("22090");
                params.add("");
                params.add("14");
                params.add("10401");
                params.add("1");
                break;
            case R.id.query:
                procName = DataCenter.PROC_FIRST_OPERATE_STEP_FLOW;
                params.add("11");
                params.add("201");
                params.add("22090");
                params.add("");
                params.add("14");
                break;
        }
        if("".equals(procName)) return;
        startThreadByProc(procName, params);
    }

    private void startThreadByProc(final String procName, final List<String> params){
        new Thread(new Runnable() {
            @Override
            public void run() {
                WebApis.sendWebServiceByMethod(procName, params);
            }
        }).start();
    }
}
