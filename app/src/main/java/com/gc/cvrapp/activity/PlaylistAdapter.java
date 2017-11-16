package com.gc.cvrapp.activity;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gc.cvrapp.R;
import com.gc.cvrapp.utils.LogUtil;

import java.util.List;

public class PlaylistAdapter extends BaseAdapter {
    private List<String> mFilelist;
    private static final String TAG = "PlaylistAdapter";

    PlaylistAdapter(List<String> filelist, AdapterCallback callback) {
        super();
        mFilelist = filelist;
        Icallback = callback;
    }

    @Override
    public int getCount() {
        return mFilelist.size();
    }

    @Override
    public Object getItem(int position) {
        return mFilelist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = View.inflate(parent.getContext().getApplicationContext(), R.layout.item, null);
            new ViewHolder(convertView);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        String item = (String)getItem(position);
        holder.tv_name.setText(item);
        holder.tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != Icallback)
                    Icallback.startPlayback((String)getItem(position));
            }
        });
        return convertView;

    }

    class ViewHolder {
        TextView tv_name;
        public ViewHolder(View view) {
            tv_name = (TextView) view.findViewById(R.id.file_name);
            view.setTag(this);
        }
    }

    public interface AdapterCallback {
        void startPlayback(String item);
    }

    private AdapterCallback Icallback;

}
