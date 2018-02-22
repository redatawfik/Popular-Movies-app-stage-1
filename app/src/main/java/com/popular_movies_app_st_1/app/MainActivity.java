package com.popular_movies_app_st_1.app;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.popular_movies_app_st_1.app.adapter.MovieAdapter;
import com.popular_movies_app_st_1.app.model.Movie;
import com.popular_movies_app_st_1.app.utilities.JsonUtils;
import com.popular_movies_app_st_1.app.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

import static android.content.Intent.EXTRA_INDEX;
import static android.content.Intent.EXTRA_REFERRER;
import static com.popular_movies_app_st_1.app.utilities.NetworkUtils.getResponseFromHttpUrl;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    Context context = this;

    Movie[] moviesArray;

    RecyclerView mRecyclerView;
    MovieAdapter mMovieAdapter;
    ProgressBar progressBar;
    TextView errorMessageTextView;

    private static final String TOP_RATED = "top_rated";
    private static final String MOST_POPULAR = "popular";
    private static final String UPCOMING = "upcoming";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.rv_movies);
        progressBar = findViewById(R.id.pb_loading);
        errorMessageTextView = findViewById(R.id.tv_error_message);

        loadMoviesData(MOST_POPULAR);
    }


    boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        return isConnected;
    }


    void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        errorMessageTextView.setVisibility(View.VISIBLE);
    }

    void showMoviesList() {
        mRecyclerView.setVisibility(View.VISIBLE);
        errorMessageTextView.setVisibility(View.INVISIBLE);
    }

    void loadMoviesData(String sortBy) {
        if (checkInternetConnection()) {
            showMoviesList();
        } else showErrorMessage();

        URL url = NetworkUtils.buildUrl(sortBy);
        new MoviesAsyncTask().execute(url);
    }

    void setRecyclerView() {


        int numberOfColumn = 2;
        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumn);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(mMovieAdapter);

    }

    @Override
    public void onClick(int position) {

        String originalTitle = moviesArray[position].getOriginalTitle();
        String overview = moviesArray[position].getOverview();
        String posterImage = moviesArray[position].getPosterImage();
        double votRange = moviesArray[position].getVotRange();
        String releaseDate = moviesArray[position].getReleaseDate();
        String backdropPath = moviesArray[position].getBackdropPath();


        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("originalTitle", originalTitle);
        intent.putExtra("overview", overview);
        intent.putExtra("posterImage", posterImage);
        intent.putExtra("votRange", votRange);
        intent.putExtra("releaseDate", releaseDate);
        intent.putExtra("backdropPath", backdropPath);
        startActivity(intent);
    }


    private class MoviesAsyncTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(URL... urls) {
            String requestResult = null;
            try {
                requestResult = getResponseFromHttpUrl(urls[0]);
            } catch (IOException e) {
                Log.e(TAG, "AsyncTask error : + " + e);

            }
            return requestResult;
        }

        @Override
        protected void onPostExecute(String s) {
            moviesArray = JsonUtils.getMoviesArray(s);
            mMovieAdapter = new MovieAdapter(moviesArray, context, (MovieAdapter.MovieAdapterOnClickHandler) context);
            progressBar.setVisibility(View.INVISIBLE);
            setRecyclerView();


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.movies_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.most_popular_action) {

            mMovieAdapter.setMoviesArray(null);
            loadMoviesData(MOST_POPULAR);
            return true;

        } else if (itemId == R.id.top_rated_action) {

            mMovieAdapter.setMoviesArray(null);
            loadMoviesData(TOP_RATED);
            return true;

        } else if (itemId == R.id.upcoming_action) {

            mMovieAdapter.setMoviesArray(null);
            loadMoviesData(UPCOMING);
            return true;

        } else {
            return super.onOptionsItemSelected(item);
        }


    }
}