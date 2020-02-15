package com.example.popularmovieapp1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity implements TrailerAdapter.ListItemClickListener {
    public static final String FAVOURITES_SHARED_PREFERENCES = "FAVOURITES_SHARED_PREFERENCES";
    private static final String TAG = "DetailActivity";

    //TODO ADD YOUR API KEY HERE
    private static final String API_KEY = "";
    public static int Movie_id;
    Movie movie;
    CheckBox Favourites_CheckBox;
    //ROOM Logic
    MovieRoomDatabase MovieRoomDB;
    SharedPreferences sharedPreferences;
    private RecyclerView trailer_RecyclerView;
    private RecyclerView review_RecyclerView;
    private boolean IsFavourite;
    private TrailerAdapter trailerAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Trailer> trailerList;
    private List<Review> reviewList;
    private int PAGES = 1;
    private Call<TrailerResults> call;

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
                    Log.i("update_status", "" + e.getMessage());
                }
            }
        }
        Log.i("update_status", "Network is available : FALSE ");
        return false;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        MovieRoomDB = MovieRoomDatabase.getInstance(this);

        //Getting the Movie ID
        Movie_id = getIntent().getIntExtra("id", 419704);

        review_RecyclerView = findViewById(R.id.Review_Recycler_View);
        review_RecyclerView.setLayoutManager(new LinearLayoutManager(this));

        trailer_RecyclerView = findViewById(R.id.Trailer_Recycler_View);
        trailer_RecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        final MovieInterface movieInterface = MovieClient.getClient().create(MovieInterface.class);
        Call<TrailerResults> call = movieInterface.getTrailers(Movie_id, API_KEY, "en-US");
        Call<ReviewResults> callReview = movieInterface.getReviews(Movie_id, API_KEY, "en-US", PAGES);

//Call for Reviews
        if (!isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this, "There is NO INTERNET CONNECTION", Toast.LENGTH_SHORT);
            toast.show();
        } else if (API_KEY == "") {
            Toast toast = Toast.makeText(this, "There is NO API KEY", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            callReview.enqueue(new Callback<ReviewResults>() {
                @Override
                public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: REVIEW CALL IS MADE");
                        reviewList = response.body().getResults();
                        reviewAdapter = new ReviewAdapter(DetailActivity.this, reviewList);
                        review_RecyclerView.setAdapter(reviewAdapter);
                    }
                }

                @Override
                public void onFailure(Call<ReviewResults> call, Throwable t) {
                }
            });
        }

//Call for Trailers
        if (!isNetworkAvailable(this)) {
            Toast toast = Toast.makeText(this, "There is NO INTERNET CONNECTION", Toast.LENGTH_SHORT);
            toast.show();
        } else if (API_KEY == "") {
            Toast toast = Toast.makeText(this, "There is NO API KEY", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            call.enqueue(new Callback<TrailerResults>() {
                @Override
                public void onResponse(Call<TrailerResults> call, Response<TrailerResults> response) {
                    if (response.isSuccessful()) {
                        Log.d(TAG, "onResponse: TRAILER CALL IS MADE");
                        trailerList = response.body().getResults();
                        trailerAdapter = new TrailerAdapter(DetailActivity.this, trailerList, DetailActivity.this);
                        trailer_RecyclerView.setAdapter(trailerAdapter);
                    }
                }

                @Override
                public void onFailure(Call<TrailerResults> call, Throwable t) {
                }
            });
        }

        //Setting up the Favourites Checkbox
        Favourites_CheckBox = findViewById(R.id.checkbox_star);
        Favourites_CheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, final boolean isChecked) {
                movie.setIsFavourite(isChecked);
                AppExecutors.getInstance().diskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (isChecked) {
                            MovieRoomDB.MovieDao().InsertMovie(movie);
                        } else {
                            MovieRoomDB.MovieDao().DeleteMovie(movie);

                        }
                    }
                });
            }
        });

        movie = (Movie) getIntent().getSerializableExtra("movie");
        //Setting up a method to change the state of the checkbox
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                final Movie moviesByID = MovieRoomDB.MovieDao().getAllMovies(movie.getId());
                CheckboxCheckedIfFav(moviesByID != null);
            }
        });


        //Setting up the GetIntents
        String poster_path = getIntent().getStringExtra("poster_path");
        String title = getIntent().getStringExtra("title");
        Double popularity = getIntent().getDoubleExtra("popularity", 2);

        IsFavourite = getIntent().getBooleanExtra("IsFavourite", false);

        String overview = getIntent().getStringExtra("overview");
        String releaseDate = getIntent().getStringExtra("releaseDate");
        double voteAverage = getIntent().getDoubleExtra("voteAverage", 2);

        //Converting the double value to a String
        String voteAvgStringValue = Double.valueOf(voteAverage).toString();
        String popularityStringValue = Double.valueOf(popularity).toString();

        //Setting all Values
        TextView voterAvgTVD = findViewById(R.id.VoteAverage_TVD);
        voterAvgTVD.setText(voteAvgStringValue);

        TextView titleTVD = findViewById(R.id.MovieTitle_TVD);
        titleTVD.setText(title);

        TextView overviewTVD = findViewById(R.id.MovieOverview_TVD);
        overviewTVD.setText(overview);

        TextView popularityTVD = findViewById(R.id.MoviePopularity_TVD);
        popularityTVD.setText(popularityStringValue);

        TextView releaseDateTVD = findViewById(R.id.MovieReleaseDate_TVD);
        releaseDateTVD.setText(releaseDate);

        ImageView imageURLDetail = findViewById(R.id.MovieImage_IVD);
        Picasso
                .get().
                load(poster_path).
                into(imageURLDetail);
    }

    private void CheckboxCheckedIfFav(boolean favourite) {
        Favourites_CheckBox.setChecked(favourite);
        Favourites_CheckBox.setHighlightColor(Favourites_CheckBox.isChecked() ?
                ContextCompat.getColor(this, R.color.colorAccent) :
                ContextCompat.getColor(this, R.color.colorPrimaryDark));
    }


    @Override
    public void onListItemClick(int itemClicked) {
        Log.d(TAG, "onListItemClick:  Movie was Clicked  " + trailerList.get(itemClicked));
        String TrailerlickedforToast = trailerList.get(itemClicked).getName();
        Toast toast = Toast.makeText(this, "You clicked on " + TrailerlickedforToast, Toast.LENGTH_SHORT);
        toast.show();

        //Intent to open the Trailer Youtube Url
        Trailer currenTrailer = trailerList.get(itemClicked);
        Intent VideoIntent = new Intent(Intent.ACTION_VIEW);
        VideoIntent.setData(Uri.parse(currenTrailer.getKey()));
        startActivityForResult(VideoIntent, 12);

    }

    //Going to MainActivity when back button pressed
    public void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

    @Override
    public void onBackPressed() {
        goToMain();
        super.onBackPressed();
    }
}




