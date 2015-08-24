package com.cf.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cf.popularmovies.model.MovieResult;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class MovieResultAdapter extends ArrayAdapter<MovieResult> {

    Context context;
    int layoutResource;
    List<MovieResult> data = null;

    public MovieResultAdapter(Context context, int resource, List<MovieResult> objects) {
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
            resultHolder.imageView_result = (ImageView)view.findViewById(R.id.ImageView_movie_poster);
            resultHolder.textView_result = (TextView)view.findViewById(R.id.textView_movie_poster);

            view.setTag(resultHolder);
        }
        else
        {
            resultHolder = (ResultHolder)view.getTag();
        }

        MovieResult result = data.get(position);

        // If PosterPath contains PackageName retrieve image from offline storage
        if (result.getPosterPath() != null) {
            if (result.getPosterPath().contains(context.getPackageName())) {
                Picasso.with(context)
                        .load(new File(result.getPosterPath()))
                        .into(resultHolder.imageView_result);
            } else {
                Picasso.with(context)
                        .load(getImageURL(result.getPosterPath()))
                        .into(resultHolder.imageView_result);
            }
        } else {
            Picasso.with(context)
                    .load(R.drawable.placeholder)
                    .into(resultHolder.imageView_result);
        }

        resultHolder.textView_result.setText(result.getTitle());

        return view;
    }

    static class ResultHolder {
        ImageView imageView_result;
        TextView textView_result;
    }

    private String getImageURL(String posterPath) {

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w185";

        return IMAGE_BASE_URL + IMAGE_SIZE + posterPath;
    }
}