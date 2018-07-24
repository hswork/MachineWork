package rxjava.jackyjie.example.com.machine.web;

import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rxjava.jackyjie.example.com.machine.web.model.ArtImage;
import rxjava.jackyjie.example.com.machine.web.model.DropDown;
import rxjava.jackyjie.example.com.machine.web.model.MachineStatusType;
import rxjava.jackyjie.example.com.machine.web.model.MachineType;
import rxjava.jackyjie.example.com.machine.web.model.Message;
import rxjava.jackyjie.example.com.machine.web.model.OperateStepFLow;
import rxjava.jackyjie.example.com.machine.web.model.ResultListObjectModel;
import rxjava.jackyjie.example.com.machine.web.model.ResultObjectModel;

public class DataCenter {

    // hasFocusOn editText's focus
    public static boolean hasFocusOn = false;

    public static View focusView = null;

    // 主页字段呈现
    public static HashMap<String, Object>  mainPresent;

    // 存储的全局数据
    public static HashMap<String, Object> globalData = new HashMap<>();

    // machinestatus
    public static List<MachineStatusType> machineStatusTypeModel = new ArrayList<>();

    // machineType
    public static List<MachineType> machineTypeModel = new ArrayList<>();

    // 获取消息
    public static ResultListObjectModel<Message> messageModel = new ResultListObjectModel<>();

    // 获取工艺图片
    public static ResultObjectModel<ArtImage> ArtImageModel = new ResultObjectModel<>();

    // 获取下拉菜单
    public static ResultListObjectModel<DropDown> DropDownModel = new ResultListObjectModel<>();

    //  获取主页信息
    public static ResultListObjectModel<Message> MainOperateModel= new ResultListObjectModel<>();

    // 卡确认信息
    public static ResultListObjectModel<Message> CardInfoModel= new ResultListObjectModel<>();

    // 获取该员工的菜单
    public static ResultListObjectModel<OperateStepFLow> OperateStepFlowModel= new ResultListObjectModel<>();

    //  获取员工执行该流程的第一个操作类型
    public static ResultListObjectModel<Message> FirstOperateStepFLowModel= new ResultListObjectModel<>();

    // 按确认执行 sOperateTypeID为空或NULL  跳回主页
    public static ResultListObjectModel<Message> OnSubmitModel= new ResultListObjectModel<>();

    // 查询翻页
    public static ResultListObjectModel<Message> QueryPageModel= new ResultListObjectModel<>();

    // 查询不翻页
    public static ResultListObjectModel<Message> QueryModel= new ResultListObjectModel<>();


    // 存储过程名称
    public static final String PROC_ANSWER_CALL = "[spznAndroidAnswerCall]";
    public static final String PROC_DOWNLOAD_MACHINE = "[spznAndroidDownLoadMachineTaskInfo]";
    public static final String PROC_MACHINE_SIGNAL = "[spznAndroidGetMachineSignal]";
    public static final String PROC_MACHINE_STATUS_TYPE = "[spznAndroidGetMachineStatusType]";
    public static final String PROC_MACHINE_TYPE = "[spznAndroidGetMachineType]";
    public static final String PROC_SERVER_DATETIME = "[spznAndroidGetServerDateTime]";
    public static final String PROC_MESSAGE = "[spznAndroidGetMessage]";
    public static final String PROC_ART_IMAGE = "[spznAndroidGetArtImage]";
    public static final String PROC_DROP_DOWN = "[spznAndroidGetDropDown]";
    public static final String PROC_MAIN_OPERATE = "[spznAndroidGetMainOperateFormItem]";
    public static final String PROC_CARD_INFO = "[spznAndroidGetCardInfoOperateFormItem]";
    public static final String PROC_OPERATE_STEP_FLOW = "[spznAndroidGetOperateStepFLow]";
    public static final String PROC_FIRST_OPERATE_STEP_FLOW = "[spznAndroidGetFirstOperateStepFLow]";
    public static final String PROC_ON_SUBMIT = "[spznAndroidOnSuBmit]";
    public static final String PROC_QUERY_PAGE= "[spznAndroidQueryPage]";
}
