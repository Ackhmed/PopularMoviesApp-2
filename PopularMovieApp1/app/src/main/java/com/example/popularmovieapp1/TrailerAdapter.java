package com.example.popularmovieapp1;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Context mContext;
    private List<Trailer> mTrailerList;
    private ListItemClickListener itemClickListener;

    public interface ListItemClickListener {
        void onListItemClick(int itemClicked);
    }

    public TrailerAdapter(Context mContext, List<Trailer> mTrailerList, ListItemClickListener itemClickListener) {
        this.mContext = mContext;
        this.mTrailerList = mTrailerList;
        this.itemClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public TrailerAdapter.TrailerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.trailer_layout, parent, false);
        return new TrailerViewHolder(view, itemClickListener);
    }


    @Override
    public void onBindViewHolder(@NonNull TrailerViewHolder holder, int position) {
        Trailer currentTrailer = mTrailerList.get(position);
        String MovieTitle = currentTrailer.getName();
        holder.YoutubeTitleTV.setText(MovieTitle);

    }


    @Override
    public int getItemCount() {
        return mTrailerList.size();
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView YoutubeIconIV;
        TextView YoutubeTitleTV;

        public TrailerViewHolder(@NonNull View itemView, ListItemClickListener trailerListener) {
            super(itemView);
            YoutubeIconIV = itemView.findViewById(R.id.Trailer_MovieImage_IV);
            YoutubeTitleTV = itemView.findViewById(R.id.TrailerTitle_TV);
            itemClickListener = trailerListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onListItemClick(getAdapterPosition());

        }
    }
}
