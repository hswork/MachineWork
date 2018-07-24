package rxjava.jackyjie.example.com.machine.adapter;

import android.content.Context;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import rxjava.jackyjie.example.com.machine.R;
import rxjava.jackyjie.example.com.machine.interfaces.OperateListener;
import rxjava.jackyjie.example.com.machine.web.model.OperateStepFLow;

public class OperateAdapter extends RecyclerView.Adapter<OperateAdapter.ViewHolder> {
    private static final String TAG = "MessageAdapter";

    private List<OperateStepFLow> operateList;
    private OperateListener operateListener;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView menu_text;
        LinearLayout padding1;
        RelativeLayout padding2;
        ImageView menu_img;

        public ViewHolder(View view) {
            super(view);
            menu_text = (TextView) view.findViewById(R.id.menu_text);
            menu_img = (ImageView)view.findViewById(R.id.menu_img);
            padding1 = (LinearLayout) view.findViewById(R.id.padding1);
            padding2 = (RelativeLayout) view.findViewById(R.id.padding2);
            padding1.setPadding(10, 10, 10, 10);
            padding2.setPadding(10, 10, 10, 10);
        }
    }

    public OperateAdapter(List<OperateStepFLow> List, OperateListener listener) {

        operateList = List;
        operateListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(OperateAdapter.ViewHolder holder, final int position) {

        Context context = holder.menu_text.getContext();
        final OperateStepFLow flow = operateList.get(position);
        holder.menu_text.setText(flow.sStepFlowName);
        holder.menu_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operateListener.OnClick(flow.iBreakEnd, flow.sOperateFLowID);
            }
        });
        int imgId = 0;
        switch (flow.iiCon){
            case 1:
                imgId = R.drawable.img_10;
                break;
            case 2:
                imgId = R.drawable.img_11;
                break;
            case 3:
                imgId = R.drawable.img_12;
                break;
            case 4:
                imgId = R.drawable.img_13;
                break;
            case 5:
                imgId = R.drawable.img_14;
                break;
            default:
                imgId = R.drawable.img_10;
        }
        holder.menu_img.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), imgId, null));
        holder.menu_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                operateListener.OnClick(flow.iBreakEnd, flow.sOperateFLowID);
            }
        });
    }

    @Override
    public int getItemCount() {
        return operateList.size();
    }

    // 重置data
    public void setData(List<OperateStepFLow> List) {
        this.operateList = List;
    }
}