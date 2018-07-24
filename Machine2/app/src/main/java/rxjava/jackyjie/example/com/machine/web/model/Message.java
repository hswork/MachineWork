package rxjava.jackyjie.example.com.machine.web.model;

import java.util.List;

public class Message {

    public String sn; // 编号
    public int bc; // 是否常量
    public String st; // 项目文本
    public String sv; // 值
    public int br; // 是否只读
    public int bu; // 是否下拉框

    public List<DropDown> dropDownList; // 下拉框参数
    public List<String> showList; // 显示用下拉框
}
