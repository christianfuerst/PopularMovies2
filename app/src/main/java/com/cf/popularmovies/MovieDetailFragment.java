package com.cf.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.cf.popularmovies.API.VideoAPI;
import com.cf.popularmovies.model.MovieResult;
import com.cf.popularmovies.model.Video;
import com.cf.popularmovies.model.VideoResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MovieDetailFragment extends Fragment {

    private static final String KEY_VIDEO_RESULT_DATA = "video_result_data";

    private ListView listview;
    private Bundle savedInstanceState;

    private MovieResult result;
    private List<VideoResult> video_result_data = null;
    private VideoResultAdapter videoResultAdapter;
    private Boolean isTaskRunning;

    private TextView textView_movie_title;
    private TextView textView_release_date;
    private TextView textView_vote_average;
    private TextView textView_details;
    private TextView textView_video_unavailable;
    private ImageView imageView_movie_header;
    private ImageView imageView_movie_poster_small;

    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // VideoResult data will be stored in ParcelableArrayList in order to prevent another API call
        outState.putParcelableArrayList(KEY_VIDEO_RESULT_DATA, new ArrayList<>(video_result_data));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView_movie_title = (TextView) getActivity().findViewById(R.id.textView_movie_title);
        textView_release_date = (TextView) getActivity().findViewById(R.id.textView_release_date);
        textView_vote_average = (TextView) getActivity().findViewById(R.id.textView_vote_average);
        textView_details = (TextView) getActivity().findViewById(R.id.textView_details);
        textView_video_unavailable = (TextView) getActivity().findViewById(R.id.textView_video_unavailable);
        imageView_movie_header = (ImageView) getActivity().findViewById(R.id.imageView_movie_header);
        imageView_movie_poster_small = (ImageView) getActivity().findViewById(R.id.imageView_movie_poster_small);
        listview = (ListView) getActivity().findViewById(R.id.listView_video);

        // Handle image loading for "BackDrop" image
        // If no image is available use a placeholder instead
        if (result.getBackdropPath() != null)
        {
            Picasso.with(getActivity().getApplicationContext())
                    .load(getImageURL(result.getBackdropPath()))
                    .into(imageView_movie_header);
        } else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(R.drawable.placeholder)
                    .into(imageView_movie_header);
        }

        // Handle image loading for "Poster" image
        // If no image is available use a placeholder instead
        if (result.getPosterPath() != null)
        {
            Picasso.with(getActivity().getApplicationContext())
                    .load(getImageURL(result.getPosterPath()))
                    .into(imageView_movie_poster_small);
        } else {
            Picasso.with(getActivity().getApplicationContext())
                    .load(R.drawable.placeholder)
                    .into(imageView_movie_poster_small);
        }

        // Update the GUI with data
        textView_movie_title.setText(result.getTitle());
        textView_release_date.setText(result.getReleaseDate());
        String vote_average = result.getVoteAverage() + "/10.0";
        textView_vote_average.setText(vote_average);
        textView_details.setText(result.getOverview());

        // If an InstanceState is present, we don't have to do another API call in order to reduce network traffic
        if (savedInstanceState != null) {
            // Retrieve result data from ParcelableArrayList
            video_result_data = savedInstanceState.getParcelableArrayList(KEY_VIDEO_RESULT_DATA);

            // Use the custom adapter to the result data visible in the GUI
            videoResultAdapter = new VideoResultAdapter(getActivity(), R.layout.item_listview_video, video_result_data);
            listview.setAdapter(videoResultAdapter);

            setListViewHeightBasedOnChildren(listview);
        }
        else
        // If no InstanceState is present, start the custom AsyncTask in order to retrieve data from the API
        {
            FetchVideosTask fetchVideosTask = new FetchVideosTask();
            fetchVideosTask.execute(result.getId().toString());
        }

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String youtube_video_id = videoResultAdapter.getItem(position).getKey();

                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + youtube_video_id));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent=new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + youtube_video_id));
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Get data from parent activity, no API call required
        Intent intent = getActivity().getIntent();

        if (intent != null && intent.hasExtra("result")) {

            result = intent.getParcelableExtra("result");

        }

        return inflater.inflate(R.layout.fragment_movie_detail, container, false);
    }

    private class FetchVideosTask extends AsyncTask<String, Void, List<VideoResult>> {

        private final String API_KEY = getString(R.string.api_key);
        private String api_endpoint = "http://api.themoviedb.org/3";
        private RetrofitError retrofitError = null;

        // In order to avoid multiple AsyncTask we set isTaskRunning true first
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isTaskRunning = true;
        }

        // Use RetroFit to retrieve data from API
        @Override
        protected List<VideoResult> doInBackground(String... params) {

            Video video;

            try {

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint(api_endpoint)
                        .build();

                VideoAPI videoAPI = restAdapter.create(VideoAPI.class);
                video = videoAPI.getVideo(API_KEY, params[0]);

            } catch (RetrofitError e) {

                retrofitError = e;
                video = new Video();

            }

            return video.getResults();
        }

        @Override
        protected void onPostExecute(List<VideoResult> videoResults) {
            super.onPostExecute(videoResults);

            video_result_data = FilterYouTubeVideos(videoResults);
            Snackbar snackbar = null;

            // Use the custom adapter to the result data visible in the GUI
            videoResultAdapter = new VideoResultAdapter(getActivity(), R.layout.item_listview_video, video_result_data);
            listview.setAdapter(videoResultAdapter);

            setListViewHeightBasedOnChildren(listview);

            if (video_result_data.isEmpty()) {
                textView_video_unavailable.setVisibility(View.VISIBLE);
            }

            // If the API call didn't work out as expected Snackbar will appear
            if (retrofitError != null) {

                snackbar = Snackbar.make(getView(), retrofitError.getMessage(), Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction(getString(R.string.button_reload), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isTaskRunning) {
                            textView_video_unavailable.setVisibility(View.GONE);
                            FetchVideosTask fetchVideosTask = new FetchVideosTask();
                            fetchVideosTask.execute(result.getId().toString());
                        }
                    }
                });

                snackbar.show();
            }
            else
            {
                if (snackbar != null)
                {
                    // If the API call worked, hide Snackbar if present
                    snackbar.dismiss();
                }
            }

            // AsyncTask is done, so we set isTaskRunning back to false
            isTaskRunning = false;
        }
    }

    // Helper function to construct image URLs
    private String getImageURL(String posterPath) {

        final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        final String IMAGE_SIZE = "w342";

        return IMAGE_BASE_URL + IMAGE_SIZE + posterPath;
    }

    // Helper function to filter videos from api to YouTube only
    private List<VideoResult> FilterYouTubeVideos(List<VideoResult> videoResults) {

        List<VideoResult> videoResults_youtube = videoResults;
        int position = 0;

        for (VideoResult temp : videoResults) {
           if (temp.getSite() == getString(R.string.movie_site_youtube)) {
               videoResults_youtube.add(position, temp);
               position = position + 1;
           }
        }
        
        return videoResults_youtube;
    }

    // The ListView used in fragment_movie_detail.xml is nested
    // into a ScrollView in order to avoid scrolling within the ListView
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = listView.getPaddingTop() + listView.getPaddingBottom();
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            if (listItem instanceof ViewGroup) {
                listItem.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

}
