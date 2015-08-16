package com.cf.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cf.popularmovies.API.ReviewAPI;
import com.cf.popularmovies.API.VideoAPI;
import com.cf.popularmovies.model.MovieResult;
import com.cf.popularmovies.model.ReviewPage;
import com.cf.popularmovies.model.ReviewResult;
import com.cf.popularmovies.model.Video;
import com.cf.popularmovies.model.VideoResult;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MovieDetailFragment extends Fragment {

    private static final String KEY_VIDEO_RESULT_DATA = "video_result_data";
    private static final String KEY_REVIEW_RESULT_DATA = "review_result_data";
    private LinearLayout linear_listView_video;
    private LinearLayout linear_listView_review;

    private MovieResult result;
    private List<VideoResult> video_result_data = null;
    private List<ReviewResult> review_result_data = null;
    private Boolean isTaskRunning;

    private TextView textView_movie_title;
    private TextView textView_release_date;
    private TextView textView_vote_average;
    private TextView textView_details;
    private TextView textView_video_unavailable;
    private TextView textView_review_unavailable;
    private ImageView imageView_movie_header;
    private ImageView imageView_movie_poster_small;

    public MovieDetailFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // VideoResult & Review data will be stored in ParcelableArrayList in order to prevent another API call
        outState.putParcelableArrayList(KEY_VIDEO_RESULT_DATA, new ArrayList<>(video_result_data));
        outState.putParcelableArrayList(KEY_REVIEW_RESULT_DATA, new ArrayList<>(review_result_data));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        textView_movie_title = (TextView) getActivity().findViewById(R.id.textView_movie_title);
        textView_release_date = (TextView) getActivity().findViewById(R.id.textView_release_date);
        textView_vote_average = (TextView) getActivity().findViewById(R.id.textView_vote_average);
        textView_details = (TextView) getActivity().findViewById(R.id.textView_details);
        textView_video_unavailable = (TextView) getActivity().findViewById(R.id.textView_video_unavailable);
        textView_review_unavailable = (TextView) getActivity().findViewById(R.id.textView_review_unavailable);
        imageView_movie_header = (ImageView) getActivity().findViewById(R.id.imageView_movie_header);
        imageView_movie_poster_small = (ImageView) getActivity().findViewById(R.id.imageView_movie_poster_small);
        linear_listView_video = (LinearLayout) getActivity().findViewById(R.id.linear_listView_video);
        linear_listView_review = (LinearLayout) getActivity().findViewById(R.id.linear_listView_review);

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
            review_result_data = savedInstanceState.getParcelableArrayList(KEY_REVIEW_RESULT_DATA);

            // Use the custom adapter to the result data visible in the GUI
            ShowVideoResults(video_result_data);
            ShowReviewResults(review_result_data);
        }
        else
        // If no InstanceState is present, start the custom AsyncTask in order to retrieve data from the API
        {
            FetchMovieDataTask fetchMovieDataTask = new FetchMovieDataTask();
            fetchMovieDataTask.execute(result.getId().toString());
        }

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

    private class FetchMovieDataTask extends AsyncTask<String, Void, MovieData> {

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
        protected MovieData doInBackground(String... params) {

            MovieData movieData = new MovieData();
            Video video;
            ReviewPage reviewPage;

            try {

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint(api_endpoint)
                        .build();

                VideoAPI videoAPI = restAdapter.create(VideoAPI.class);
                video = videoAPI.getVideo(API_KEY, params[0]);

                ReviewAPI reviewAPI = restAdapter.create(ReviewAPI.class);
                reviewPage = reviewAPI.getPage(API_KEY, params[0]);

            } catch (RetrofitError e) {

                retrofitError = e;
                video = new Video();
                reviewPage = new ReviewPage();

            }

            movieData.videoResults = video.getResults();
            movieData.reviewResults = reviewPage.getResults();

            return movieData;
        }

        @Override
        protected void onPostExecute(MovieData movieData) {
            super.onPostExecute(movieData);

            video_result_data = FilterYouTubeVideos(movieData.videoResults);
            review_result_data = movieData.reviewResults;
            Snackbar snackbar = null;

            // Use the custom adapter to the result data visible in the GUI
            ShowVideoResults(video_result_data);
            ShowReviewResults(review_result_data);

            if (video_result_data.isEmpty()) {
                textView_video_unavailable.setVisibility(View.VISIBLE);
            }

            if (review_result_data.isEmpty()) {
                textView_review_unavailable.setVisibility(View.VISIBLE);
            }

            // If the API call didn't work out as expected Snackbar will appear
            if (retrofitError != null) {

                snackbar = Snackbar.make(getView(), retrofitError.getMessage(), Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction(getString(R.string.button_reload), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isTaskRunning) {
                            textView_video_unavailable.setVisibility(View.GONE);
                            textView_review_unavailable.setVisibility(View.GONE);

                            FetchMovieDataTask fetchMovieDataTask = new FetchMovieDataTask();
                            fetchMovieDataTask.execute(result.getId().toString());
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

    // Helper function to show a list of available videos
    public void ShowVideoResults(List<VideoResult> video_result_data) {

        for (int i = 0; i < video_result_data.size(); i++) {

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getActivity().getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_listview_video, null);

            TextView textView_video_name = (TextView)view.findViewById(R.id.textView_video_name);
            final String name = video_result_data.get(i).getName();
            final String youtube_video_id = video_result_data.get(i).getKey();
            textView_video_name.setText(name);

            linear_listView_video.addView(view);

            linear_listView_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

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

    }

    // Helper function to show a list of available reviews
    public void ShowReviewResults(List<ReviewResult> review_result_data) {

        for (int i = 0; i < review_result_data.size(); i++) {

            LayoutInflater layoutInflater;
            layoutInflater = (LayoutInflater) getActivity().getApplicationContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_listview_review, null);

            TextView textView_review_author = (TextView)view.findViewById(R.id.textView_review_author);
            TextView textView_review_content = (TextView)view.findViewById(R.id.textView_review_content);
            final String author = review_result_data.get(i).getAuthor();
            final String content = review_result_data.get(i).getContent();
            textView_review_author.setText(getString(R.string.details_movie_review_by) + " " + author);
            textView_review_content.setText(Html.fromHtml(content));

            linear_listView_review.addView(view);

        }

    }

    public class MovieData {

        private List<VideoResult> videoResults = new ArrayList<>();
        private List<ReviewResult> reviewResults = new ArrayList<>();

    }

}
