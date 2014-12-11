package com.dragon.xchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.dragon.xchat.data.Friend;

/**
 * Created by Administrator on 2014/8/17 0017.
 */
public class ChatAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<Friend> mArrayList = null;

    private int CHAT_LIST_FIXED_LENGTH = 1;

    public ChatAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        if (mArrayList == null || mArrayList.size() == 0) {
            return CHAT_LIST_FIXED_LENGTH;
        } else {
            return (mArrayList.size() + CHAT_LIST_FIXED_LENGTH);
        }
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.chat_item, null);
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.chat_item_icon);
        iconView.setScaleType(ImageView.ScaleType.FIT_XY);

        TextView nameView = (TextView) view.findViewById(R.id.chat_item_name);

        TextView contentView = (TextView) view.findViewById(R.id.chat_item_content);

        TextView timeView = (TextView) view.findViewById(R.id.chat_item_time);

        if (pos == 0) {
            if (CHAT_LIST_FIXED_LENGTH == 1) {
                
                iconView.setBackgroundResource(R.drawable.ic_launcher);
                nameView.setText(R.string.team_name);
                contentView.setText(R.string.team_desc);
                timeView.setText("");
            } else {
                //do normal things
            }
        }else{
        	iconView.setBackgroundResource(R.drawable.head_icon);
            nameView.setText(mArrayList.get(pos-1).getName());
            contentView.setText(mArrayList.get(pos-1).getDesc());
            timeView.setText("");
        }

        return view;
    }

    public void setList(List list) {
        mArrayList = list;
    }

    public void setFixedLength(int fixedLength) throws Exception {
        if (fixedLength != 0 && fixedLength != 1) {
            throw new Exception("fixedLength must be 0 or 1");
        }
        CHAT_LIST_FIXED_LENGTH = fixedLength;
    } 
}
