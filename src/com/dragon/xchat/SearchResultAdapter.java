package com.dragon.xchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dragon.xchat.data.Friend;

import java.util.List;

/**
 * Created by Administrator on 2014/8/17 0017.
 */
public class SearchResultAdapter extends BaseAdapter {
    private Context mContext = null;
    private List<Friend> mArrayList = null;

    public SearchResultAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return (mArrayList == null ? 0 : mArrayList.size());
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
            view = LayoutInflater.from(mContext).inflate(R.layout.search_result_item, null);
        }

        TextView nameView = (TextView) view.findViewById(R.id.search_result_name);
        nameView.setText(mArrayList.get(pos).getName());

        return view;
    }

    public void setList(List list) {
        mArrayList = list;
    }

}
