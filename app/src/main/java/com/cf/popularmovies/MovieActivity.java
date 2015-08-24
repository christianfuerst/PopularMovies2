package com.cf.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.cf.popularmovies.model.MovieResult;


public class MovieActivity extends AppCompatActivity implements MovieFragment.Callback, MovieDetailFragment.Callback {

    private static final String MOVIE_DETAIL_FRAGMENT_TAG = "movie_detail_fragment_tag";

    public static boolean twopanes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie);

        if (findViewById(R.id.movie_detail_container) != null) {
            twopanes = true;

            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.movie_detail_container, new MovieDetailFragment())
                        .commit();
            }
        } else {
            twopanes = false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie, menu);
        return true;
    }

    @Override
    public void OnItemSelected(MovieResult movieResult, String sort_by) {
        if (MovieDetailFragment.isTaskRunning == false) {
            if (twopanes) {
                Bundle bundle = new Bundle();
                bundle.putParcelable(MovieDetailFragment.KEY_MOVIE_RESULT_DATA, movieResult);
                bundle.putString(MovieDetailFragment.KEY_SORT_BY, sort_by);

                MovieDetailFragment movieDetailFragment = new MovieDetailFragment();
                movieDetailFragment.setArguments(bundle);

                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, movieDetailFragment, MOVIE_DETAIL_FRAGMENT_TAG)
                        .commit();
            } else
            {
                Intent sendResultToMovieDetailActivity = new Intent(this, MovieDetailActivity.class)
                        .putExtra(MovieDetailFragment.KEY_MOVIE_RESULT_DATA, movieResult)
                        .putExtra(MovieDetailFragment.KEY_SORT_BY, sort_by);

                startActivity(sendResultToMovieDetailActivity);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();


        if (id == R.id.action_settings) {

            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // Callback will only be triggered from MovieDetailFragment
    // if sort_by = favorites and twopanes = true
    @Override
    public void OnFavoriteRemove() {

        // Clear detail container
        getSupportFragmentManager().beginTransaction()
                .remove(getSupportFragmentManager().findFragmentById(R.id.movie_detail_container))
                .commit();

        // Refresh movie container
        MovieFragment movieFragment = (MovieFragment)
                getSupportFragmentManager().findFragmentById(R.id.movie_container);

        movieFragment.RefreshMovies();

    }

    // Callback will only be triggered from MovieDetailFragment
    // if no movies were found and twopanes = true
    @Override
    public void OnNoMoviesFound() {

        if (getSupportFragmentManager().findFragmentById(R.id.movie_detail_container) != null)
        {
            // Clear detail container
            getSupportFragmentManager().beginTransaction()
                    .remove(getSupportFragmentManager().findFragmentById(R.id.movie_detail_container))
                    .commit();
        }

    }
}
