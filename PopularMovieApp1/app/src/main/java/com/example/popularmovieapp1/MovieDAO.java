package com.example.popularmovieapp1;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {


  @Query("SELECT * FROM Movie WHERE IsFavourite = 1")
  LiveData<List<Movie>> getAllFavouriteMovies();

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  void InsertMovie(Movie movie);

  @Update(onConflict = OnConflictStrategy.REPLACE)
  void updateMovie(Movie movie);

  @Delete
  void DeleteMovie(Movie movie);

  @Query("SELECT * FROM Movie WHERE Id = :id")
  Movie getAllMovies(int id);
}
