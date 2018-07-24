package rxjava.jackyjie.example.com.machine.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import rxjava.jackyjie.example.com.machine.R;
import rxjava.jackyjie.example.com.machine.web.model.DropDown;

public class DropDownAdapter extends ArrayAdapter<DropDown> {
    public int resourceId;
    public int dropDownSourceId;
    public List<DropDown> dropDownList;

    public DropDownAdapter(Context context, int resource, List<DropDown> objects) {
        super(context, resource, objects);
        resourceId = resource;
        dropDownList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        TextViewHolder viewHolder;
        LinearLayout padding1;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
            viewHolder = new TextViewHolder();
            viewHolder.text_view = (TextView) view.findViewById(R.id.text_view);
            viewHolder.text_view.setPadding(0, getContext().getResources().getDimensionPixelSize(R.dimen.edit_text_padding_top), 0, 0);
            viewHolder.text_view.setTextColor(Color.BLACK);
//            padding1 = (LinearLayout)view.findViewById(R.id.padding1);
//            padding1.setPadding(0, getContext().getResources().getDimensionPixelSize(R.dimen.edit_text_padding_top), 0, 0);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (TextViewHolder) view.getTag();
        }

        DropDown item = getItem(position);
        viewHolder.text_view.setText(item.sValue);

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(dropDownSourceId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.name_view = (TextView) view.findViewById(R.id.name_view);
            viewHolder.value_view = (TextView) view.findViewById(R.id.value_view);
            viewHolder.name_view.setPadding(3, 3, 3, 3);
            viewHolder.value_view.setPadding(3, 3, 3, 3);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        DropDown item = getItem(position);
        viewHolder.name_view.setText(item.sName);
        viewHolder.value_view.setText(item.sValue);

        return view;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
        dropDownSourceId = resource;
    }

    public class ViewHolder {
        public TextView name_view;
        public TextView value_view;
    }

    public class TextViewHolder {
        public TextView text_view;
    }
}
