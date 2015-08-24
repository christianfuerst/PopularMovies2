package com.cf.popularmovies;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.cf.popularmovies.API.MovieAPI;
import com.cf.popularmovies.model.MoviePage;
import com.cf.popularmovies.model.MovieResult;
import com.cf.popularmovies.provider.movie.MovieColumns;
import com.cf.popularmovies.provider.movie.MovieCursor;

import java.util.ArrayList;
import java.util.List;

import retrofit.RestAdapter;
import retrofit.RetrofitError;

public class MovieFragment extends Fragment {

    private static final String KEY_RESULT_DATA = "result_data";
    private static final String KEY_SELECTED_ITEM_POSITION = "selected_item_position";
    public static final String KEY_API_ERROR = "api_error";

    private GridView gridView;

    private List<MovieResult> result_data = null;
    private int selected_item_position;
    private TextView textView_no_movies_found;
    private Snackbar snackbar;

    private MovieResultAdapter movieResultAdapter;
    private Boolean isTaskRunning;
    private String sort_by;
    private String api_error;
    private Cursor cursor;

    public interface Callback {
        public void OnItemSelected(MovieResult movieResult, String sort_by);
        public void OnNoMoviesFound();
    }

    public MovieFragment() {
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Data will be stored in ParcelableArrayList in order to prevent another API call
        outState.putParcelableArrayList(KEY_RESULT_DATA, new ArrayList<>(result_data));
        outState.putString(KEY_API_ERROR,api_error);

        if (selected_item_position != GridView.INVALID_POSITION) {
            outState.putInt(KEY_SELECTED_ITEM_POSITION, selected_item_position);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        // If the user changed sort_by preferences get new results
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String sort_by = sharedPreferences
                .getString(getString(R.string.preferences_sort_by_key),
                        getString(R.string.preferences_sort_by_default_value));

        if (sort_by != null && !this.sort_by.equals(sort_by)) {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sort_by);
        }

        this.sort_by = sort_by;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        gridView = (GridView) getActivity().findViewById(R.id.gridView_movie_poster);
        textView_no_movies_found = (TextView) getActivity().findViewById(R.id.textView_no_movies_found);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        // Get sort_by preference from SharedPreferences
        sort_by = sharedPreferences
                .getString(getString(R.string.preferences_sort_by_key),
                        getString(R.string.preferences_sort_by_default_value));

        // If an InstanceState is present, we don't have to do another API call in order to reduce network traffic
        if (savedInstanceState != null) {
            // Retrieve result data from ParcelableArrayList
            result_data = savedInstanceState.getParcelableArrayList(KEY_RESULT_DATA);
            selected_item_position = savedInstanceState.getInt(KEY_SELECTED_ITEM_POSITION);
            api_error = savedInstanceState.getString(KEY_API_ERROR);

            if (api_error != null) {
                snackbar = Snackbar.make(getView(), api_error, Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction(getString(R.string.button_reload), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                        fetchMoviesTask.execute(sort_by);
                    }
                });

                snackbar.show();
            } else {
                // Use the custom adapter to the result data visible in the GUI
                if (result_data.size() != 0) {
                    movieResultAdapter = new MovieResultAdapter(getActivity(), R.layout.item_gridview_movie_poster, result_data);
                    gridView.setAdapter(movieResultAdapter);
                }
                else
                {
                    textView_no_movies_found.setVisibility(TextView.VISIBLE);
                }
            }
        }
        else
        // If no InstanceState is present, start the custom AsyncTask in order to retrieve data from the API
        {
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute(sort_by);
        }

        // If a movie is selected MovieDetailActivity is started and result data is passed by
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MovieResult result = movieResultAdapter.getItem(position);

                selected_item_position = position;

                ((Callback) getActivity()).OnItemSelected(result, sort_by);
            }
        });

    }

    public void RefreshMovies() {
        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
        fetchMoviesTask.execute(sort_by);
    }

    private class FetchMoviesTask extends AsyncTask<String, Void, List<MovieResult>> {

        private final String API_KEY = getString(R.string.api_key);
        private String api_endpoint = "http://api.themoviedb.org/3";
        private RetrofitError retrofitError = null;

        // In order to avoid multiple AsyncTask we set isTaskRunning true first
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            isTaskRunning = true;
            api_error = null;

            textView_no_movies_found.setVisibility(TextView.GONE);

        }

        // Use RetroFit to retrieve data from API
        @Override
        protected List<MovieResult> doInBackground(String... params) {

            List<MovieResult> movieResults = new ArrayList<>();

            if (params[0].equals(getString(R.string.preferences_sort_by_offline_value))) {

                cursor = getActivity().getApplicationContext().getContentResolver()
                        .query(MovieColumns.CONTENT_URI,
                                null,
                                null,
                                null,
                                null
                        );

                MovieCursor movieCursor = new MovieCursor(cursor);

                for (movieCursor.moveToFirst(); !movieCursor.isAfterLast(); movieCursor.moveToNext()) {
                    MovieResult movieResult = new MovieResult();

                    movieResult.setId(movieCursor.getTmdbId());
                    movieResult.setTitle(movieCursor.getTitle());
                    movieResult.setOverview(movieCursor.getOverview());
                    movieResult.setReleaseDate(movieCursor.getReleaseDate());
                    movieResult.setVoteAverage(movieCursor.getVoteAverage());
                    movieResult.setBackdropPath(movieCursor.getBackdroppath());
                    movieResult.setPosterPath(movieCursor.getPosterpath());

                    movieResults.add(movieCursor.getPosition(), movieResult);
                }

                cursor.close();
                movieCursor.close();

                return movieResults;

            } else {

                MoviePage page;

                try {

                    RestAdapter restAdapter = new RestAdapter.Builder()
                            .setLogLevel(RestAdapter.LogLevel.FULL)
                            .setEndpoint(api_endpoint)
                            .build();

                    MovieAPI movieAPI = restAdapter.create(MovieAPI.class);
                    page = movieAPI.getPage(API_KEY, params[0]);

                } catch (RetrofitError e) {

                    retrofitError = e;
                    api_error = retrofitError.getMessage();
                    page = new MoviePage();

                }

                movieResults = page.getResults();

            }

            return movieResults;
        }

        // After we retrieved data from API it's time to handle the GUI
        @Override
        protected void onPostExecute(List<MovieResult> results) {
            super.onPostExecute(results);

            result_data = results;

            // Use the custom adapter to the result data visible in the GUI
            movieResultAdapter = new MovieResultAdapter(getActivity(), R.layout.item_gridview_movie_poster, result_data);
            gridView.setAdapter(movieResultAdapter);

            if (selected_item_position != GridView.INVALID_POSITION) {
                gridView.setSelection(selected_item_position);
            }

            // If the API call didn't work out as expected Snackbar will appear
                if (retrofitError != null) {

                snackbar = Snackbar.make(getView(), retrofitError.getMessage(), Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction(getString(R.string.button_reload), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!isTaskRunning) {
                            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
                            fetchMoviesTask.execute(sort_by);
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

            if (results.size() == 0) {
                textView_no_movies_found.setVisibility(TextView.VISIBLE);

                if (MovieActivity.twopanes) {
                    ((Callback) getActivity()).OnNoMoviesFound();
                }
            }

            // AsyncTask is done, so we set isTaskRunning back to false
            isTaskRunning = false;
        }
    }

}
