package com.example.popularmovieapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {
    private Context mContext;
    protected List<Movie> mMoviesList;
    private ListItemClickListener itemClickListener;

    public MovieAdapter(Context mContext, List<Movie> mMoviesList, ListItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mMoviesList = mMoviesList;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_list_item, parent, false);
        return new MovieHolder(view, itemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder holder, int position) {
        Movie currentMovie = mMoviesList.get(position);
        String imageUrl = currentMovie.getPosterURL();
        String title = currentMovie.getTitle();
        Double popularity = currentMovie.getPopularity();
        String overview = currentMovie.getOverview();
        String releaseData = currentMovie.getReleaseDate();
        holder.releaseDate.setText(releaseData);
        holder.overviewTV.setText(overview);
        holder.popularityTV.setText(String.valueOf(popularity));
        holder.titleTV.setText(title);

        //Using Picasso
        Picasso
                .get().
                load(imageUrl).
                into(holder.movieImageIV);
    }

    public interface ListItemClickListener {
        void onListItemClick(int itemClicked);
    }

    @Override
    public int getItemCount() {
        return mMoviesList.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView movieImageIV;
        TextView titleTV;
        TextView popularityTV;
        TextView overviewTV;
        TextView releaseDate;
        TextView voteAverageTV;
        ListItemClickListener movieListener;

        public MovieHolder(View itemView, ListItemClickListener movieListener) {
            super(itemView);
            movieImageIV = itemView.findViewById(R.id.MovieImage_IV);
            titleTV = (TextView) itemView.findViewById(R.id.MovieTitle_TV);
            popularityTV = (TextView) itemView.findViewById(R.id.MoviePopularity_TV);
            overviewTV = (TextView) itemView.findViewById(R.id.MovieOverview_TV);
            releaseDate = (TextView) itemView.findViewById(R.id.MovieReleaseDate_TV);
            voteAverageTV = itemView.findViewById(R.id.VoteAverage_TVD);
            this.movieListener = movieListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onListItemClick(getAdapterPosition());
        }
    }

    public void setMovieData(List<Movie> favMovs){
        mMoviesList = favMovs;

        notifyDataSetChanged();
    }




}
