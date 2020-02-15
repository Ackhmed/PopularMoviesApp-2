package com.example.popularmovieapp1;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

public class MovieViewModel extends AndroidViewModel {

    private MovieDAO movieDAO;
    private LiveData<List<Movie>> movieLiveList;

    public MovieViewModel(@NonNull Application application) {
        super(application);

        movieDAO = MovieRoomDatabase.getInstance(application).MovieDao();
        movieLiveList = movieDAO.getAllFavouriteMovies();
}
public LiveData<List<Movie>> getMovieLiveList(){
        return movieLiveList;
}

}
