package com.dragon.xchat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dragon.xchat.dummyContent.DiscoverContent;

/**
 * Created by Administrator on 2014/8/17 0017.
 */
public class DiscoverAdapter extends BaseAdapter {
    private Context mContext = null;

    public DiscoverAdapter(Context context) {
        mContext = context;
    }


    @Override
    public int getCount() {
        return DiscoverContent.ITEMS_NAME.length;
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
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.discover_item, null);
        }

        ImageView iconView = (ImageView) view.findViewById(R.id.discover_item_icon);
        iconView.setScaleType(ImageView.ScaleType.FIT_XY);
        iconView.setImageResource(DiscoverContent.ITEMS_ICON[i]);

        TextView nameView = (TextView) view.findViewById(R.id.discover_item_name);
        nameView.setText(DiscoverContent.ITEMS_NAME[i]);

        return view;
    }
}
