package com.lorvent.project.telgujukebox.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lorvent.project.telgujukebox.R;
import com.lorvent.project.telgujukebox.model.Review;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;




public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {

    private static ArrayList<Review> mReview;
    private Context mContext;

    public MovieReviewAdapter(Context context,ArrayList<Review> reviews) {

        mContext = context;
        mReview=reviews;
    }
    @Override
    public MovieReviewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View moviesView = inflater.inflate(R.layout.item_movie_and_tv_show_review, parent, false);

        MovieReviewAdapter.ViewHolder viewHolder = new MovieReviewAdapter.ViewHolder(context, moviesView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MovieReviewAdapter.ViewHolder viewHolder, int position) {
        Review currentReview = mReview.get(position);

        viewHolder.expTv1.setText(currentReview.getMovieReviewContent().trim());
        viewHolder.movieReviewAuthorTextView.setText(currentReview.getMovieReviewAuthor().trim());
    }

    @Override
    public int getItemCount() {
        return mReview.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final TextView movieReviewAuthorTextView;
        public final ExpandableTextView expTv1;
        private Context context;
        public ViewHolder(Context context, View itemView) {
            super(itemView);
            expTv1 = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            movieReviewAuthorTextView = (TextView) itemView.findViewById(R.id.review_author);
            this.context = context;
        }
    }
}
