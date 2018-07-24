package rxjava.jackyjie.example.com.machine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import rxjava.jackyjie.example.com.machine.R;
import rxjava.jackyjie.example.com.machine.util.ControlUtil;
import rxjava.jackyjie.example.com.machine.web.model.DropDown;
import rxjava.jackyjie.example.com.machine.web.model.Message;

    public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{
        private static final String TAG = "MessageAdapter";

        private List<Message> messageList;
        private int iColNum = 0;
        private View.OnFocusChangeListener mFocusChangedListener;

        static class ViewHolder extends RecyclerView.ViewHolder {
            LinearLayout layoutItem;

            public ViewHolder(View view) {
                super(view);
                layoutItem = (LinearLayout)view.findViewById(R.id.itemLayout);
            }
        }

        public MessageAdapter(List<Message> fruitList, int iColNum) {

            messageList = fruitList;
            this.iColNum = iColNum;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
            final ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, final int position) {

            LinearLayout lLayoutRoot = holder.layoutItem; // root Layout布局
            Context context = lLayoutRoot.getContext();  // root context
            final Message data = messageList.get(position); // root data

            // 获取边框布局
            Drawable drawable = getDrawable(context, position, iColNum);
            lLayoutRoot.setBackground(drawable);

            lLayoutRoot.setPadding(40, 10, 0, 10 );
            lLayoutRoot.removeAllViews();

            // 生成view
            lLayoutCreateView(context, lLayoutRoot,  position, data);
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        /*
        * 设置列数
        * */
        public void setColNum(int iColNum){
            this.iColNum = iColNum;
        }

        public void setmFocusChangedListener(View.OnFocusChangeListener mFocusChangedListener){
            this.mFocusChangedListener = mFocusChangedListener;
        }

        /*
        * setValue
        * set : if sn != ""
        * */
        private String setName(String sn){
            return "".equals(sn) ? "" : sn + ":";
        }

        private int getWidth(String value, int size){
            int width = ControlUtil.getCharacterWidth(value, 28);
            if(width < 120){
                width = 120;
            }
            return width;
        }

        private void lLayoutCreateView(Context context, LinearLayout lLayoutRoot, final int position, final Message data) {

            TextView textview = new TextView(context);
            textview.setTextColor(Color.WHITE);
            textview.setTextSize(28);
            String value = setName(data.st);
            textview.setWidth(getWidth(value, 28)); // 设置宽度为90
            textview.setText(value);
            lLayoutRoot.addView(textview);

            if(data.br == 1 && data.bu == 0 ) {

                TextView text_view2 = new TextView(context);
                text_view2.setTextColor(Color.WHITE);
                text_view2.setTextSize(28);
                text_view2.setText(data.sv);
                lLayoutRoot.addView(text_view2);
            }
            else if(data.br == 0 && data.bu == 0 ) {

                LinearLayout layout = new LinearLayout(context);
                layout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                layout.setPadding(0, 0, 10, 0);

                final EditText editText = new EditText(context);
                editText.setTextColor(ResourcesCompat.getColor(context.getResources(), R.color.black, null)); // 字体颜色
                editText.setText(data.sv);
                editText.setTextSize(28);
                editText.setGravity(Gravity.CENTER);
                editText.setMaxLines(1);
                editText.setCursorVisible(true);
                editText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        // 获取值
                        Message msg = messageList.get(position);
                        msg.sv = editText.getText().toString().trim();
                        Log.d(TAG, "afterTextChanged: " + msg.sv);
                    }
                });
                layout.addView(editText);
                lLayoutRoot.addView(layout);
            }
            else if(data.br == 0 && data.bu == 1) {

                LinearLayout wideLayout = new LinearLayout(context);
                wideLayout.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                wideLayout.setPadding(0, 0, 10, 0);

                RelativeLayout layout = new RelativeLayout(context);
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                layout.setLayoutParams(params);
                layout.setBackgroundColor(Color.WHITE);

                // 下拉框生成
                final Spinner spinner = new Spinner(context);
                RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params1.rightMargin = 30;
                spinner.setLayoutParams(params1);
                // 设置adapter
                data.showList = new ArrayList<>();
                for (DropDown item : data.dropDownList) {
                    data.showList.add(item.sValue);
                }
                final List<String> list = data.showList;
                final DropDownAdapter adapter = new DropDownAdapter(context, R.layout.custom_spiner_text_item, data.dropDownList);//设置下拉列表的风格
                adapter.setDropDownViewResource(R.layout.custom_spinner_dropdown_item); // 下拉框分格
                spinner.setAdapter(adapter);
                spinner.setVisibility(View.VISIBLE);
                spinner.setBackgroundColor(Color.WHITE);
                layout.addView(spinner);

                // 设置 下拉图片
                ImageView imageView = new ImageView(context);
                imageView.setImageResource(R.drawable.drop_down);
                RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(30, 30);
                params2.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                imageView.setLayoutParams(params2);
                imageView.setPadding(0, 5, 0, 0);
                layout.addView(imageView);

                // 设置EditText
                final EditText editText = new EditText(context);
                RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                params4.rightMargin = 30;
                editText.setLayoutParams(params4);
                editText.setBackgroundColor(Color.WHITE);
                editText.setTextSize(28);
                editText.setTextColor(Color.BLACK);

                layout.addView(editText);

                //添加事件Spinner事件监听
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        editText.setText(list.get(i));
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {
                    }
                });

                if(mFocusChangedListener != null){
                    editText.setOnFocusChangeListener(mFocusChangedListener);
                }
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        data.sv = editText.getText().toString();
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });

                layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.performClick();
                    }
                });

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        spinner.performClick();
                    }
                });

                wideLayout.addView(layout);
                lLayoutRoot.addView(wideLayout);
            }
        }

        /*
        * from dynamic
        * Drawable
        * */
        private Drawable getDrawable(Context context, int position, int iColNum){
            Drawable drawable = null;

            if(position / iColNum == 0 && position % iColNum == 0){// 第一行第一列
                    drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.table_left_first, null);
            }
            else if(position / iColNum == 0 && position % iColNum != 0) { // 第一行其他列
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.table_right_first, null);
            }
            else if(position / iColNum != 0 && position % iColNum == 0) { // 非第一行第一列
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.table_left, null);
            }
            else if(position / iColNum != 0 && position % iColNum != 0) { // 非第一行非第一列
                drawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.table_right, null);
            }
            return drawable;
        }
    }