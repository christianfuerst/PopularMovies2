package com.cf.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.cf.popularmovies.provider.movie.MovieColumns;
import com.cf.popularmovies.provider.movie.MovieContentValues;
import com.cf.popularmovies.provider.movie.MovieCursor;
import com.cf.popularmovies.provider.movie.MovieSelection;
import com.cf.popularmovies.provider.review.ReviewColumns;
import com.cf.popularmovies.provider.review.ReviewContentValues;
import com.cf.popularmovies.provider.review.ReviewCursor;
import com.cf.popularmovies.provider.review.ReviewSelection;
import com.cf.popularmovies.provider.video.VideoColumns;
import com.cf.popularmovies.provider.video.VideoContentValues;
import com.cf.popularmovies.provider.video.VideoCursor;
import com.cf.popularmovies.provider.video.VideoSelection;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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

    private boolean isTaskRunning;
    private boolean isFavorite;

    private TextView textView_movie_title;
    private TextView textView_release_date;
    private TextView textView_vote_average;
    private TextView textView_details;
    private TextView textView_video_unavailable;
    private TextView textView_review_unavailable;
    private ImageView imageView_movie_header;
    private ImageView imageView_movie_poster_small;
    private Button button_favorite;
    Cursor cursor;

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
        button_favorite = (Button) getActivity().findViewById(R.id.button_favorite);

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

        // Initialize favorite button until FetchMovieDataTask is done
        button_favorite.setClickable(false);
        button_favorite.setText(getText(R.string.details_movie_favorite_loading));

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

        // OnClickListener for favorite button in order to add / remove movie to favorites
        button_favorite.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (button_favorite.getText()
                        .equals(getString(R.string.details_movie_favorite_add))) {
                    AddFavoriteTask addFavoriteTask = new AddFavoriteTask();
                    addFavoriteTask.execute();
                    isFavorite = true;
                }
                else
                {
                    RemoveFavoriteTask removeFavoriteTask = new RemoveFavoriteTask();
                    removeFavoriteTask.execute();
                    isFavorite = false;
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

    // Background task to fetch movie data and update the gui
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

            isFavorite = IsFavorite(result.getId());

            try {

                RestAdapter restAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setEndpoint(api_endpoint)
                        .build();

                VideoAPI videoAPI = restAdapter.create(VideoAPI.class);
                video = videoAPI.getVideo(API_KEY, params[0]);

                ReviewAPI reviewAPI = restAdapter.create(ReviewAPI.class);
                reviewPage = reviewAPI.getPage(API_KEY, params[0]);

                movieData.movieResult = result;
                movieData.videoResults = video.getResults();
                movieData.reviewResults = reviewPage.getResults();

                // If selected movie is a favorite, local storage is updated
                if (isFavorite) {
                    RemoveFavorite(result.getId());
                    AddFavorite(movieData);
                }

            } catch (RetrofitError e) {

                // If api call fails and use local storage otherwise prepare error message
                if (isFavorite) {
                    movieData = GetFavorite(result.getId());
                } else {
                    retrofitError = e;
                    video = new Video();
                    reviewPage = new ReviewPage();

                    movieData.movieResult = result;
                    movieData.videoResults = video.getResults();
                    movieData.reviewResults = reviewPage.getResults();
                }

            }

            return movieData;
        }

        @Override
        protected void onPostExecute(MovieData movieData) {
            super.onPostExecute(movieData);

            video_result_data = FilterYouTubeVideos(movieData.videoResults);
            review_result_data = movieData.reviewResults;
            Snackbar snackbar = null;

            // Update favorite Button
            if (isFavorite) {
                button_favorite.setText(getText(R.string.details_movie_favorite_remove));
                button_favorite.setClickable(true);
            } else {
                button_favorite.setText(getText(R.string.details_movie_favorite_add));
                button_favorite.setClickable(true);
            }

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

    // Background task to store movie data and images into local file system
    public class AddFavoriteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            MovieData movieData = new MovieData();

            movieData.movieResult = result;
            movieData.videoResults = video_result_data;
            movieData.reviewResults = review_result_data;

            AddFavorite(movieData);

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            button_favorite.setText(getString(R.string.details_movie_favorite_loading));
            button_favorite.setClickable(false);

            Snackbar.make(getView(), result.getTitle() + " "
                    + getString(R.string.details_movie_favorite_added),
                    Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            button_favorite.setText(getString(R.string.details_movie_favorite_remove));
            button_favorite.setClickable(true);
        }
    }

    // Background task to store movie data and images into local file system
    public class RemoveFavoriteTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {

            RemoveFavorite(result.getId());

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            button_favorite.setText(getString(R.string.details_movie_favorite_loading));
            button_favorite.setClickable(false);

            Snackbar.make(getView(), result.getTitle() + " "
                    + getString(R.string.details_movie_favorite_removed),
                    Snackbar.LENGTH_LONG).show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            button_favorite.setText(getString(R.string.details_movie_favorite_add));
            button_favorite.setClickable(true);
        }
    }

    // Helper function to construct image URLs
    private String getImageURL(String posterPath) {

        if (posterPath.contains(getActivity().getApplicationContext().getPackageName())) {
            return posterPath;
        } else {
            final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
            final String IMAGE_SIZE = "w342";

            return IMAGE_BASE_URL + IMAGE_SIZE + posterPath;
        }
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
                        Intent intent = new Intent(Intent.ACTION_VIEW,
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

    // Helper function to retrieve movie data from offline storage
    public MovieData GetFavorite(int tmdb_id) {
        MovieData movieData = new MovieData();

        MovieSelection movieSelection = new MovieSelection();
        movieSelection.tmdbId(tmdb_id);
        cursor = getActivity().getApplicationContext().getContentResolver()
                .query(MovieColumns.CONTENT_URI,
                        null,
                        movieSelection.sel(),
                        movieSelection.args(),
                        null
                );

        MovieCursor movieCursor = new MovieCursor(cursor);

        // Retrieve the _id for the movie from SQLite table (exactly one row expected)
        movieCursor.moveToNext();
        Long movie_id = movieCursor.getId();

        // Query the db for video and review records for this particular movie
        // and put data into video_result_data and review_result_data
        VideoSelection videoSelection = new VideoSelection();
        videoSelection.movieId(movie_id);
        cursor = getActivity().getApplicationContext().getContentResolver()
                .query(VideoColumns.CONTENT_URI,
                        null,
                        videoSelection.sel(),
                        videoSelection.args(),
                        null
                );
        VideoCursor videoCursor = new VideoCursor(cursor);

        for (videoCursor.moveToFirst(); !videoCursor.isAfterLast(); videoCursor.moveToNext()) {
            VideoResult videoResult = new VideoResult();
            videoResult.setKey(videoCursor.getKey());
            videoResult.setName(videoCursor.getName());
            videoResult.setSite(videoCursor.getSite());
            movieData.videoResults.add(videoCursor.getPosition(), videoResult);
        }

        ReviewSelection reviewSelection = new ReviewSelection();
        reviewSelection.movieId(movie_id);
        cursor = getActivity().getApplicationContext().getContentResolver()
                .query(ReviewColumns.CONTENT_URI,
                        null,
                        reviewSelection.sel(),
                        reviewSelection.args(),
                        null
                );
        ReviewCursor reviewCursor = new ReviewCursor(cursor);

        for (reviewCursor.moveToFirst(); !reviewCursor.isAfterLast(); reviewCursor.moveToNext()) {
            ReviewResult reviewResult = new ReviewResult();
            reviewResult.setAuthor(reviewCursor.getAuthor());
            reviewResult.setContent(reviewCursor.getContent());
            movieData.reviewResults.add(reviewCursor.getPosition(), reviewResult);
        }

        return movieData;
    }

    // Helper function to check if selected movie is a favorite
    public boolean IsFavorite(int tmdb_id) {

        // Check if the movie is marked as favorite in order to use local database and prevent api call
        MovieSelection movieSelection = new MovieSelection();
        movieSelection.tmdbId(tmdb_id);
        cursor = getActivity().getApplicationContext().getContentResolver()
                .query(MovieColumns.CONTENT_URI,
                        null,
                        movieSelection.sel(),
                        movieSelection.args(),
                        null
                );

        MovieCursor movieCursor = new MovieCursor(cursor);

        if (movieCursor.getCount() == 1) {
            return true;
        }  else {
            return false;
        }

    }

    // Helper function to add movie data into offline storage
    public void AddFavorite(MovieData movieData) {

        MovieResult result = movieData.movieResult;
        List<VideoResult> video_result_data = movieData.videoResults;
        List<ReviewResult> review_result_data = movieData.reviewResults;

        MovieContentValues movieContentValues = new MovieContentValues();
        movieContentValues.putTmdbId(result.getId());
        movieContentValues.putTitle(result.getTitle());
        movieContentValues.putOverview(result.getOverview());
        movieContentValues.putReleaseDate(result.getReleaseDate());
        movieContentValues.putVoteAverage(result.getVoteAverage());

        if (result.getBackdropPath() != null && !result.getBackdropPath().isEmpty()) {
            String url_backdrop = result.getBackdropPath();
            Bitmap bitmap_backdrop = urlImageToBitmap(getImageURL(url_backdrop));
            String path_backdrop = SaveBitmapToInternalStorage(bitmap_backdrop,
                    getString(R.string.internal_storage_backdrop_image_path),
                    result.getBackdropPath().substring(1));

            movieContentValues.putBackdroppath(path_backdrop);
        } else {
            movieContentValues.putBackdroppath(null);
        }

        if (result.getPosterPath() != null && !result.getPosterPath().isEmpty()) {
            String url_poster = result.getPosterPath();
            Bitmap bitmap_poster = urlImageToBitmap(getImageURL(url_poster));
            String path_poster = SaveBitmapToInternalStorage(bitmap_poster,
                    getString(R.string.internal_storage_poster_image_path),
                    result.getPosterPath().substring(1));

            movieContentValues.putPosterpath(path_poster);
        } else {
            movieContentValues.putPosterpath(null);
        }

        getActivity().getApplicationContext().getContentResolver()
                .insert(MovieColumns.CONTENT_URI, movieContentValues.values());

        MovieSelection movieSelection = new MovieSelection();
        movieSelection.tmdbId(result.getId());
        Cursor cursor = getActivity().getApplicationContext().getContentResolver()
                .query(MovieColumns.CONTENT_URI,
                        null,
                        movieSelection.sel(),
                        movieSelection.args(),
                        null
                );

        MovieCursor movieCursor = new MovieCursor(cursor);
        movieCursor.moveToNext();
        Long movie_id = movieCursor.getId();

        for (int i = 0; i < video_result_data.size(); i++) {
            VideoContentValues videoContentValues = new VideoContentValues();
            videoContentValues.putMovieId(movie_id);
            videoContentValues.putKey(video_result_data.get(i).getKey());
            videoContentValues.putName(video_result_data.get(i).getName());
            videoContentValues.putSite(video_result_data.get(i).getSite());
            getActivity().getApplicationContext().getContentResolver()
                    .insert(VideoColumns.CONTENT_URI, videoContentValues.values());
        }

        for (int i = 0; i < review_result_data.size(); i++) {
            ReviewContentValues reviewContentValues = new ReviewContentValues();
            reviewContentValues.putMovieId(movie_id);
            reviewContentValues.putAuthor(review_result_data.get(i).getAuthor());
            reviewContentValues.putContent(review_result_data.get(i).getContent());
            getActivity().getApplicationContext().getContentResolver()
                    .insert(ReviewColumns.CONTENT_URI, reviewContentValues.values());
        }
    }

    // Helper function to remove movie from offline storage
    public void RemoveFavorite(int tmdb_id) {

        MovieSelection movieSelection = new MovieSelection();
        movieSelection.tmdbId(tmdb_id);

        Cursor cursor = getActivity().getApplicationContext().getContentResolver()
                .query(MovieColumns.CONTENT_URI,
                        null,
                        movieSelection.sel(),
                        movieSelection.args(),
                        null
                );

        MovieCursor movieCursor = new MovieCursor(cursor);
        movieCursor.moveToNext();

        if (movieCursor.getBackdroppath() != null && !movieCursor.getBackdroppath().isEmpty()) {
            String backdropPath = movieCursor.getBackdroppath();
            File backdropFile = new File(backdropPath);
            backdropFile.delete();
        }

        if (movieCursor.getPosterpath() != null && !movieCursor.getPosterpath().isEmpty()) {
            String posterPath = movieCursor.getPosterpath();
            File posterFile = new File(posterPath);
            posterFile.delete();
        }

        movieSelection.delete(getActivity().getApplicationContext().getContentResolver());

    }

    // Helper function to save bitmaps into local storage
    public String SaveBitmapToInternalStorage(Bitmap bitmap, String directory, String filename) {
        ContextWrapper contextWrapper = new ContextWrapper(getActivity().getApplicationContext());

        File dir = contextWrapper.getDir(directory, Context.MODE_PRIVATE);
        File path = new File(dir, filename);

        FileOutputStream fileOutputStream;

        try {

            fileOutputStream = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return path.getAbsolutePath();
    }

    // Helper function to retrieve bitmaps from local storage
    public Bitmap LoadBitmapFromInternalStorage(String path) {
        try  {
            File file = new File(path);
            Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            return bitmap;
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    // Helper function to retrieve an image from url and convert it to Bitmap
    public Bitmap urlImageToBitmap(String urlImage){
        Bitmap bitmap = null;
        URL url = null;
        try {
            url = new URL(urlImage);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (url != null) {
            try {
                bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    public class MovieData {

        private MovieResult movieResult = new MovieResult();
        private List<VideoResult> videoResults = new ArrayList<>();
        private List<ReviewResult> reviewResults = new ArrayList<>();

    }

}
