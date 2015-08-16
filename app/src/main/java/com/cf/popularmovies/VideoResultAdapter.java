package com.cf.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.cf.popularmovies.model.VideoResult;

import java.util.List;

public class VideoResultAdapter extends ArrayAdapter<VideoResult> {

    Context context;
    int layoutResource;
    List<VideoResult> data = null;

    public VideoResultAdapter(Context context, int resource, List<VideoResult> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layoutResource = resource;
        this.data = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = convertView;
        ResultHolder resultHolder;

        if (view == null) {
            LayoutInflater layoutInflater = ((Activity)context).getLayoutInflater();
            view = layoutInflater.inflate(layoutResource, parent, false);

            resultHolder = new ResultHolder();
            resultHolder.textView_result = (TextView)view.findViewById(R.id.textView_video_name);

            view.setTag(resultHolder);
        }
        else
        {
            resultHolder = (ResultHolder)view.getTag();
        }

        VideoResult result = data.get(position);

        resultHolder.textView_result.setText(result.getName());

        return view;
    }

    static class ResultHolder {
        TextView textView_result;
    }
}
