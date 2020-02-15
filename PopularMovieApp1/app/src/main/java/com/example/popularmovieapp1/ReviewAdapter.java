package com.example.popularmovieapp1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{
    private Context mContext;
    private List<Review> mReviewList;

    public ReviewAdapter(Context mContext, List<Review> mReviewList) {
        this.mContext = mContext;
        this.mReviewList = mReviewList;
    }

    @NonNull
    @Override
    public ReviewAdapter.ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.review_layout, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ReviewViewHolder holder, int position) {
       Review currentReview = mReviewList.get(position);
        String MovieReviewContent = currentReview.getContent();
        String MovieReviewAuthor = currentReview.getAuthor();
        holder.ReviewContentTV.setText(MovieReviewContent);
        holder.AuthorTV.setText(MovieReviewAuthor);
    }

    @Override
    public int getItemCount() {
        return mReviewList.size();
    }

    public class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView ReviewContentTV;
        TextView AuthorTV;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            ReviewContentTV = itemView.findViewById(R.id.Review_Content_TV);
            AuthorTV = itemView.findViewById(R.id.Author_TV);
        }
    }
}
