package com.sansen.myandroidlifegamedame.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.sansen.myandroidlifegamedame.R;

import java.io.File;

public class DialogListAdapter extends BaseAdapter {
    private Context context;
    private File[] files;

    public DialogListAdapter(Context mContext,File[] mFiles){
        this.context = mContext;
        this.files = mFiles;
    }

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int position) {
        return files[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewHolder holder = null;
        if(convertView == null) {
            convertView= inflater.inflate(R.layout.item_dialog_file, null);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.item_tv);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        if(files[position] != null && files[position].getName() != null){
            holder.textView.setText(files[position].getName());
//            holder.textView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(context,"点击测试"+position,Toast.LENGTH_SHORT).show();
//                }
//            });

        }

        return convertView;
    }

    class ViewHolder{
        TextView textView;
    }

}
