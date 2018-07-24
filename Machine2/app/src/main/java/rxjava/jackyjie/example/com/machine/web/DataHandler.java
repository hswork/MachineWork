package rxjava.jackyjie.example.com.machine.web;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rxjava.jackyjie.example.com.machine.util.HashMapUtil;
import rxjava.jackyjie.example.com.machine.util.JsonConvertUtil;
import rxjava.jackyjie.example.com.machine.web.model.ArtImage;
import rxjava.jackyjie.example.com.machine.web.model.DropDown;
import rxjava.jackyjie.example.com.machine.web.model.Extra;
import rxjava.jackyjie.example.com.machine.web.model.MachineStatusType;
import rxjava.jackyjie.example.com.machine.web.model.MachineType;
import rxjava.jackyjie.example.com.machine.web.model.Message;
import rxjava.jackyjie.example.com.machine.web.model.OperateStepFLow;
import rxjava.jackyjie.example.com.machine.web.model.ResultListObjectModel;
import rxjava.jackyjie.example.com.machine.web.model.ResultObjectModel;

public class DataHandler {
    private static final String TAG = "DataHandler";

    // 通过存储过程处理消息
    public static boolean handleMessageByProc(String procName, String sResult){
        switch (procName){
            case DataCenter.PROC_ANSWER_CALL:
                break;
            case DataCenter.PROC_ART_IMAGE:
                return handleArtImage(sResult);
            case DataCenter.PROC_CARD_INFO:
                return handleCardInfo(sResult);
            case DataCenter.PROC_DOWNLOAD_MACHINE:
                return  handleDownLoadMachine(sResult);
            case DataCenter.PROC_DROP_DOWN:
                return handleDropDown(sResult);
            case DataCenter.PROC_FIRST_OPERATE_STEP_FLOW:
                return handleFirstOperateStepFLow(sResult);
            case DataCenter.PROC_MACHINE_SIGNAL:
                return handleGetMachineSignal(sResult);
            case DataCenter.PROC_MACHINE_STATUS_TYPE:
                return handleMachineStatusType(sResult);
            case DataCenter.PROC_MACHINE_TYPE:
                return handleMachineType(sResult);
            case DataCenter.PROC_MAIN_OPERATE:
                return handleMainOperate(sResult);
            case DataCenter.PROC_MESSAGE:
                return handleMessage(sResult);
            case DataCenter.PROC_ON_SUBMIT:
                return handleOnSubmit(sResult);
            case DataCenter.PROC_OPERATE_STEP_FLOW:
                return handleOperateStepFlow(sResult);
            case DataCenter.PROC_QUERY_PAGE:
                return handleQueryPage(sResult);
            case DataCenter.PROC_SERVER_DATETIME:
                return handleServerDateTime(sResult);
        }
        return false;
    }

    // 上下电下载任务
    private static boolean handleDownLoadMachine(String sResult){
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            JSONObject jsonObject = jsondt2.getJSONObject(0);
            HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject);
            DataCenter.globalData.putAll(hashes);
            Gson gson = new Gson();
            Log.d(TAG, "handleDownLoadMachine: " + gson.toJson(hashes));
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    // 获取织机信号
    private static boolean handleGetMachineSignal(String sResult){
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 =jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            // 状态
            String sReturnType = JsonConvertUtil.getString(jsonObject1, "sReturnType");
            if("error".equals(sReturnType)){

                HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject1, "sReturnType");
                DataCenter.globalData.putAll(hashes);
                return false;
            }
            else {

                for(int i = 0 ; i< jsonArray.length(); i++){
                    JSONArray jsondt = jsonArray.getJSONArray(i);
                    JSONObject jsonObject = jsondt.getJSONObject(0);
                    HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject, "sReturnType");
                    DataCenter.globalData.putAll(hashes);
                    Gson gson = new Gson();
                    Log.d(TAG, "handleGetMachineSignal: " + gson.toJson(hashes));
                }
                return true;
            }
        }catch(Exception ex){
            return false;
        }
    }

    // 获取织机所有状态
    private static boolean handleMachineStatusType(String sResult){

        List<MachineStatusType> type = DataCenter.machineStatusTypeModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            DataCenter.machineStatusTypeModel.clear();
            for(int i = 0 ; i < jsondt2.length(); i++){
                MachineStatusType item = new MachineStatusType();
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                item.iStatusID= JsonConvertUtil.getInt(jsonObject, "iStatusID");
                item.sStatusType = JsonConvertUtil.getString(jsonObject, "sStatusType");
                DataCenter.machineStatusTypeModel.add(item);
            }
            Gson gson = new Gson();
            Log.d(TAG, "handleMachineStatusType: " + gson.toJson(DataCenter.machineStatusTypeModel));
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    // 获取机型
    private static boolean handleMachineType(String sResult){

        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            DataCenter.machineTypeModel.clear();
            for(int i = 0 ; i < jsondt1.length(); i++){
                MachineType item = new MachineType();
                JSONObject jsonObject = jsondt1.getJSONObject(i);
                item.iMachineGroupID= JsonConvertUtil.getInt(jsonObject, "iMachineGroupID");
                item.sMachineGroupName = JsonConvertUtil.getString(jsonObject, "sMachineGroupName");
                DataCenter.machineTypeModel.add(item);
            }
            Gson gson = new Gson();
            Log.d(TAG, "handleMachineType: " + gson.toJson(DataCenter.machineTypeModel));
            return true;
        }catch(Exception ex){
            return false;
        }
    }

    // 获取服务器时间
    private static boolean handleServerDateTime(String sResult){

        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject1, "sReturnType");
            DataCenter.globalData.putAll(hashes);
            Gson gson = new Gson();
            Log.d(TAG, "handleServerDateTime: " + gson.toJson(hashes));
            return true;
        }catch(Exception ex){
           return false;
        }
    }

    // 获取消息
    private static boolean handleMessage(String sResult){

        ResultListObjectModel<Message> model = DataCenter.messageModel;
        model.t = new ArrayList<>();
        model.bFlag = false;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            String sReturnType = JsonConvertUtil.getString(jsonObject1, "sReturnType");
            if("error".equals(sReturnType)){

                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                for(int i = 0 ; i< jsondt2.length(); i++){
                    JSONObject jsonObject = jsondt2.getJSONObject(i);
                    Message item = new Message();
                    item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                    item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                    item.st = JsonConvertUtil.getString(jsonObject, "st");
                    item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                    item.br = JsonConvertUtil.getInt(jsonObject, "br");
                    item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                    model.t.add(item);
                }
                Gson gson = new Gson();
                Log.d(TAG, "handleMessage: " + gson.toJson(model));
                return false;
            }
            else {
                int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
                Double nFont = JsonConvertUtil.getDouble(jsonObject1, "nFont");
                Double nQuery = JsonConvertUtil.getDouble(jsonObject1, "nQuery");
                model.extra = new Extra();
                model.extra.iColNum = iColNum;
                model.extra.nFont = nFont;
                model.extra.nQuery = nQuery;

                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                model.t = new ArrayList<>();
                for(int i = 0 ; i< jsondt2.length(); i++){
                    JSONObject jsonObject = jsondt2.getJSONObject(i);
                    Message item = new Message();
                    item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                    item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                    item.st = JsonConvertUtil.getString(jsonObject, "st");
                    item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                    item.br = JsonConvertUtil.getInt(jsonObject, "br");
                    item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                    model.t.add(item);
                }
                Gson gson = new Gson();
                Log.d(TAG, "handleMessage: " + gson.toJson(model));
                model.bFlag = true;
                return true;
            }
        }catch(Exception ex){
            Log.d(TAG, "handleMessage: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 获取工艺图片
    private static boolean handleArtImage(String sResult){
        ResultObjectModel<ArtImage> model = DataCenter.ArtImageModel;

        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
            int nFont = JsonConvertUtil.getInt(jsonObject1, "nFont");
            model.extra = new Extra();
            model.extra.iColNum = iColNum;
            model.extra.nFont = nFont;

            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            JSONObject jsonObject2 = jsondt2.getJSONObject(0);
            String gFile = JsonConvertUtil.getString(jsonObject2, "gFile");
            model.t = new ArtImage();
            model.t.gFile = gFile;
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleArtImage: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleArtImage: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 获取下拉菜单
    private static boolean handleDropDown(String sResult){
        ResultListObjectModel<DropDown> model = DataCenter.DropDownModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            model.t = new ArrayList<>();
            for(int i = 0 ; i< jsondt2.length(); i++){
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                DropDown item = new DropDown();
                item.sName = JsonConvertUtil.getString(jsonObject, "sname");
                item.sValue = JsonConvertUtil.getString(jsonObject, "svalue");
                model.t.add(item);
            }
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleDropDown: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleDropDown: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 获取主页信息
    private static boolean handleMainOperate(String sResult){
        ResultListObjectModel<Message> model = DataCenter.MainOperateModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
            model.extra = new Extra();
            model.extra.iColNum = iColNum;

            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            model.t = new ArrayList<>();
            for(int i = 0 ; i< jsondt2.length(); i++){
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                Message item = new Message();
                item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                item.st = JsonConvertUtil.getString(jsonObject, "st");
                item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                item.br = JsonConvertUtil.getInt(jsonObject, "br");
                item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                model.t.add(item);
            }
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleMainOperate: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleMainOperate: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 卡确认信息
    public static boolean handleCardInfo(String sResult){
        ResultListObjectModel<Message> model = DataCenter.CardInfoModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            String sReturnType = JsonConvertUtil.getString(jsonObject1, "sReturnType");
            if("error".equals(sReturnType)) {
                model.bFlag = false;

                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                model.t = new ArrayList<>();
                for(int i = 0 ; i< jsondt2.length(); i++){
                    JSONObject jsonObject = jsondt2.getJSONObject(i);
                    Message item = new Message();
                    item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                    item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                    item.st = JsonConvertUtil.getString(jsonObject, "st");
                    item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                    item.br = JsonConvertUtil.getInt(jsonObject, "br");
                    item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                    model.t.add(item);
                }
                return false;
            }else
                {
                model.bFlag = true;
                int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
                model.extra = new Extra();
                model.extra.iColNum = iColNum;
                HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject1, "sReturnType");
                DataCenter.globalData.putAll(hashes);

                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                model.t = new ArrayList<>();
                for(int i = 0 ; i< jsondt2.length(); i++){
                    JSONObject jsonObject = jsondt2.getJSONObject(i);
                    Message item = new Message();
                    item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                    item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                    item.st = JsonConvertUtil.getString(jsonObject, "st");
                    item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                    item.br = JsonConvertUtil.getInt(jsonObject, "br");
                    item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                    model.t.add(item);
                    HashMapUtil.addKeyValue(DataCenter.globalData, item.sn, item.sv);
                }
                Gson gson = new Gson();
                Log.d(TAG, "handleCardInfo: " + gson.toJson(model));
                return true;
            }
        }catch(Exception ex){
            Log.d(TAG, "handleCardInfo: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 获取该员工的菜单
    private static boolean handleOperateStepFlow(String sResult){
        ResultListObjectModel<OperateStepFLow> model = DataCenter.OperateStepFlowModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            int iPid = JsonConvertUtil.getInt(jsonObject1, "iPid");
            model.extra = new Extra();
            model.extra.iPid = iPid;

            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            model.t = new ArrayList<>();
            for(int i = 0 ; i< jsondt2.length(); i++){
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                OperateStepFLow item = new OperateStepFLow();
                item.sOperateFLowID = JsonConvertUtil.getString(jsonObject, "sOperateFLowID");
                item.sStepFlowName = JsonConvertUtil.getString(jsonObject, "sStepFlowName");
                item.iiCon = JsonConvertUtil.getInt(jsonObject, "iiCon");
                item.iBreakEnd = JsonConvertUtil.getInt(jsonObject, "iBreakEnd");
                model.t.add(item);
            }
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleOperateStepFlow: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleOperateStepFlow: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 获取员工执行该流程的第一个操作类型
    private static boolean handleFirstOperateStepFLow(String sResult){
        ResultListObjectModel<Message> model = DataCenter.FirstOperateStepFLowModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
            String sOperateTypeID = JsonConvertUtil.getString(jsonObject1, "sOperateTypeID");
            model.extra = new Extra();
            model.extra.iColNum = iColNum;
            model.extra.sOperateTypeID = sOperateTypeID;

            HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject1, "sReturnType");
            DataCenter.globalData.putAll(hashes);

            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            model.t = new ArrayList<>();
            for(int i = 0 ; i< jsondt2.length(); i++){
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                Message item = new Message();
                item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                item.st = JsonConvertUtil.getString(jsonObject, "st");
                item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                item.br = JsonConvertUtil.getInt(jsonObject, "br");
                item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                model.t.add(item);
            }
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleFirstOperateStepFLow: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleFirstOperateStepFLow: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 按确认执行 sOperateTypeID为空或NULL  跳回主页
    private static boolean handleOnSubmit(String sResult){
        ResultListObjectModel<Message> model = DataCenter.OnSubmitModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            if(jsonArray.length() == 0){
                model.bFlag = true;
                return true;
            }

            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            String sReturnType =  JsonConvertUtil.getString(jsonObject1, "sReturnType");
            if("error".equals(sReturnType)){
                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                JSONObject jsonObject = jsondt2.getJSONObject(0);
                model.message = JsonConvertUtil.getString(jsonObject, "sv");
                model.bFlag = false;
                return false;
            }else {

                int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
                String sOperateTypeID = JsonConvertUtil.getString(jsonObject1, "sOperateTypeID");
                model.extra = new Extra();
                model.extra.iColNum = iColNum;
                model.extra.sOperateTypeID = sOperateTypeID;

                // 回写数据
                HashMap<String, Object> hashes = HashMapUtil.getKeyValues(jsonObject1, "sReturnType");
                DataCenter.globalData.putAll(hashes);

                JSONArray jsondt2 = jsonArray.getJSONArray(1);
                model.t = new ArrayList<>();
                for (int i = 0; i < jsondt2.length(); i++) {
                    JSONObject jsonObject = jsondt2.getJSONObject(i);
                    Message item = new Message();
                    item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                    item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                    item.st = JsonConvertUtil.getString(jsonObject, "st");
                    item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                    item.br = JsonConvertUtil.getInt(jsonObject, "br");
                    item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                    model.t.add(item);
                }
                model.bFlag = true;
                Gson gson = new Gson();
                Log.d(TAG, "handleOnSubmit: " + gson.toJson(model));
                return true;
            }
        }catch(Exception ex){
            Log.d(TAG, "handleOnSubmit: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }

    // 查询翻页
    private static boolean handleQueryPage(String sResult){
        ResultListObjectModel<Message> model = DataCenter.QueryPageModel;
        try {
            JSONArray jsonArray = new JSONArray(sResult);
            JSONArray jsondt1 = jsonArray.getJSONArray(0);
            JSONObject jsonObject1 = jsondt1.getJSONObject(0);
            int iColNum = JsonConvertUtil.getInt(jsonObject1, "iColNum");
            String sOperateTypeID = JsonConvertUtil.getString(jsonObject1, "sOperateTypeID");
            Double nFont = JsonConvertUtil.getDouble(jsonObject1, "nFont");
            Double nQuery = JsonConvertUtil.getDouble(jsonObject1, "nQuery");
            model.extra = new Extra();
            model.extra.iColNum = iColNum;
            model.extra.sOperateTypeID = sOperateTypeID;
            model.extra.nFont = nFont;
            model.extra.nQuery = nQuery;

            JSONArray jsondt2 = jsonArray.getJSONArray(1);
            model.t = new ArrayList<>();
            for(int i = 0 ; i< jsondt2.length(); i++){
                JSONObject jsonObject = jsondt2.getJSONObject(i);
                Message item = new Message();
                item.sv = JsonConvertUtil.getString(jsonObject, "sv");
                item.sn = JsonConvertUtil.getString(jsonObject, "sn");
                item.st = JsonConvertUtil.getString(jsonObject, "st");
                item.bc = JsonConvertUtil.getInt(jsonObject, "bc");
                item.br = JsonConvertUtil.getInt(jsonObject, "br");
                item.bu = JsonConvertUtil.getInt(jsonObject, "bu");
                model.t.add(item);
            }
            model.bFlag = true;
            Gson gson = new Gson();
            Log.d(TAG, "handleQueryPage: " + gson.toJson(model));
            return true;
        }catch(Exception ex){
            Log.d(TAG, "handleQueryPage: " + ex.getMessage());
            model.bFlag = false;
            return false;
        }
    }
}
