package com.example.popularmovieapp1;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements MovieAdapter.ListItemClickListener {
    private static final String TAG = "MainActivity";

    //TODO ADD YOUR API KEY HERE
    private static final String API_KEY = "";
    private List<Movie> movieList;
    private MovieAdapter movieAdapter;
    private Movie movie;
    private RecyclerView movie_recycleview;
    private int PAGES = 1;
    private List<Movie> favMovieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        movie_recycleview = findViewById(R.id.Movies_RV);
        movie_recycleview.setLayoutManager(new GridLayoutManager(this, 2));
        MovieInterface movieInterface = MovieClient.getClient().create(MovieInterface.class);

        movieAdapter = new MovieAdapter(MainActivity.this, new ArrayList<Movie>(), MainActivity.this);
        movie_recycleview.setAdapter(movieAdapter);

        //If statement indicating to make a call only if there is an internet connection
        if (!isNetworkAvailable(this)) {

            Toast toast = Toast.makeText(this, "There is NO INTERNET CONNECTION", Toast.LENGTH_SHORT);
            toast.show();
        } else if (API_KEY == "") {

            Toast toast = Toast.makeText(this, "There is NO API KEY", Toast.LENGTH_SHORT);
            toast.show();
        } else {


            getPopularMovies();
        }


        //viewModelSetup
        MovieViewModel viewModel = new ViewModelProvider(MainActivity.this).get(MovieViewModel.class);
        viewModel.getMovieLiveList().observe(this, new Observer<List<Movie>>() {
            @Override
            public void onChanged(List<Movie> favMovies) {
                favMovieList = favMovies;

            }
        });
    }

    private void getPopularMovies() {
        MovieInterface movieInterface = MovieClient.getClient().create(MovieInterface.class);
        Call<MovieResults> call = movieInterface.getPopularMovies(API_KEY, "en-US", PAGES);
        call.enqueue(new Callback<MovieResults>() {
                         @Override
                         public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                             if (response.isSuccessful()) {
                                 List<Movie> movieList = response.body().getResults();
                                 Collections.sort(movieList);
                                 movieAdapter.setMovieData(movieList);
                             }
                         }

                         @Override
                         public void onFailure(Call<MovieResults> call, Throwable t) {
                         }
                     }
        );
    }

    @Override
    public void onListItemClick(int itemClicked) {
        Movie movie = movieAdapter.mMoviesList.get(itemClicked);
        Log.d(TAG, "onListItemClick:  Movie was Clicked  " + movie.getTitle());

        String MovieClickedforToast = movie.getTitle();
        Toast toast = Toast.makeText(this, "You clicked on " + MovieClickedforToast, Toast.LENGTH_SHORT);
        toast.show();

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("movie", movie);
        intent.putExtra("IsFavourite", movie.isFavourite());
        intent.putExtra("poster_path", movie.getPosterURL());
        intent.putExtra("title", movie.getTitle());
        intent.putExtra("voteAverage", movie.getVoteAverage());
        intent.putExtra("popularity", movie.getPopularity());
        intent.putExtra("overview", movie.getOverview());
        intent.putExtra("releaseDate", movie.getReleaseDate());
        intent.putExtra("id", movie.getId());
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sort, menu);
        inflater.inflate(R.menu.menu_favourite, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.sort_vote) {
            getPopularMovies();
            movieAdapter.notifyDataSetChanged();
            Toast toast = Toast.makeText(this, "Movies Sorted by Popularity", Toast.LENGTH_SHORT);
            toast.show();
            return true;
        } else {
            if (id == R.id.sort_favourite) {
                movieAdapter.setMovieData(favMovieList);
                Toast toast = Toast.makeText(this, "Movies Sorted by Fav", Toast.LENGTH_SHORT);
                toast.show();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    //Refence: https://stackoverflow.com/questions/57277759/getactivenetworkinfo-is-deprecated-in-api-29
    //Method to check for network
    public static boolean isNetworkAvailable(Context context) {
        if (context == null) return false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                        Log.i("update_status", "Network is available : true");
                        return true;
                    }
                } catch (Exception e) {
                    Log.i("update_statut", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status", "Network is available : FALSE ");
        return false;
    }


}






